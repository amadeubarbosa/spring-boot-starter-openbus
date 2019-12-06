package br.pucrio.tecgraf.springboot.openbus.orb;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

public interface ORBManager {

    void startOrb();

    void shutdownOrb();

    void activatePOA();

    POA getPOA();

    ORB getORB();

}
