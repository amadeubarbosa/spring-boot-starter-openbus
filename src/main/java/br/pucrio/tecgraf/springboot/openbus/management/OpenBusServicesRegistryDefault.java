package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class OpenBusServicesRegistryDefault implements OpenBusServicesRegistry {

    private Logger log = LoggerFactory.getLogger(OpenBusServicesRegistryDefault.class);

    private Set<ServiceOffer> availableServiceOffers = new HashSet<>();
    private Set<ServiceOffer> registeredServiceOffers = new HashSet<>();

    @Override
    public void addService(ServiceOffer serviceOffer) {
        availableServiceOffers.add(serviceOffer);
        log.info("Oferta adicionada: {}", serviceOffer);
    }

    @Override
    public void removeService(ServiceOffer serviceOffer) {
        availableServiceOffers.remove(serviceOffer);
        registeredServiceOffers.remove(serviceOffer);
        log.info("Oferta removida: {} ", serviceOffer);
    }

    @Override
    public void registerService(ServiceOffer serviceOffer) {
        availableServiceOffers.remove(serviceOffer);
        registeredServiceOffers.add(serviceOffer);
        log.info("Oferta registrada: {} ", serviceOffer);
    }

    @Override
    public Set<ServiceOffer> availableOffers() {
        return new HashSet<>(availableServiceOffers);
    }

    @Override
    public Set<ServiceOffer> registeredOffers() {
        return new HashSet<>(registeredServiceOffers);
    }


}
