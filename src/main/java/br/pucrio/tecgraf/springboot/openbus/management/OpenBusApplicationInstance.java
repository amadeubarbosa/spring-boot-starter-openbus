package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplication;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplicationDefault;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteServiceDefault;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteService;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.exception.SCSException;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.util.Map;
import java.util.stream.Collectors;

public class OpenBusApplicationInstance {

    private Logger log = LoggerFactory.getLogger(OpenBusApplicationInstance.class);

    private String name;
    private byte major;
    private byte minor;
    private byte patch;
    private ORB orb;
    private POA poa;
    private ComponentContext componentContext;
    private OpenBusRegistrator openBusRegistrator;

    private OpenBusConfiguration openBusConfiguration;
    private OpenBusPropertiesServices openBusPropertiesServices;

    private ORBManager orbManager;

    private RemoteApplication remoteApplication;

    public OpenBusApplicationInstance(String name,
                                      byte major,
                                      byte minor,
                                      byte patch,
                                      ORBManager orbManager,
                                      OpenBusConfiguration openBusConfiguration,
                                      OpenBusPropertiesServices openBusPropertiesServices) {
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.orbManager = orbManager;
        this.openBusConfiguration = openBusConfiguration;
        this.openBusPropertiesServices = openBusPropertiesServices;
        this.orb = orbManager.getORB();
        this.poa = orbManager.getPOA();
    }

    public void initialize() throws SCSException {
        // Cria o contexto do componente
        createComponentContext();
        // Cria o item principal que guarda a instância remota
        createRemoteApplication();
    }

    public void addRegistrator(OpenBusRegistrator openBusRegistrator) {
        // Registrador com assistente, informando o registry padrão
        this.openBusRegistrator = openBusRegistrator;
    }

    public void start() {
        // Ativa o poa raiz
        orbManager.activatePOA();
        // Registra os serviços
        openBusRegistrator.registerServices();
        // Inicializa o ORB
        orbManager.startOrb();
    }

    public void stop() {
        // Sinaliza parada para o registro
        openBusRegistrator.stop();
        // Para o orb
        orbManager.shutdownOrb();
    }

    public RemoteApplication getRemoteApplication() {
        return remoteApplication;
    }

    public String getName() {
        return name;
    }

    public ORB getOrb() {
        return orb;
    }

    public POA getPoa() {
        return poa;
    }

    public OpenBusConfiguration getOpenBusConfiguration() {
        return openBusConfiguration;
    }

    private void createComponentContext() throws SCSException {
        // A especificação da plataforma é uma concatenação de "java" + a versão da propriedade da versão de java
        String platformSpecification = String.format("%s (%s) - %s %s [%s]",
                System.getProperty("java.vm.name"),
                System.getProperty("java.vm.vendor"),
                System.getProperty("java.runtime.version"),
                System.getProperty("java.vm.version"),
                System.getProperty("java.vm.info"));
        componentContext = new ComponentContext(orb, poa, new ComponentId(
                name, major, minor, patch, platformSpecification));
    }

    private void createRemoteApplication() {
        this.remoteApplication = new RemoteApplicationDefault(name, componentContext, mountApplicationProperties());
    }

    private ServiceProperty[] mountApplicationProperties() {
        Map<String, String> serviceProperties = openBusPropertiesServices.getProperties();
        return serviceProperties.keySet().stream()
                // TODO Filtrar propriedades por serviço
                //.filter(item -> item.equals(name) || item.equals("default"))
                .map(item -> new ServiceProperty(item, serviceProperties.get(item)))
                .collect(Collectors.toList())
                .toArray(new ServiceProperty[]{});
    }

    public RemoteService addService(Servant servant, String name, String id) throws SCSException {
        if (name.equals("default")) throw new SCSException("O nome 'default' é reservado: não pode ser utilizado para criação de serviços OpenBus");
        log.info("Criando serviço openBus: {}, id: {}, servant: {})", name, id, servant);
        // Cria o conceito de oferta e propriedades e o registra como bean
        RemoteService remoteService = new RemoteServiceDefault(remoteApplication, servant, name, id);
        // Registra o serviço para depois ser registrado no OpenBus
        remoteApplication.addService(remoteService);
        return remoteService;
    }

}
