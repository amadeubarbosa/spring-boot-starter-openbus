package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
import br.pucrio.tecgraf.springboot.openbus.register.RemoteApplication;
import tecgraf.openbus.assistant.Assistant;

/**
 * A notificação de eventos permite que outros componentes possam complementar o comportamento do OpenBus, como,
 * o {@link br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusApplicationInstanceSpring}, que permite que as
 * instâncias criadas sejam disponibilizadas para o spring.
 */
public interface OpenBusComponentsCreatorNotifier {

    ORBManager onCreateOrbManager(ORBManager orbManager);
    Assistant onCreateAssistant(Assistant assistant);
    OpenBusRegistrator onCreateOpenBusRegistrator(OpenBusRegistrator openBusRegistrator);
    OpenBusApplicationInstance onCreateOpenBusApplicationInstance(OpenBusApplicationInstance openBusApplicationInstance);

}
