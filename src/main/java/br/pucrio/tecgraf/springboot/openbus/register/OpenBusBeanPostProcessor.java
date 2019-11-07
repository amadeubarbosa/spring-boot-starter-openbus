package br.pucrio.tecgraf.springboot.openbus.register;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusAssistantAware;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusComponentAware;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusServicesRegistry;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import org.omg.PortableServer.Servant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import scs.core.ComponentContext;
import scs.core.exception.SCSException;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OpenBusBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware, OpenBusAssistantAware, OpenBusComponentAware, OpenBusServicesRegistryAware {

    private Logger log = LoggerFactory.getLogger(OpenBusBeanPostProcessor.class);

    private Assistant assistant;
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private ComponentContext componentContext;
    private OpenBusServicesRegistry openBusServiceRegistry;

    public OpenBusBeanPostProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory,
            ComponentContext componentContext,
            OpenBusServicesRegistry openBusServiceRegistry) {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
        this.componentContext = componentContext;
        this.openBusServiceRegistry = openBusServiceRegistry;
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

    private ServiceProperty[] mountServiceProperties(String name) {
        Map<String, String> serviceProperties = configurableListableBeanFactory.getBean(OpenBusPropertiesServices.class).getProperties();
        return serviceProperties.keySet().stream()
                .filter(item -> item.equals(name) || item.equals("default"))
                .map(item -> new ServiceProperty(item, serviceProperties.get(item)))
                .collect(Collectors.toList())
                .toArray(new ServiceProperty[]{});
    }

    private void scanOpenBusServiceAnnotation(Object bean, String beanName) throws SCSException {
        OpenBusService openBusServiceAnnotation = AnnotationUtils.findAnnotation(bean.getClass(), OpenBusService.class);
        if (openBusServiceAnnotation != null) {
            // Verifica se é um servant
            Servant servant = createServant(bean, openBusServiceAnnotation.servant());
            // Registra a faceta
            String name = openBusServiceAnnotation.name();
            String id = openBusServiceAnnotation.id();
            if (name.equals("default")) throw new SCSException("O nome 'default' é reservado: não pode ser utilizado para criação de serviços OpenBus");
            log.info("Adicionando serviço openbus para o bean {} (faceta: {}, id: {}, servant: {})", beanName, name, id, servant);
            componentContext.addFacet(name, id, servant);
            // Cria o conceito de oferta e propriedades e o registra como bean
            ServiceOffer serviceOffer = new DefaultServiceOffer(
                    name,
                    componentContext.getIComponent(),
                    mountServiceProperties(name)
            );
            configurableListableBeanFactory.registerSingleton(name, serviceOffer);
            // Registra o serviço para depois ser registrado no OpenBus
            openBusServiceRegistry.addService(serviceOffer);
        }


        // TODO Registrar os Servicos (        System.out.println("OS SERVICOS DEVEM SER REGISTRADOS AQUI");
        //System.out.println("Bean: " + bean + ", beanName: " + beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
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


    @Override
    public void setOpenBusAssistant(Assistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void setComponentContext(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Override
    public void setOpenBusServiceRegistry(OpenBusServicesRegistry openBusServiceRegistry) {
        this.openBusServiceRegistry = openBusServiceRegistry;
    }
}
