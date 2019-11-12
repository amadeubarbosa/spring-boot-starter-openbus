package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.orb.ORBManagerDefault;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplication;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.OnFailureCallback;

/**
 * Essa classe, apesar de não estar no conceito do formato padrão, é um builder que facilita a criação de instâncias
 * {@link OpenBusApplicationInstance}.
 */
public class OpenBusApplicationInstanceProducer implements Produtor<OpenBusApplicationInstance> {

    private OnFailureCallback onFailureCallback;
    private String componentName;
    private byte major;
    private byte minor;
    private byte patch;

    private ORBManager orbManager;
    private OpenBusConfiguration openBusConfiguration;
    private OpenBusPropertiesServices openBusPropertiesServices;

    private Produtor<Assistant> openBusAssistantProducer;

    private OpenBusComponentsCreatorNotifier notifier;

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
    public OpenBusApplicationInstanceProducer(String componentName, byte major, byte minor, byte patch,
                                              OpenBusComponentsCreatorNotifier notifier,
                                              OpenBusPropertiesOrb openBusPropertiesOrb,
                                              OpenBusConfiguration openBusConfiguration,
                                              OpenBusPropertiesConnection openBusPropertiesConnection,
                                              OpenBusPropertiesServices openBusPropertiesServices,
                                              OnFailureCallback onFailureCallback) throws Exception {
        // Propriedades básicas
        this.componentName = componentName;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.notifier = notifier;
        // Callback (pode ser customizado ou o padrão)
        this.onFailureCallback = onFailureCallback;
        if (this.onFailureCallback == null) {
            this.onFailureCallback = new OpenBusFailureCallback(openBusConfiguration);
        }
        // Gerenciador padrão de ORB
        this.orbManager = createOrbManager(openBusPropertiesOrb);
        // Produtor de assistant para o registrator
        this.openBusAssistantProducer = new OpenBusAssistantProducer(orbManager, componentName, onFailureCallback,
                openBusConfiguration, openBusPropertiesConnection);
        // Configurações do openbus por parâmetro
        this.openBusConfiguration = openBusConfiguration;
        // Configurações de serviço por parâmetro
        this.openBusPropertiesServices = openBusPropertiesServices;

    }

    public ORBManager createOrbManager(OpenBusPropertiesOrb openBusPropertiesOrb) {
        return notifier.onCreateOrbManager(new ORBManagerDefault(openBusPropertiesOrb));
    }

    public Assistant createAssistant() throws Exception {
        return notifier.onCreateAssistant(openBusAssistantProducer.produces());
    }

    public OpenBusRegistrator createOpenBusRegistrator(RemoteApplication remoteApplication) throws Exception {
        return notifier.onCreateOpenBusRegistrator(new OpenBusAssistantRegistrator(createAssistant(), remoteApplication));
    }

    @Override
    public OpenBusApplicationInstance produces() throws Exception {
        OpenBusApplicationInstance instance = new OpenBusApplicationInstance(
                componentName, major, minor, patch,
                orbManager, openBusConfiguration, openBusPropertiesServices);
        return notifier.onCreateOpenBusApplicationInstance(instance);
    }

}
