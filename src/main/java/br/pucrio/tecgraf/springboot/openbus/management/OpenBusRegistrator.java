package br.pucrio.tecgraf.springboot.openbus.management;

public interface OpenBusRegistrator {

    void activatePOA();

    void initializeEngine();

    void registerServices();

    void startOrb();

    void stop();

}
