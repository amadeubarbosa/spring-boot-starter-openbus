package br.pucrio.tecgraf.springboot.openbus.management;

public interface OpenBusRegistrator {

    void initializeEngine();

    void registerServices();

    void stop();

}
