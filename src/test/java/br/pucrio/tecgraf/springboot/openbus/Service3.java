package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;

@OpenBusService(name = "Service3", id = "IDL:br/pucrio/tecgraf/springboot/openbus/ServiceOperations3:1.0", servant = ServiceOperations3POATie.class)
public class Service3 implements ServiceOperations3Operations {

    public String greetsThree() {
        return "Hello from three!";
    }

}
