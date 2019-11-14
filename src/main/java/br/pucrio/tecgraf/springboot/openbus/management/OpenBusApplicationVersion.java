package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.autoconfigure.OpenBusApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpenBusApplicationVersion {

    private static final Pattern PATTERN_DIGITS = Pattern.compile("\\d+");
    public static final String OPEN_BUS_COMPONENT_VERSION = "OpenBus-Component-Version";

    private static Logger log = LoggerFactory.getLogger(OpenBusApplicationVersion.class);

    private boolean verified;

    private byte major;
    private byte minor;
    private byte patch;

    /**
     * Lê o arquivo de MANIFESTO para ler a versão
     */
    public boolean readManifest() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Enumeration<URL> manifestResources = classLoader.getResources("META-INF/MANIFEST.MF");
            while (manifestResources.hasMoreElements()) {
                InputStream inputStream = manifestResources.nextElement().openStream();
                Manifest manifest = new Manifest(inputStream);
                String version = manifest.getMainAttributes().getValue(OPEN_BUS_COMPONENT_VERSION);
                if (version == null) {
                    assumesEmptyVersions();
                }
                else {
                    assumesVersion(version);
                }
            }
        }
        catch (Exception ex) {
            assumesEmptyVersions();
        }
        return verified;
    }

    /**
     * Efetua um parse no parâmetro para estabelecer a versão.
     * Qualquer string dentro dos itens será retirada.
     * @param version versão a ser parseada (ex.: 1.1.2-SNAPSHOT virará 1.1.2)
     */
    public boolean applyVersion(String version) {
        return assumesVersion(version);
    }

    private boolean assumesVersion(String version) {
        log.trace("Atribuindo a versão {}", version);
        if (version == null || version.equals("") || version.split(".").length != 3) {
            log.trace("A versão {} não é válida", version);
            assumesEmptyVersions();
        }
        else {
            byte[] arrayedVersion = matchOnlyNumbers(version.split("."));
            this.major = arrayedVersion[0];
            this.minor = arrayedVersion[1];
            this.patch = arrayedVersion[2];
            verified = true;
        }
        return verified;
    }

    private byte matchByte(String input) {
        StringBuffer result = new StringBuffer("");
        Matcher matcher = PATTERN_DIGITS.matcher(input);
        while (matcher.find()) {
            result.append(matcher.group());
        }
        return result.toString().isEmpty() ? 0 : Byte.parseByte(result.toString());
    }

    private byte[] matchOnlyNumbers(String[] arrayedVersion) {
        byte[] items = new byte[3];
        for (int i = 0; i < 3; i++) {
            items[i] = matchByte(arrayedVersion[i]);
        }
        return items;
    }

    public boolean applyVersion(byte major, byte minor, byte patch) {
        if (major == -1 || minor == -1 || patch == -1) {
            return verified;
        }
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        verified = true;
        return verified;
    }

    private void assumesEmptyVersions() {
        log.trace("Assumindo os valores padrão para aplicação openbus 0.0.0");
        this.major = 0;
        this.minor = 0;
        this.patch = 0;
    }

    public byte getMajor() {
        return major;
    }

    public byte getMinor() {
        return minor;
    }

    public byte getPatch() {
        return patch;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

    /**
     * Cuidado em utilizar em várias threads essa classe.
     */
    public static class Builder {

        private OpenBusApplicationVersion instance = new OpenBusApplicationVersion();
        private boolean ready;

        public Builder readManifest() {
            if (!ready) ready = instance.readManifest();
            return this;
        }

        public Builder applyVersion(String version) {
            if (!ready) ready = instance.applyVersion(version);
            return this;
        }

        public Builder applyVersion(byte major, byte minor, byte patch) {
            if (!ready) ready = instance.applyVersion(major, minor, patch);
            return this;
        }

        public OpenBusApplicationVersion version() {
            if (!instance.verified) {
                log.warn("Não foi detectada nenhuma versão disponível (utilize a anotação {} para modificar a " +
                         "versão ou crie a entrada {} no arquivo de manifesto)", OpenBusApplication.class.getName(),
                         OPEN_BUS_COMPONENT_VERSION);
            }
            log.info("Versão assumida do componente: {}", instance.toString());
            return instance;
        }

    }

}
