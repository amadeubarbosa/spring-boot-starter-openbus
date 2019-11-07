package br.pucrio.tecgraf.springboot.openbus;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableConfigurationProperties
@OpenBusApplication(value = "servico-teste", major = 1, minor = 0, patch = 0)
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}
