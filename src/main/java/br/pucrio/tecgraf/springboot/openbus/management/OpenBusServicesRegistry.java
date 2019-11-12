package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.register.RemoteService;
import scs.core.exception.SCSException;

import java.util.Set;

public interface OpenBusServicesRegistry {

    void addService(RemoteService remoteService) throws SCSException;

    void removeService(RemoteService remoteService);

    void registerService(RemoteService remoteService);

    Set<RemoteService> availableOffers();

    Set<RemoteService> registeredOffers();

    void unregisterServices();

}
