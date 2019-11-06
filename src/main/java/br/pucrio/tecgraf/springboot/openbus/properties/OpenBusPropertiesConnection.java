package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

// TODO Consertar para autoconfiguration

@Configuration
public class OpenBusPropertiesConnection {

    @ConfigurationProperties(prefix = "openbus.connection")
    class OpenBusPropertiesConnectionInternal {
        private Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProducer() {
            return properties;
        }
    }

    @Configuration
    @EnableConfigurationProperties(OpenBusPropertiesConnectionInternal.class)
    public class OpenBusPropertiesAssistantFactory {

        @Bean(name = "connectionProperties")
        public Properties producer(OpenBusPropertiesConnectionInternal openBusPropertiesConnectionInternal) {
            Properties properties = new Properties();
            for (String key : openBusPropertiesConnectionInternal.getProducer().keySet()) {
                properties.setProperty(key, openBusPropertiesConnectionInternal.getProducer().get(key));
            }
            return properties;
        }

    }

}
