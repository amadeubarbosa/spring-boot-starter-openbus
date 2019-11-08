package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class OpenBusServicesRegistry {

    private Logger log = LoggerFactory.getLogger(OpenBusServicesRegistry.class);

    private Set<ServiceOffer> availableServiceOffers = new HashSet<>();
    private Set<ServiceOffer> registeredServiceOffers = new HashSet<>();

    public void addService(ServiceOffer serviceOffer) {
        availableServiceOffers.add(serviceOffer);
        log.info("Oferta adicionada: {}", serviceOffer);
    }

    public void removeService(ServiceOffer serviceOffer) {
        availableServiceOffers.remove(serviceOffer);
        registeredServiceOffers.remove(serviceOffer);
        log.info("Oferta removida: {} ", serviceOffer);
    }

    public void registerService(ServiceOffer serviceOffer) {
        availableServiceOffers.remove(serviceOffer);
        registeredServiceOffers.add(serviceOffer);
        log.info("Oferta registrada: {} ", serviceOffer);
    }

    public Set<ServiceOffer> availableOffers() {
        return new HashSet<>(availableServiceOffers);
    }

    public Set<ServiceOffer> registeredOffers() {
        return registeredOffers();
    }


}
