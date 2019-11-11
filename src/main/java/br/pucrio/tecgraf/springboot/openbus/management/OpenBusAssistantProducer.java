package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration;
import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesConnection;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.AssistantParams;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;

public class OpenBusAssistantProducer implements Produtor<Assistant> {

    private Logger log = LoggerFactory.getLogger(OpenBusAssistantProducer.class);

    private AssistantParams assistantParams;

    private String address;
    private int port;
    private String name;
    private RSAPrivateKey privateKey;

    public OpenBusAssistantProducer(ORB orb,
                                    String componentName,
                                    OpenBusFailureCallback openbusFailureCallback,
                                    OpenBusConfiguration openBusConfiguration,
                                    OpenBusPropertiesConnection openBusPropertiesConnection) {
        // Assistant params
        assistantParams = new AssistantParams();
        assistantParams.interval = openBusConfiguration.getRetryInterval().getSeconds() / 1F;
        assistantParams.callback = openbusFailureCallback;
        assistantParams.orb = orb;
        assistantParams.connprops = openBusPropertiesConnection.producer();
        // openBus params
        this.address = openBusConfiguration.getAddress();
        this.port = openBusConfiguration.getPort();
        this.name = componentName == null ? openBusConfiguration.getName() : componentName;
        this.privateKey = loadRSAPrivateKey(openBusConfiguration.getPrivateKey());
        log.info("Configurações do assistente openBus inicializado");
    }

    private RSAPrivateKey loadRSAPrivateKey(PrivateKey privateKey) {
        if (!(privateKey instanceof RSAPrivateKey)) {
            throw new RuntimeException("O assistente do OpenBus só suporta chaves RSA");
        }
        return (RSAPrivateKey)privateKey;
    }

    public Assistant produces() {
        log.info("Inicializando produção de assistente do openBus");
        Assistant assistant;
        try {
            assistant = Assistant.createWithPrivateKey(address, port, name, privateKey,
                    assistantParams);
            log.info("Assistente do openBus produzido com sucesso");
            return assistant;
        }
        catch (IllegalArgumentException e) {
            log.error("Assistente do openBus não foi inicializado", e);
            throw new RuntimeException("Erro de configuração ao tentar criar Assistente do OpenBus.", e);
        }
    }

}
