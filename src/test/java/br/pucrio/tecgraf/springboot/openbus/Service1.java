package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;

@OpenBusService(name = "Service1", interfaceName = "IDL:Service1:1.0")
public class Service1 {

    public String greetsOne() {
        return "Hello from one!";
    }

}
