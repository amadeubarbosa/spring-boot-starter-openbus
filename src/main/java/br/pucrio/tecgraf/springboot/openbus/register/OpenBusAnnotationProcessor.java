package br.pucrio.tecgraf.springboot.openbus.register;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class OpenBusAnnotationProcessor implements BeanPostProcessor {

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Autowired
    public OpenBusAnnotationProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory) {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
    }

    private void scanOpenBusServiceAnnotation(Object bean, String beanName) {
        System.out.println("Bean: " + bean + ", beanName: " + beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        scanOpenBusServiceAnnotation(bean, beanName);
        return bean;
    }

}
