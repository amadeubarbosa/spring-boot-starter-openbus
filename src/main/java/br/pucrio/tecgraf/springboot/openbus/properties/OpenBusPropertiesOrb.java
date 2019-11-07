package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Component
@ConfigurationProperties(prefix = "openbus.orb")
public class OpenBusPropertiesOrb extends AbstractPropertiesFile {

}
