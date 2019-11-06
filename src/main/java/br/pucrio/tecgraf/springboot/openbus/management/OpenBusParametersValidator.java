package br.pucrio.tecgraf.springboot.openbus.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OpenBusParametersValidator {

    private PropertyResolver propertyResolver;
    private ApplicationContext applicationContext;
    private Map<String, Object> parameters = new HashMap<>();

    @Autowired
    public OpenBusParametersValidator(Environment propertyResolver) {
        this.propertyResolver = propertyResolver;
        this.applicationContext = applicationContext;
        validate();
    }

    private void addParameter(String parameter) {
        parameters.put(parameter, propertyResolver.getProperty(parameter));
    }

    public void validate() {
        // Add parameters to be checked
        addParameter("openBusAddress");
        addParameter("openBusRetryInterval");
        addParameter("openBusPrivateKey");
        addParameter("openBusConfig");
        // Check them
        String emptyParameters = parameters.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(entry -> entry.getKey())
                .collect(Collectors.joining(", "));
        if (emptyParameters != null && !emptyParameters.isEmpty()) throw new RuntimeException("Openbus parameters wasn't declared: " + emptyParameters);
    }

}
