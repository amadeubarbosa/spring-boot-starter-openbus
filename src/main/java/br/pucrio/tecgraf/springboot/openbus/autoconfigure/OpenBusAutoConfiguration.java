package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusApplicationInstance;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusFailureCallback;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusRegistrator;
import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

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

    private OpenBusApplication openBusApplication;

    /**
     * A versão pode vir de 3 lugares (na ordem de prioridade)
     * 1) Da anotação
     * 2) Do arquivo application.properties
     * 3) Do manifesto
     * Caso não seja encontrado nesses 3 lugares, o valor padrão será 0.0.0
     */
    private void registerVersion() {
        this.major = openBusApplication.major();
        this.minor = openBusApplication.minor();
        this.patch = openBusApplication.patch();
        if (this.major == 0) {
            // TODO Tenta ler do application.properties
            if (this.major == 0) {
                // TODO Tenta ler do manifesto
            }
        }
    }

    private void createAnnotationInstance() {
        Map<String, Object> annotatedBeans = applicationContext.getBeansWithAnnotation(OpenBusApplication.class);
        if (annotatedBeans == null || annotatedBeans.size() == 0) throw new RuntimeException("Não há nenhum componente" +
                " OpenBus: favor anote com " + OpenBusApplication.class.getName());
        else if (annotatedBeans.size() > 1) throw new RuntimeException("Só é permitido um componente" +
                " OpenBus por aplicação (foram encontrados " + annotatedBeans.size() + " componentes)");
        Class<?> openBusComponentClass = annotatedBeans.values().toArray()[0].getClass();
        openBusApplication = AnnotationUtils.findAnnotation(openBusComponentClass, OpenBusApplication.class);
    }

    private void registerName() {
        this.componentName = openBusApplication.value();
    }

    private void registerComponent() {
        createAnnotationInstance();
        registerName();
        registerVersion();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        registerComponent();
    }


    @Bean
    public OpenBusApplicationInstance openBusApplicationInstance(ORBManager orbManager,
                                                                 OpenBusRegistrator openBusRegistrator,
                                                                 OpenBusFailureCallback openbusFailureCallback,
                                                                 OpenBusConfiguration openBusConfiguration,
                                                                 OpenBusPropertiesServices openBusPropertiesServices,
                                                                 OpenBusPropertiesConnection openBusPropertiesConnection) {
        return new OpenBusApplicationInstance(orbManager, openBusRegistrator,
                openbusFailureCallback, openBusConfiguration, openBusPropertiesServices, openBusPropertiesConnection);
    }

}
