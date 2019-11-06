package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;
import br.pucrio.tecgraf.springboot.openbus.register.ServiceOfferRegister;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import scs.core.exception.SCSException;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.AssistantParams;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Collection;
import java.util.Properties;

@Component
public class OpenBusAssistantRegistrator implements OpenBusRegistrator {

    private Logger logger = LoggerFactory.getLogger(OpenBusAssistantRegistrator.class);

    private String address;
    private int port;
    private String name;
    private RSAPrivateKey privateKey;
    private AssistantParams assistantParams;

    private Assistant assistant;

    private ORB orb;
    private POA poa;

    private Collection<ServiceOffer> serviceOffers;

    public OpenBusAssistantRegistrator(
            OpenBusFailureCallback openbusFailureCallback,
            OpenBusConfiguration openBusConfiguration,
            Properties connectionProperties,
            ORB orb,
            POA poa,
            ServiceOfferRegister serviceOffers) throws SCSException {
        // Assistant params
        assistantParams = new AssistantParams();
        assistantParams.interval = openBusConfiguration.getRetryInterval().getSeconds() / 1F;
        assistantParams.callback = openbusFailureCallback;
        assistantParams.orb = orb;
        assistantParams.connprops = connectionProperties;
        // openBus params
        this.address = openBusConfiguration.getAddress();
        this.port = openBusConfiguration.getPort();
        this.name = openBusConfiguration.getName();
        this.privateKey = loadRSAPrivateKey(openBusConfiguration.getPrivateKey());
        this.orb = orb;
        this.poa = poa;
        this.serviceOffers = serviceOffers.create();
        logger.info("Configurações do assistente openBus inicializado");
    }

    private RSAPrivateKey loadRSAPrivateKey(PrivateKey privateKey) {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new RuntimeException("O assistente do OpenBus só suporta chaves RSA");
        }
        return (RSAPrivateKey)privateKey;
    }

    @Override
    public void activatePOA() {
        try {
            poa.the_POAManager().activate();
        }
        catch (UserException | SystemException e) {
            throw new RuntimeException("Erro ao tentar ativar o POA raíz.", e);
        }
    }

    @Override
    public void initializeEngine() {
        logger.info("Inicializando assistente do openBus");
        try {
            assistant = Assistant.createWithPrivateKey(address, port, name, privateKey,
                    assistantParams);
            logger.info("Assistente do openBus inicializado com sucesso");
        }
        catch (IllegalArgumentException e) {
            logger.error("Assistente do openBus não foi inicializado", e);
            throw new RuntimeException("Erro de configuração ao tentar criar Assistente do OpenBus.", e);
        }
    }

    @Override
    public void registerServices() {
        for (ServiceOffer serviceOffer: serviceOffers) {
            try {
                assistant.registerService(serviceOffer.getComponent(), serviceOffer.getServiceProperties().toArray(new ServiceProperty[] {}));
            }
            catch (SystemException e) {
                throw new RuntimeException(String.format("Ocorreu um erro CORBA ao tentar registrar o " +
                        "serviço %s (%s:%d)", name, address, port));
            }
        }
    }

    /**
     * Inicializa o object request broker.
     */
    @Override
    public void startOrb() {
        logger.info(String.format("O Serviço %s (openBus %s:%s) está aguardando por requisições dos clientes.",
                name, address, port));
        orb.run();
    }

    @Override
    public void stop() {
        logger.info("Parando assistente do openBus");
        if (assistant == null) {
            logger.warn("O assistente do openBus não havia sido criado");
            return;
        }
    }

}
