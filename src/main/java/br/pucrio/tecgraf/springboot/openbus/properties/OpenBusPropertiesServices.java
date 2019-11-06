package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class OpenBusPropertiesServices {

    @ConfigurationProperties(prefix = "openbus.services")
    class OpenBusPropertiesServicesInternal {
        private Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProducer() {
            return properties;
        }
    }

    @Configuration
    @EnableConfigurationProperties(OpenBusPropertiesServicesInternal.class)
    public class OpenBusServicesAssistantFactory {

        @Bean(name = "servicesProperties")
        public Map<String, String> producer(OpenBusPropertiesServicesInternal openBusPropertiesServicesInternal) {
            return openBusPropertiesServicesInternal.getProducer();
        }

    }

}
