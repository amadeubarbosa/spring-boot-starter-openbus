package br.pucrio.tecgraf.springboot.openbus.orb;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tecgraf.openbus.core.ORBInitializer;

import java.util.Properties;

@Configuration
public class JacorbORBConfiguration {

    // TODO Essa classe não deve existir: ela deve ser um componente para entrar em OpenBusAssistantRegistrator (ou externamente)

    @Bean(name = "orb")
    public ORB produceORB(@Qualifier("orbProperties") Properties orbProperties) {
        return ORBInitializer.initORB(new String[] {}, orbProperties);
    }

    @Bean(name = "poa")
    public POA producesORBpo(ORB orb) throws InvalidName {
        return POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
    }

}
