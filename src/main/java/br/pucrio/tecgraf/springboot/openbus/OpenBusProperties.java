package br.pucrio.tecgraf.springboot.openbus;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ConfigurationProperties("openbus")
@Validated
@Component
public class OpenBusProperties {

    @NotNull(message = "Informe o endereço do openbus onde o serviço deverá ser registrado")
    private String address;
    @NotNull(message = "Informe o nome do serviço a ser registrado")
    private String name;
    @NotNull(message = "Informe a porta do serviço que será levantada localmente")
    private Integer port;
    private Duration retryInterval = Duration.of(6, ChronoUnit.SECONDS);
    @NotNull(message = "Informe a chave privada que dá acesso para que o serviço seja registrado no openbus")
    private RSAPrivateKey privateKey;

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

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        return "OpenBusProperties{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", retryInterval=" + retryInterval +
                ", privateKey=" + privateKey +
                '}';
    }
}
