package br.pucrio.tecgraf.springboot.openbus.properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractPropertiesFile {

    private Map<String, String> properties = new HashMap<>();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Properties producer() {
        Properties properties = new Properties();
        for (String key : this.properties.keySet()) {
            properties.setProperty(key, this.properties.get(key));
        }
        return properties;
    }

}
