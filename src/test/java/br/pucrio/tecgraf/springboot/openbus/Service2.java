package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import org.springframework.stereotype.Service;

@Service
@OpenBusService(name = "Service2", id = "IDL:br/pucrio/tecgraf/springboot/openbus/ServiceOperations2:1.0", servant = ServiceOperations2POATie.class)
public class Service2 implements ServiceOperations2Operations {

    public String greetsTwo() {
        return "Hello from two!";
    }

}
