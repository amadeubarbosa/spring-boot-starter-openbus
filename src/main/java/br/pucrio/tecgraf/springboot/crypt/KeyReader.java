package br.pucrio.tecgraf.springboot.crypt;

import tecgraf.openbus.exception.CryptographyException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public interface KeyReader {

    PrivateKey loadPrivateKey(String file) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, CryptographyException;

}
