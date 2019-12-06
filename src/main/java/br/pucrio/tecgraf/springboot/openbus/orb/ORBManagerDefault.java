package br.pucrio.tecgraf.springboot.openbus.orb;

import br.pucrio.tecgraf.springboot.openbus.properties.OpenBusPropertiesOrb;
import org.jacorb.orb.BasicAdapter;
import org.jacorb.orb.iiop.IIOPProfile;
import org.jacorb.util.Version;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import tecgraf.openbus.core.ORBInitializer;

import java.util.Properties;

public class ORBManagerDefault implements ORBManager {

    private Logger log = LoggerFactory.getLogger(ORBManagerDefault.class);

    private Properties orbProperties;
    private ORB orb;
    private POA poa;

    private Thread orbThread;

    public ORBManagerDefault(OpenBusPropertiesOrb openBusPropertiesOrb) {
        this.orbProperties = openBusPropertiesOrb.producer();
    }

    public void activatePOA() {
        try {
            log.info("Ativando POAManager");
            poa.the_POAManager().activate();
        }
        catch (UserException | SystemException e) {
            throw new RuntimeException("Erro ao tentar ativar o POAManager.", e);
        }
    }

    /**
     * Inicializa o object request broker.
     */
    public void startOrb() {
        log.info("Iniciando ORB");
        if (orb instanceof org.jacorb.orb.ORB) {
            org.jacorb.orb.ORB jacORB = (org.jacorb.orb.ORB)orb;
            BasicAdapter basicAdapter = jacORB.getBasicAdapter();
            log.info("O ORB sendo rodado é o jacORB " + Version.versionInfo);
            log.info("DOH? {}", basicAdapter.getEndpointProfiles().stream().map(profile -> ((IIOPProfile)profile).getAddress() + "").reduce(", ", String::concat));
            log.info("O serviço está sendo rodado localmente em {}:{}", basicAdapter.getAddress(), basicAdapter.getPort());
        }
        else {
            log.warn("O ORB sendo rodado não foi identificado: não serão informadas características locais do componente");
        }
        orbThread = new Thread(() -> orb.run(), "Thread-Default-JacORB-Openbus");
        orbThread.start();
    }

    public void shutdownOrb() {
        try {
            log.info("Realizando shutdown no ORB");
            if (orbThread != null) orbThread.interrupt();
            if (orb != null) {
                orb.shutdown(true);
                orb.destroy();
            }
        } catch (Exception e) {
            log.error("Erro ao se desconectar do servidor ORB", e);
        }
    }

    public ORB getORB() {
        if (orb == null) {
            log.info("Inicializando ORB");
            orb = ORBInitializer.initORB(new String[] {}, orbProperties);
        }
        return orb;
    }

    public POA getPOA() {
        try {
            if (poa == null) {
                log.info("Obtendo a referência para RootPOA");
                poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            }
            return poa;
        } catch (InvalidName invalidName) {
            throw new RuntimeException("Erro ao obter referência para o RootPOA", invalidName);
        }
    }

}
