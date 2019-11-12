package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import br.pucrio.tecgraf.springboot.openbus.management.*;
import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.OnFailureCallback;

public class OpenBusApplicationInstanceSpring implements
        BeanFactoryAware,
        OpenBusComponentsCreatorNotifier {

    private Logger log = LoggerFactory.getLogger(OpenBusApplicationInstanceSpring.class);

    public static final String ORB_MANAGER = "orbManager";
    public static final String OPEN_BUS_SERVICES_REGISTRY = "openBusServicesRegistry";
    public static final String ASSISTANT = "assistant";
    public static final String OPEN_BUS_REGISTRATOR = "openBusRegistrator";
    public static final String OPEN_BUS_APPLICATION_INSTANCE = "openBusApplicationInstance";

    private ConfigurableListableBeanFactory beanFactory;

    private String componentName;
    private byte major;
    private byte minor;
    private byte patch;
    private OpenBusPropertiesOrb openBusPropertiesOrb;
    private OpenBusConfiguration openBusConfiguration;
    private OpenBusPropertiesConnection openBusPropertiesConnection;
    private OpenBusPropertiesServices openBusPropertiesServices;
    private OnFailureCallback onFailureCallback;

    /**
     * Forma padrão de instanciar {@link OpenBusApplicationInstance}.
     * Todos os itens serão criados com o padrão e um Assistant será utilizado.
     * @param componentName nome do componente
     * @param major versão maior do componente
     * @param minor versão menor do componente
     * @param patch versão de modificação do componente
     * @param openBusPropertiesOrb configurações do orb
     * @param openBusConfiguration configurações do openBus
     * @param openBusPropertiesConnection configurações da conexão ao openBus
     * @param openBusPropertiesServices configurações de cada serviço
     * @param onFailureCallback callback que será chamado em caso de falha
     * @throws Exception normalmente ocorre quando o assistente falha ao ser criado
     */
    public OpenBusApplicationInstanceSpring(String componentName, byte major, byte minor, byte patch,
                                            OpenBusPropertiesOrb openBusPropertiesOrb,
                                            OpenBusConfiguration openBusConfiguration,
                                            OpenBusPropertiesConnection openBusPropertiesConnection,
                                            OpenBusPropertiesServices openBusPropertiesServices,
                                            OnFailureCallback onFailureCallback) throws Exception {
        this.componentName = componentName;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.openBusPropertiesOrb = openBusPropertiesOrb;
        this.openBusConfiguration = openBusConfiguration;
        this.openBusPropertiesConnection = openBusPropertiesConnection;
        this.openBusPropertiesServices = openBusPropertiesServices;
        this.onFailureCallback = onFailureCallback;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.debug("Bean factory configurado");
        this.beanFactory = (ConfigurableListableBeanFactory)beanFactory;
    }

    public OpenBusApplicationInstance instantiate() throws Exception {
        log.debug("Inicializando instância do componente OpenBus");
        OpenBusApplicationInstanceProducer producer = new OpenBusApplicationInstanceProducer(
                componentName, major, minor, patch, this, openBusPropertiesOrb, openBusConfiguration,
                openBusPropertiesConnection, openBusPropertiesServices, onFailureCallback);
        OpenBusApplicationInstance openBusApplicationInstance = producer.produces();
        log.info("Instância do componente OpenBus criada");
        // Cria a instância do componente local para disponibilização para a aplicação
        openBusApplicationInstance.initialize();
        log.info("Instância do componente OpenBus pronta para utilização");
        return openBusApplicationInstance;
    }

    @Override
    public ORBManager onCreateOrbManager(ORBManager orbManager) {
        log.info("Registrando componente {}", ORB_MANAGER);
        beanFactory.registerSingleton(ORB_MANAGER, orbManager);
        return orbManager;
    }

    @Override
    public OpenBusServicesRegistry onCreateOpenBusServicesRegistry(OpenBusServicesRegistry openBusServicesRegistry) {
        log.info("Registrando componente {}", OPEN_BUS_SERVICES_REGISTRY);
        beanFactory.registerSingleton(OPEN_BUS_SERVICES_REGISTRY, openBusServicesRegistry);
        return openBusServicesRegistry;
    }

    @Override
    public Assistant onCreateAssistant(Assistant assistant) {
        beanFactory.registerSingleton(ASSISTANT, assistant);
        return assistant;
    }

    @Override
    public OpenBusRegistrator onCreateOpenBusRegistrator(OpenBusRegistrator openBusRegistrator) {
        log.info("Registrando componente {}", OPEN_BUS_REGISTRATOR);
        beanFactory.registerSingleton(OPEN_BUS_REGISTRATOR, openBusRegistrator);
        return openBusRegistrator;
    }

    @Override
    public OpenBusApplicationInstance onCreateOpenBusApplicationInstance(OpenBusApplicationInstance openBusApplicationInstance) {
        // Não está preparado para utilização fora do springboot: por enquanto, deve ser registrado dentro do contexto
        // do spring com @Configuration e @Bean
        return openBusApplicationInstance;
    }


}
