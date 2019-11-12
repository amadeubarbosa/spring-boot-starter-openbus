package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tecgraf.openbus.assistant.Assistant;

public class OpenBusAssistantRegistrator implements OpenBusRegistrator {

    private Logger logger = LoggerFactory.getLogger(OpenBusAssistantRegistrator.class);

    private Assistant assistant;
    private RemoteApplication remoteApplication;

    public OpenBusAssistantRegistrator(Assistant assistant, RemoteApplication remoteApplication) {
        this.assistant = assistant;
        this.remoteApplication = remoteApplication;
    }

    @Override
    public void registerServices() {
        // Registra a apĺicação (componente) no OpenBus
        logger.debug("Registrando aplicação {} (componente) no openbus", remoteApplication);
        assistant.registerService(remoteApplication.getComponentContext().getIComponent(), remoteApplication.getApplicationProperties());
        logger.info("Aplicação {} (componente) registrada com sucesso", remoteApplication);
    }

    @Override
    public void stop() {
        logger.info("Parando assistente do openBus");
        if (assistant == null) {
            logger.warn("O assistente do openBus não havia sido criado");
            return;
        }
        // Efetua shutdown do assistant
        assistant.shutdown();
    }

}
