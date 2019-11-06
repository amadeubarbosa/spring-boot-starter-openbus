package br.pucrio.tecgraf.springboot.openbus;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@ConfigurationProperties("openbus.config")
@Component
public class OpenBusPropertiesCustom {

    private Map<String, String> values = new HashMap<>();

    public Map<String, String> getValues() {
        return this.values;
    }

    public Properties getProperties() {
        Properties properties = new Properties();
        for (String key : values.keySet()) {
            properties.setProperty(key, values.get(key));
        }
        return properties;
    }

}
