package br.pucrio.tecgraf.springboot.crypt;

import org.springframework.stereotype.Component;
import tecgraf.openbus.exception.CryptographyException;
import tecgraf.openbus.security.Cryptography;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

@Component
public class OpenBusKeyReader implements KeyReader {

    @Override
    public PrivateKey loadPrivateKey(String file) throws IOException, InvalidKeySpecException, CryptographyException {
        return Cryptography.getInstance().readKeyFromFile(file);
    }

}
