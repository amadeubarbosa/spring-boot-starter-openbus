package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

@Component
public class OpenBusAssistantRegistrator implements OpenBusRegistrator {

    private Logger logger = LoggerFactory.getLogger(OpenBusAssistantRegistrator.class);

    private Assistant assistant;
    private OpenBusServicesRegistry openBusServicesRegistry;

    public OpenBusAssistantRegistrator(Assistant assistant, OpenBusServicesRegistry openBusServicesRegistry) {
        this.assistant = assistant;
        this.openBusServicesRegistry = openBusServicesRegistry;
    }

    @Override
    public void registerServices() {
        logger.info("Registrando serviços no openbus");
        for (ServiceOffer serviceOffer: openBusServicesRegistry.availableOffers()) {
            // Registra o serviço
            assistant.registerService(serviceOffer.getComponent(), serviceOffer.getServiceProperties().toArray(new ServiceProperty[] {}));
            // Move de disponível para registrada
            openBusServicesRegistry.registerService(serviceOffer);
        }
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
