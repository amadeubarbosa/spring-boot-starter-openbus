package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

@Configuration(proxyBeanMethods = false)

public class OpenBusAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Object openBusComponent;

    private byte major;
    private byte minor;
    private byte patch;

    @Bean(name = "major")
    public byte major() {
        return major;
    }

    @Bean(name = "minor")
    public byte minor() {
        return minor;
    }

    @Bean(name = "patch")
    public byte patch() {
        return patch;
    }

    private void findOpenBusComponent() {
        Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(OpenBusApplication.class);
        if (annotatedBeans == null || annotatedBeans.size() == 0) throw new RuntimeException("Não há nenhum componente" +
                " OpenBus: favor anote com " + OpenBusApplication.class.getName());
        else if (annotatedBeans.size() > 1) throw new RuntimeException("Só é permitido um componente" +
                " OpenBus por aplicação (foram encontrados " + annotatedBeans.size() + " componentes)");
        Class<?> openBusComponentClass = annotatedBeans.values().toArray()[0].getClass();
        OpenBusApplication openBusApplication = AnnotationUtils.findAnnotation(openBusComponentClass, OpenBusApplication.class);
        this.major = openBusApplication.major();
        this.minor = openBusApplication.minor();
        this.patch = openBusApplication.patch();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        findOpenBusComponent();
    }

}
