package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@OpenBusService(name = "Service1", id = "IDL:br/pucrio/tecgraf/springboot/openbus/ServiceOperations1:1.0", servant = ServiceOperations1POATie.class)
public class Service1 implements ServiceOperations1Operations {

    private Logger log = LoggerFactory.getLogger(Service1.class);

    public String greetsOne() {
            log.info("Chamando o log 1 *********** {}", "LEGAL");
        return "Hello from one!";
    }

}
