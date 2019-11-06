package br.pucrio.tecgraf.springboot.openbus.register;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class OpenBusBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    private void scanOpenBusServiceAnnotation(Object bean, String beanName) {
        // TODO Registrar os Servicos (        System.out.println("OS SERVICOS DEVEM SER REGISTRADOS AQUI");
        //System.out.println("Bean: " + bean + ", beanName: " + beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        scanOpenBusServiceAnnotation(bean, beanName);
        return bean;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        configurableListableBeanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }


}
