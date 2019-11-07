package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;

@OpenBusService(name = "Service2", id = "IDL:Service1:2.0", servant = ServiceOperations2POATie.class)
public class Service2 implements ServiceOperations2Operations {

    public String greetsTwo() {
        return "Hello from two!";
    }

}
