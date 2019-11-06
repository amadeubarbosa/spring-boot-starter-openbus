package br.pucrio.tecgraf.springboot.openbus.converters;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import tecgraf.openbus.exception.CryptographyException;
import tecgraf.openbus.security.Cryptography;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

@Component
@ConfigurationPropertiesBinding
public class FilePrivateKeyConverter implements Converter<String, RSAPrivateKey> {

    public RSAPrivateKey convert(String source) {
        try {
            return Cryptography.getInstance().readKeyFromFile(source);
        } catch (InvalidKeySpecException | IOException | CryptographyException e) {
            throw new RuntimeException(String.format("Ocorreu um erro ao ler a chave privada (%s).",
                    source), e);
        }
    }

}
