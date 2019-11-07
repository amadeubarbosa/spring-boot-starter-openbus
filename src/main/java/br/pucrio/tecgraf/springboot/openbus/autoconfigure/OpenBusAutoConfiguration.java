package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusComponentProducer;
import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import scs.core.ComponentContext;
import scs.core.exception.SCSException;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenBusPropertiesConnection.class, OpenBusPropertiesOrb.class, OpenBusPropertiesServices.class})
public class OpenBusAutoConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Object openBusComponent;

    private String componentName;

    private byte major;
    private byte minor;
    private byte patch;

    private ORBManager orbManager;

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

    @Bean(name = "componentName")
    public String componentName() {
        return componentName;
    }

    @Bean(name = "orb")
    public ORB orb() {
        return orbManager.getORB();
    }

    @Bean(name = "poa")
    public POA poa() {
        return orbManager.getPOA();
    }

    @Bean(name = "componentContext")
    public ComponentContext componentContext() throws SCSException {
        return new OpenBusComponentProducer(orbManager.getORB(), orbManager.getPOA(), componentName, major, minor, patch).producesComponentContext();
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
        this.componentName = openBusApplication.value();
        this.orbManager = applicationContext.getBean(ORBManager.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        findOpenBusComponent();
    }

}
