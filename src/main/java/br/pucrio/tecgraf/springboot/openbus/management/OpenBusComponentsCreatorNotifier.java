package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplication;
import tecgraf.openbus.assistant.Assistant;

public interface OpenBusComponentsCreatorNotifier {

    ORBManager onCreateOrbManager(ORBManager orbManager);
    Assistant onCreateAssistant(Assistant assistant);
    OpenBusRegistrator onCreateOpenBusRegistrator(OpenBusRegistrator openBusRegistrator);
    OpenBusApplicationInstance onCreateOpenBusApplicationInstance(OpenBusApplicationInstance openBusApplicationInstance);

}
