package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import tecgraf.openbus.assistant.Assistant;

public interface OpenBusComponentsCreatorNotifier {

    ORBManager onCreateOrbManager(ORBManager orbManager);
    OpenBusServicesRegistry onCreateOpenBusServicesRegistry(OpenBusServicesRegistry openBusServicesRegistry);
    Assistant onCreateAssistant(Assistant assistant);
    OpenBusRegistrator onCreateOpenBusRegistrator(OpenBusRegistrator openBusRegistrator);
    OpenBusApplicationInstance onCreateOpenBusApplicationInstance(OpenBusApplicationInstance openBusApplicationInstance);

}
