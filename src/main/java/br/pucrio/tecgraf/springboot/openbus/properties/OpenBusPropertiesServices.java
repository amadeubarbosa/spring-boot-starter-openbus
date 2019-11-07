package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// Camel-case não são mais permitidos: no springboot >2.0 o kebab-case é obrigatório
@Configuration
@ConfigurationProperties(prefix = "openbus.services")
@PropertySource("classpath:application.properties")
@Component
public class OpenBusPropertiesServices extends AbstractPropertiesFile {

    private boolean traduzir;

    public boolean isTraduzir() {
        return traduzir;
    }

    public void setTraduzir(boolean traduzir) {
        this.traduzir = traduzir;
    }

}
