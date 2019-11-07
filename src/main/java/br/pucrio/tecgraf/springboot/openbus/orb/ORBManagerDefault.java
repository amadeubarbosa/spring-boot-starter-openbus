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

@Component
public class ORBManagerDefault implements ORBManager {

    private Logger log = LoggerFactory.getLogger(ORBManagerDefault.class);

    private Properties orbProperties;
    private ORB orb;
    private POA poa;

    public ORBManagerDefault(OpenBusPropertiesOrb openBusPropertiesOrb) {
        this.orbProperties = openBusPropertiesOrb.producer();
    }

    public void activatePOA() {
        try {
            poa.the_POAManager().activate();
        }
        catch (UserException | SystemException e) {
            throw new RuntimeException("Erro ao tentar ativar o POA raíz.", e);
        }
    }

    /**
     * Inicializa o object request broker.
     */
    public void startOrb() {
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
        orb.run();
    }


    public ORB getORB() {
        if (orb != null) log.warn("Já havia um ORB rodando e não foi desativado: esse procedimento pode gerar" +
                " ações inesperadas (o escopo de um ORB sempre deve ser singleton no spring, a não ser que você saiba" +
                " o que esteja fazendo)");
        orb = ORBInitializer.initORB(new String[] {}, orbProperties);
        return orb;
    }

    public POA getPOA() {
        try {
            poa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            return poa;
        } catch (InvalidName invalidName) {
            throw new RuntimeException("Erro ao ativar o RoorPOA", invalidName);
        }
    }

}
