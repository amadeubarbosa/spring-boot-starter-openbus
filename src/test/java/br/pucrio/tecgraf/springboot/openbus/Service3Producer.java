package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Service3Producer {

    @Bean
    @OpenBusService(name = "Service3", id = "IDL:br/pucrio/tecgraf/springboot/openbus/ServiceOperations3:1.0", servant = ServiceOperations3POATie.class)
    public Service3 greetsTwo() {
        return new Service3();
    }

}
