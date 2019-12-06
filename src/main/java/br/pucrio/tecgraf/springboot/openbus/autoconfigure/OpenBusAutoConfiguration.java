package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusApplicationInstance;
import br.pucrio.tecgraf.springboot.openbus.management.OpenBusApplicationVersion;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import br.pucrio.tecgraf.springboot.openbus.register.OpenBusBeanPostProcessor;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
>>>>>>> Thread de shutdown
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringValueResolver;

import java.util.Map;

@Configuration(proxyBeanMethods = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan("br.pucrio.tecgraf.springboot")
@EnableConfigurationProperties({OpenBusPropertiesConnection.class, OpenBusPropertiesOrb.class, OpenBusPropertiesServices.class})
public class OpenBusAutoConfiguration implements BeanFactoryAware, EmbeddedValueResolverAware {

    private Logger log = LoggerFactory.getLogger(OpenBusAutoConfiguration.class);

    // TODO Fazer o @Conditional (somente eletivo se as condições forem realizadas)
    static class OpenbusConditional extends AnyNestedCondition {

        OpenbusConditional() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }
    }

    private ApplicationContext applicationContext;

    private String componentName;

    private byte major;
    private byte minor;
    private byte patch;

    private OpenBusApplication openBusApplication;

    private StringValueResolver resolver;

    /**
     * A versão pode vir de 3 lugares (na ordem de prioridade)
     * 1) Da anotação
     * 2) Do arquivo application.properties
     * 3) Do manifesto
     * Caso não seja encontrado nesses 3 lugares, o valor padrão será 0.0.0
     */
    private void registerComponent(OpenBusConfiguration openBusConfiguration) {
        createAnnotationInstance();
        registerName(openBusConfiguration);
        registerVersion(openBusConfiguration);
    }

    private void registerVersion(OpenBusConfiguration openBusConfiguration) {
        OpenBusApplicationVersion version = new OpenBusApplicationVersion.Builder()
                .applyVersion(openBusApplication.major(), openBusApplication.minor(), openBusApplication.patch())
                .applyVersion(openBusConfiguration.getComponentVersion())
                .readManifest()
                .version();

        this.major = version.getMajor();
        this.minor = version.getMinor();
        this.patch = version.getPatch();
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

    private void registerName(OpenBusConfiguration openBusConfiguration) {
        String name = null;
        // 1. Tenta obter o nome da anotação
        if (openBusApplication.value() != null) name = openBusApplication.value();
        // 2. Tenta obter o nome do arquivo de configuração
        else if (openBusConfiguration.getName() != null) name = openBusConfiguration.getName();
        // Caso o nome permaneça nulo, informa o usuário que ele deve fornecer um nome válido
        if (name == null) {
            throw new RuntimeException("Um nome de aplicação deve ser declarado ou na anotação " +
                    OpenBusApplication.class + " ou na propriedade 'openbus.name' para ser registrado");
        }
        this.componentName = resolver.resolveStringValue(name);
        log.info("Nome da aplicação resolvida: {}", this.componentName);
    }

    @Bean
    public ORB autoConfigureORBProduces(OpenBusApplicationInstance instance) {
        return instance.getOrb();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 1000)
    public OpenBusBeanPostProcessor producesOpenBusBeanPostProcessor(ConfigurableListableBeanFactory configurableListableBeanFactory,
                                                                     OpenBusApplicationInstance openBusApplicationInstance) {
        return new OpenBusBeanPostProcessor(configurableListableBeanFactory, openBusApplicationInstance);
    }

    @Bean
    @Primary
    public OpenBusApplicationInstance registerOpenBusApplicationInstance(ApplicationContext applicationContext)
            throws Exception {

        this.applicationContext = applicationContext;

        // Obtém os beans de configuração
        OpenBusPropertiesOrb openBusPropertiesOrb = applicationContext.getBean(OpenBusPropertiesOrb.class);
        OpenBusConfiguration openBusConfiguration = applicationContext.getBean(OpenBusConfiguration.class);
        OpenBusPropertiesConnection openBusPropertiesConnection = applicationContext.getBean(OpenBusPropertiesConnection.class);
        OpenBusPropertiesServices openBusPropertiesServices = applicationContext.getBean(OpenBusPropertiesServices.class);

        // Registra os detalhes do componente
        registerComponent(openBusConfiguration);

        // Registra o processador de beans
        //listableBeanFactory.registerSingleton("openBusBeanPostProcessor", OpenBusBeanPostProcessor.class);
        // Cria o builder de instâncias openbus
        OpenBusApplicationInstanceSpring openBusApplicationInstance = new OpenBusApplicationInstanceSpring(
                componentName, major, minor, patch,
                openBusPropertiesOrb, openBusConfiguration, openBusPropertiesConnection,
                openBusPropertiesServices, null);
        openBusApplicationInstance.setBeanFactory(listableBeanFactory);
        // Delega a criação dos componentes
        return openBusApplicationInstance.instantiate();
    }

    private ConfigurableListableBeanFactory listableBeanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.listableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

}
