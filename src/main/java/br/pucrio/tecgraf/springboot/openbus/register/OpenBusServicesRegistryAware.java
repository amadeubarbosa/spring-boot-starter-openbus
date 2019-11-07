package br.pucrio.tecgraf.springboot.openbus.register;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusServicesRegistry;

public interface OpenBusServicesRegistryAware {

    void setOpenBusServiceRegistry(OpenBusServicesRegistry openBusServiceRegistry);

}
