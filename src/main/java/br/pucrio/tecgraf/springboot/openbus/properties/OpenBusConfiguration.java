package br.pucrio.tecgraf.springboot.openbus.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
@Validated
@ConfigurationProperties("openbus")
public class OpenBusConfiguration {

    @NotNull(message = "Informe o endereço do openbus onde o serviço deverá ser registrado")
    private String address;
    private String name;
    @NotNull(message = "Informe a porta do serviço que será levantada localmente")
    private Integer port;
    private Duration retryInterval = Duration.of(6, ChronoUnit.SECONDS);
    @NotNull(message = "Informe a chave privada que dá acesso para que o serviço seja registrado no openbus")
    private PrivateKey privateKey;

    private String componentVersion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Duration getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(Duration retryInterval) {
        this.retryInterval = retryInterval;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }

    @Override
    public String toString() {
        return "OpenBusConfiguration{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", retryInterval=" + retryInterval +
                ", privateKey=" + privateKey +
                '}';
    }
}
