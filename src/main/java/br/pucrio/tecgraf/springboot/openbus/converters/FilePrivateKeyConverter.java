package br.pucrio.tecgraf.springboot.openbus.converters;

import br.pucrio.tecgraf.springboot.crypt.KeyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

@Component
@ConfigurationPropertiesBinding
public class FilePrivateKeyConverter implements Converter<String, PrivateKey> {

    private KeyReader keyReader;
    private Logger log = LoggerFactory.getLogger(FilePrivateKeyConverter.class);

    public FilePrivateKeyConverter(KeyReader keyReader) {
        this.keyReader = keyReader;
    }

    public PrivateKey convert(String source) {
        try {
            return keyReader.loadPrivateKey(source);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException | IOException e) {
            throw new RuntimeException(String.format("Ocorreu um erro ao ler a chave privada (%s).",
                    source), e);
        }
    }

}
