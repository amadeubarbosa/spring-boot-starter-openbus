package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusApplicationInstance;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Map;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({OpenBusPropertiesConnection.class, OpenBusPropertiesOrb.class, OpenBusPropertiesServices.class})
public class OpenBusAutoConfiguration implements BeanFactoryAware {

    private ApplicationContext applicationContext;

    private String componentName;

    private byte major;
    private byte minor;
    private byte patch;

    private OpenBusApplication openBusApplication;


    /**
     * A versão pode vir de 3 lugares (na ordem de prioridade)
     * 1) Da anotação
     * 2) Do arquivo application.properties
     * 3) Do manifesto
     * Caso não seja encontrado nesses 3 lugares, o valor padrão será 0.0.0
     */
    private void registerComponent() {
        createAnnotationInstance();
        registerName();
        registerVersion();
    }

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

    @Bean
    public OpenBusApplicationInstance registerOpenBusApplicationInstance(ApplicationContext applicationContext)
            throws Exception {

        this.applicationContext = applicationContext;

        // Registra os detalhes do componente
        registerComponent();

        // Obtém os beans de configuração
        OpenBusPropertiesOrb openBusPropertiesOrb = applicationContext.getBean(OpenBusPropertiesOrb.class);
        OpenBusConfiguration openBusConfiguration = applicationContext.getBean(OpenBusConfiguration.class);
        OpenBusPropertiesConnection openBusPropertiesConnection = applicationContext.getBean(OpenBusPropertiesConnection.class);
        OpenBusPropertiesServices openBusPropertiesServices = applicationContext.getBean(OpenBusPropertiesServices.class);
        // Cria o builder de instâncias openbus
        OpenBusApplicationInstanceSpring openBusApplicationInstance = new OpenBusApplicationInstanceSpring(
                componentName, major, minor, patch,
                openBusPropertiesOrb, openBusConfiguration, openBusPropertiesConnection,
                openBusPropertiesServices, null);
        openBusApplicationInstance.setBeanFactory(listableBeanFactory);
        // Delega a criação dos componentes
        return openBusApplicationInstance.instantiate();
    }

    private ListableBeanFactory listableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.listableBeanFactory = (ListableBeanFactory) beanFactory;
    }

}
