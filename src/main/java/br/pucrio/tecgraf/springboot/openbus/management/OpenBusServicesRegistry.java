package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.ServiceOffer;

import java.util.Set;

public interface OpenBusServicesRegistry {
    void addService(ServiceOffer serviceOffer);

    void removeService(ServiceOffer serviceOffer);

    void registerService(ServiceOffer serviceOffer);

    Set<ServiceOffer> availableOffers();

    Set<ServiceOffer> registeredOffers();
}
