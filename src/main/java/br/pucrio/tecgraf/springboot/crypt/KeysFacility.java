package br.pucrio.tecgraf.springboot.crypt;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.pkcs.PKCS8Key;
import sun.security.pkcs10.PKCS10;
import sun.security.x509.X500Name;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * @author Desphilboy
 * DorOd bar shomA barobach
 */
public class KeysFacility {

    private static PublicKey publickey = null;
    private static PrivateKey privateKey = null;
    private static KeyPairGenerator kpg = null;
    private static ByteArrayOutputStream bs = null;
    private static KeysFacility instance;
    private KeyPair keypair;
    private static PKCS10 pkcs10;
    private String signatureAlgorithm = "MD5WithRSA";

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }


    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    private KeysFacility() {
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.print("No such algorithm RSA in constructor csrgenerator\n");
        }
        kpg.initialize(2048);
        keypair = kpg.generateKeyPair();
        publickey = keypair.getPublic();
        privateKey = keypair.getPrivate();
    }


    /**
     * Generates a new key pair
     *
     * @param bits int
     *             this is the number of bits in modulus must be 512, 1024, 2048  or so on
     */
    public KeyPair generateRSAKeys(int bits) {
        kpg.initialize(bits);
        keypair = kpg.generateKeyPair();
        publickey = keypair.getPublic();
        privateKey = keypair.getPrivate();
        KeyPair dup = keypair;
        return dup;
    }

    public static KeysFacility getInstance() {
        if (instance == null)
            instance = new KeysFacility();
        return instance;
    }


    /**
     * Returns a CSR as string
     *
     * @param commonName       Common Name
     * @param organizationUnit Organizational Unit
     * @param organization     Organization
     * @param localName        Location name
     * @param stateName        State/Territory/Province/Region
     * @param country          Country
     * @return returns csr as string.
     * @throws Exception
     */
    public String getCSR(String commonName, String organizationUnit, String organization, String localName,
                         String stateName, String country) throws Exception {
        byte[] csr = generatePKCS10(commonName, organizationUnit, organization, localName, stateName, country,
                signatureAlgorithm);
        return new String(csr);
    }

    /**
     * This function generates a new Certificate
     * Signing Request.
     *
     * @param CN Common Name, is X.509 speak for the name that distinguishes
     *           the Certificate best, and ties it to your Organization
     * @param OU Organizational unit
     * @param O  Organization NAME
     * @param L  Location
     * @param S  State
     * @param C  Country
     * @return byte stream of generated request
     * @throws Exception
     */
    private static byte[] generatePKCS10(String CN, String OU, String O, String L, String S, String C, String sigAlg) throws Exception {
        // generate PKCS10 certificate request

        pkcs10 = new PKCS10(publickey);
        Signature signature = Signature.getInstance(sigAlg);
        signature.initSign(privateKey);
        // common, orgUnit, org, locality, state, country
        //X500Name(String commonName, String organizationUnit,String organizationName,Local,State, String country)
        X500Name x500Name = new X500Name(CN, OU, O, L, S, C);
        pkcs10.encodeAndSign(x500Name, signature);
        bs = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bs);
        pkcs10.print(ps);
        byte[] c = bs.toByteArray();
        try {
            if (ps != null)
                ps.close();
            if (bs != null)
                bs.close();
        } catch (Throwable th) {
        }
        return c;
    }

    public PublicKey getPublicKey() {
        return publickey;
    }


    /**
     * @return
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * saves private key to a file
     *
     * @param filename
     */
    public void savePrivateKey(String filename) {
        PKCS8EncodedKeySpec pemcontents = null;
        pemcontents = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        PKCS8Key pemprivatekey = new PKCS8Key();
        try {
            pemprivatekey.decode(pemcontents.getEncoded());
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File file = new File(filename);
        try {

            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(pemprivatekey.getEncoded());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    /**
     * Saves Certificate Signing Request to a file;
     *
     * @param filename is a String containing full path to the file which will be created containing the CSR.
     */
    public void saveCSR(String filename) {
        FileOutputStream fos = null;
        PrintStream ps = null;
        File file;
        try {

            file = new File(filename);
            file.createNewFile();
            fos = new FileOutputStream(file);
            ps = new PrintStream(fos);
        } catch (IOException e) {
            System.out.print("\n could not open the file " + filename);
        }

        try {
            try {
                pkcs10.print(ps);
            } catch (SignatureException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ps.flush();
            ps.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.print("\n cannot write to the file " + filename);
            e.printStackTrace();

        }

    }


    /**
     * Saves both public key and private  key to file names specified
     *
     * @param publicKeyFile file name of public key
     * @param privateKeyFile file name of private key
     * @throws IOException
     */
    public static void saveKeyPair(String publicKeyFile, String privateKeyFile) throws IOException {

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publickey.getEncoded());
        FileOutputStream fos = new FileOutputStream(publicKeyFile);
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        fos = new FileOutputStream(privateKeyFile);
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }


    /**
     * Reads a Private Key from a pem base64 encoded fileName.
     *
     * @param fileName  name of the fileName to read.
     * @param algorithm Algorithm is usually "RSA"
     * @return returns the privatekey which is read from the fileName;
     * @throws Exception
     */
    public PrivateKey getPemPrivateKey(String fileName, String algorithm) throws Exception {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) file.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String privKeyPEM = temp.replace("-----BEGIN PRIVATE KEY-----", "");
        privKeyPEM = privKeyPEM.replace("-----END PRIVATE KEY-----", "");
        //System.out.println("Private key\n"+privKeyPEM);

        BASE64Decoder b64 = new BASE64Decoder();
        byte[] decoded = b64.decodeBuffer(privKeyPEM);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }


    /**
     * Saves the private key to a pem file.
     *
     * @param fileName name of the file to write the key into
     *                the Private key to save.
     * @return String representation of the pkcs8 object.
     * @throws Exception
     */
    public String savePemPrivateKey(String fileName) throws Exception {
        PrivateKey key = this.privateKey;
        File f = new File(fileName);
        FileOutputStream fos = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fos);


        byte[] keyBytes = key.getEncoded();
        PKCS8Key pkcs8 = new PKCS8Key();
        pkcs8.decode(keyBytes);
        byte[] b = pkcs8.encode();

        BASE64Encoder b64 = new BASE64Encoder();
        String encoded = b64.encodeBuffer(b);

        encoded = "-----BEGIN PRIVATE KEY-----\r\n" + encoded + "-----END PRIVATE KEY-----";

        dos.writeBytes(encoded);
        dos.flush();
        dos.close();

        return pkcs8.toString();
    }


    /**
     * Saves a public key to a base64 encoded pem file
     *
     * @param filename name of the file public key to be saved
     * @return string representation of the pkcs8 object.
     * @throws Exception
     */
    public String savePemPublicKey(String filename) throws Exception {
        PublicKey key = this.publickey;
        File f = new File(filename);
        FileOutputStream fos = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fos);


        byte[] keyBytes = key.getEncoded();
        BASE64Encoder b64 = new BASE64Encoder();
        String encoded = b64.encodeBuffer(keyBytes);

        encoded = "-----BEGIN PUBLIC KEY-----\r\n" + encoded + "-----END PUBLIC KEY-----";

        dos.writeBytes(encoded);
        dos.flush();
        dos.close();

        return encoded.toString();

    }


    /**
     * Reads a public key from a file
     *
     * @param filename  name of the file to read
     * @param algorithm is usually RSA
     * @return the read public key
     * @throws Exception
     */
    public PublicKey getPemPublicKey(String filename, String algorithm) throws Exception {
        File f = new File(filename);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        dis.close();

        String temp = new String(keyBytes);
        String publicKeyPEM = temp.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");


        BASE64Decoder b64 = new BASE64Decoder();
        byte[] decoded = b64.decodeBuffer(publicKeyPEM);

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }


    public static void main(String[] args) throws Exception {
        KeysFacility gcsr = KeysFacility.getInstance();
        gcsr.setSignatureAlgorithm("SHA512WithRSA");
        System.out.println("Public Key:\n" + gcsr.getPublicKey().toString());

        System.out.println("Private Key:\nAlgorithm: " + gcsr.getPrivateKey().getAlgorithm().toString());
        System.out.println("Format:" + gcsr.getPrivateKey().getFormat().toString());
        System.out.println("To String :" + gcsr.getPrivateKey().toString());
        System.out.println("GetEncoded :" + gcsr.getPrivateKey().getEncoded().toString());
        BASE64Encoder encoder = new BASE64Encoder();
        String s = encoder.encodeBuffer(gcsr.getPrivateKey().getEncoded());
        System.out.println("Base64:" + s + "\n");

        String csr = gcsr.getCSR("desphilboy@yahoo.com", "baxshi az xodam", "Xodam", "PointCook", "VIC", "AU");
        System.out.println("CSR Request Generated!!");
        System.out.println(csr);
        gcsr.saveCSR("c:\\testdir\\javacsr.csr");
        String p = gcsr.savePemPrivateKey("c:\\testdir\\java_private.pem");
        System.out.print(p);
        p = gcsr.savePemPublicKey("c:\\testdir\\java_public.pem");
        privateKey = gcsr.getPemPrivateKey("c:\\testdir\\java_private.pem", "RSA");
        BASE64Encoder encoder1 = new BASE64Encoder();
        String s1 = encoder1.encodeBuffer(gcsr.getPrivateKey().getEncoded());
        System.out.println("Private Key in Base64:" + s1 + "\n");
        System.out.print(p);
    }

}