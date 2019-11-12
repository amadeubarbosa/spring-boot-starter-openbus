package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesServices;
import br.pucrio.tecgraf.springboot.openbus.register.DefaultServiceOffer;
import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;
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
    private OpenBusServicesRegistry registry;
    private OpenBusRegistrator openBusRegistrator;

    private OpenBusConfiguration openBusConfiguration;
    private OpenBusPropertiesServices openBusPropertiesServices;

    private ORBManager orbManager;

    public OpenBusApplicationInstance(String name,
                                      byte major,
                                      byte minor,
                                      byte patch,
                                      ORBManager orbManager,
                                      OpenBusServicesRegistry registry,
                                      OpenBusRegistrator openBusRegistrator,
                                      OpenBusConfiguration openBusConfiguration,
                                      OpenBusPropertiesServices openBusPropertiesServices) {
        this.name = name;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.orbManager = orbManager;
        this.registry = registry;
        this.openBusRegistrator = openBusRegistrator;
        this.openBusConfiguration = openBusConfiguration;
        this.openBusPropertiesServices = openBusPropertiesServices;
        this.orb = orbManager.getORB();
        this.poa = orbManager.getPOA();
    }

    public void initialize() throws SCSException {
        // Cria o contexto do componente
        createComponentContext();
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
        String platformSpecification = String.format("java-%s", System.getProperty("java.specification.version"));
        componentContext = new ComponentContext(orb, poa, new ComponentId(
                name, major, minor, patch, platformSpecification));
    }

    private ServiceProperty[] mountServiceProperties(String name) {
        Map<String, String> serviceProperties = openBusPropertiesServices.getProperties();
        return serviceProperties.keySet().stream()
                .filter(item -> item.equals(name) || item.equals("default"))
                .map(item -> new ServiceProperty(item, serviceProperties.get(item)))
                .collect(Collectors.toList())
                .toArray(new ServiceProperty[]{});
    }

    public ServiceOffer addService(Servant servant, String name, String id) throws SCSException {
        if (name.equals("default")) throw new SCSException("O nome 'default' é reservado: não pode ser utilizado para criação de serviços OpenBus");
        log.info("Criando serviço openBus: {}, id: {}, servant: {})", name, id, servant);
        componentContext.addFacet(name, id, servant);
        // Cria o conceito de oferta e propriedades e o registra como bean
        ServiceOffer serviceOffer = new DefaultServiceOffer(
                name,
                componentContext.getIComponent(),
                mountServiceProperties(name)
        );
        // Registra o serviço para depois ser registrado no OpenBus
        registry.addService(serviceOffer);
        return serviceOffer;
    }

}
