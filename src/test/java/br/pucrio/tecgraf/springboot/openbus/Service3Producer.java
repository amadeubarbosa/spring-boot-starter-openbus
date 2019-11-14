package br.pucrio.tecgraf.springboot.openbus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Service3Producer {

    @Bean
    public Service3 greetsTwo() {
        return new Service3();
    }

}
