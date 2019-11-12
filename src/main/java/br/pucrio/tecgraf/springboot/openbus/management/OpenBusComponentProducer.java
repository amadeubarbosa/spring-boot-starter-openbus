package br.pucrio.tecgraf.springboot.openbus.management;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.springframework.beans.factory.annotation.Qualifier;
import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.exception.SCSException;

public class OpenBusComponentProducer implements Produtor<ComponentContext> {

    private ORB orb;
    private POA poa;
    private String componentName;
    private byte major;
    private byte minor;
    private byte patch;

    public OpenBusComponentProducer(ORB orb, POA poa,
                                    @Qualifier("componentName") String componentName,
                                    @Qualifier("major") byte major,
                                    @Qualifier("minor") byte minor,
                                    @Qualifier("patch") byte patch) {
        this.orb = orb;
        this.poa = poa;
        this.componentName = componentName;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public ComponentContext produces() throws SCSException {
        // A especificação da plataforma é uma concatenação de "java" + a versão da propriedade da versão de java
        String platformSpecification = String.format("java-%s", System.getProperty("java.specification.version"));
        return new ComponentContext(orb, poa, new ComponentId(
                componentName, major, minor, patch, platformSpecification));
    }

}
