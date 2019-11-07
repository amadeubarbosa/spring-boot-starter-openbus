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
@ConfigurationProperties(prefix = "openbus.services")
public class OpenBusPropertiesServices extends AbstractPropertiesFile {

}
