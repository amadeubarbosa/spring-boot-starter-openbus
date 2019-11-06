package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class OpenBusPropertiesOrb {

    @ConfigurationProperties(prefix = "openbus.orb")
    class OpenBusPropertiesOrbInternal {
        private Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProducer() {
            return properties;
        }
    }

    @Configuration
    @EnableConfigurationProperties(OpenBusPropertiesOrbInternal.class)
    public class OpenBusPropertiesOrbFactory {

        @Bean(name = "orbProperties")
        public Properties producer(OpenBusPropertiesOrbInternal openBusPropertiesOrbInternal) {
            Properties properties = new Properties();
            for (String key : openBusPropertiesOrbInternal.getProducer().keySet()) {
                properties.setProperty(key, openBusPropertiesOrbInternal.getProducer().get(key));
            }
            return properties;
        }

    }

}
