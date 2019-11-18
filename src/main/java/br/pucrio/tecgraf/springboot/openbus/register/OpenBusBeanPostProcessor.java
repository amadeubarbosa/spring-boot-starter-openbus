package br.pucrio.tecgraf.springboot.openbus.register;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusApplicationInstance;
import org.omg.PortableServer.Servant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import scs.core.exception.SCSException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OpenBusBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private Logger log = LoggerFactory.getLogger(OpenBusBeanPostProcessor.class);

    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private OpenBusApplicationInstance openBusApplicationInstance;

    public OpenBusBeanPostProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory,
                                    OpenBusApplicationInstance openBusApplicationInstance) {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
        this.openBusApplicationInstance = openBusApplicationInstance;
    }

    private Servant createServant(Object servantComponent, Class<? extends Servant> servantClass) throws SCSException {
        Object objectServant;
        Class<?> targetInterfaceOperations;
        Constructor constructor;
        // TODO Apenas verificar se as interfaces batem, não forçar ser a primeira da lista
        targetInterfaceOperations = ClassUtils.getAllInterfacesForClass(servantComponent.getClass())[0];

        try {
            constructor = servantClass.getConstructor(targetInterfaceOperations);
        } catch (NoSuchMethodException e) {
            throw new SCSException("Não foi possível instanciar o servant pois o servico openbus não o implementava " +
                    "ou não era A PRIMEIRA INTERFACE DA LISTA) " + servantClass, e);
        }

        try {
            objectServant = constructor.newInstance(servantComponent);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new SCSException("Não foi possível instanciar o servant " + servantClass, e);
        }
        log.debug("Objeto servant {} instanciado", servantComponent);
        return (Servant)objectServant;
    }

    private void scanOpenBusServiceAnnotation(Object bean, String beanName) throws SCSException {
        OpenBusService openBusServiceAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), OpenBusService.class);
        if (openBusServiceAnnotation != null) {
            // Verifica se é um servant
            Servant servant = createServant(bean, openBusServiceAnnotation.servant());
            // Obtém os itens adicionais ao serviço
            String name = openBusServiceAnnotation.name();
            String id = openBusServiceAnnotation.id();
            // Registra a faceta
            RemoteService remoteService = openBusApplicationInstance.addService(servant, name, id);
            // Registra um bean com o nome do serviço para a faceta
            log.info("(1) Bean = {}, {}", beanName, remoteService);
            configurableListableBeanFactory.registerSingleton(name, remoteService);
        }
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            // Registra os beans produzidos
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            // Registra os beans com anotação
            scanOpenBusServiceAnnotation(bean, beanName);
        } catch (SCSException e) {
            throw new BeanCreationException("Erro ao criar componente openBus para o bean " + beanName, e);
        }
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        configurableListableBeanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

}
