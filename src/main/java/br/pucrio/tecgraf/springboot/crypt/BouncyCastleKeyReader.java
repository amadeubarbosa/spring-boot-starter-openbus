package br.pucrio.tecgraf.springboot.crypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

@Component
public class BouncyCastleKeyReader implements KeyReader {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public PrivateKey loadPrivateKey(String file) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        PemFile pemFile = new PemFile(file);
        byte[] content = pemFile.getPemObject().getContent();
        PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return factory.generatePrivate(privKeySpec);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IOException {
        PrivateKey privateKey = new BouncyCastleKeyReader().loadPrivateKey("/Users/valtoni/.ssh/id_rsa");
        System.out.println(privateKey);
    }

}
