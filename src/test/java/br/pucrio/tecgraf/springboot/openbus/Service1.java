package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import org.springframework.stereotype.Service;

@Service
@OpenBusService(name = "Service1", id = "IDL:br/pucrio/tecgraf/springboot/openbus/ServiceOperations1:1.0", servant = ServiceOperations1POATie.class)
public class Service1 implements ServiceOperations1Operations {

    public String greetsOne() {
        return "Hello from one!";
    }

}
