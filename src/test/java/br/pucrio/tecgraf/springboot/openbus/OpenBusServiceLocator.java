package br.pucrio.tecgraf.springboot.openbus;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jacorb.orb.ORBSingleton;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.TRANSIENT;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scs.core.IComponent;
import tecgraf.openbus.OpenBusContext;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.AssistantParams;
import tecgraf.openbus.assistant.OnFailureCallback;
import tecgraf.openbus.core.v2_0.services.access_control.InvalidRemoteCode;
import tecgraf.openbus.core.v2_0.services.access_control.LoginInfo;
import tecgraf.openbus.core.v2_0.services.access_control.NoLoginCode;
import tecgraf.openbus.core.v2_0.services.access_control.UnknownBusCode;
import tecgraf.openbus.core.v2_0.services.access_control.UnverifiedLoginCode;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceOfferDesc;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;
import tecgraf.openbus.exception.CryptographyException;
import tecgraf.openbus.security.Cryptography;

/**
 * Conjunto de métodos de uso recorrente na infraestrutura SCS/Openbus
 *
 */
public final class OpenBusServiceLocator {

    private final Logger logger = LoggerFactory.getLogger(OpenBusServiceLocator.class);
    private Assistant assistant;
    private final AtomicBoolean finalized = new AtomicBoolean(false);
    private POA poa;
    private String openbusAddress = null;
    private String openbusEntity = null;
    private int openbusPort = -1;
    private PrivateKey privateKey = null;

    /**
     * Obtém o serviço do barramento que tenha o nome da faceta e entidade passados por parâmetro.
     *
     * @param entity
     * @param facet
     * @return
     */
    public <T> T locate(Class<T> type, String entity, String facet) {
        logger.debug("Procurando por openbus.offer.entity '{}' e openbus.component.facet '{}'", entity, facet);
        ServiceOfferDesc[] descs;
        try {
            descs =
                    getAssistant().findServices(new ServiceProperty[] {
                            new ServiceProperty("openbus.offer.entity", entity) }, 1);
        }
        catch (Throwable e) {
            throw new RuntimeException(String.format("Ocorreu um erro ao procurar pela entidade %s e faceta %s",
                    entity, facet), e);
        }

        if ((descs == null) || (descs.length == 0)) {
            throw new RuntimeException(String.format("Nenhuma oferta válida encontrada com entidade %s e faceta %s",
                    entity, facet));
        }

        // Busca por oferta válida entre as encontradas
        for (ServiceOfferDesc offer: descs) {
            try {
                offer.service_ref.getComponentId();
                Object object = offer.service_ref.getFacetByName(facet);
                if (object == null) throw new RuntimeException("Não foi possível encontrar a faceta %s");
                return narrowCorbaObject(type, object);
            }
            catch (TRANSIENT e) {
                throw new RuntimeException("Erro transiente (O servidor corba está disponível?)", e);
            }
            catch (COMM_FAILURE e) {
                throw new RuntimeException("Falha de comunicação com o OpenBus", e);
            }
            catch (NO_PERMISSION e) {
                String message = "Falha de permissão ";
                switch (e.minor) {
                    case NoLoginCode.value:
                        message += String.format("Não há um login de %s válido no momento", facet);
                        break;
                    case UnknownBusCode.value:
                        message += "O serviço encontrado não está mais logado ao barramento.";
                        break;
                    case UnverifiedLoginCode.value:
                        message += "O serviço encontrado não foi capaz de validar a chamada.";
                        break;
                    case InvalidRemoteCode.value:
                        message += "Integração do serviço encontrado com o barramento está incorreta.";
                        break;
                }
                throw new RuntimeException(message, e);
            }
        }
        // Nenhuma oferta válida
        throw new RuntimeException(String.format("Nenhuma oferta válida encontrada com entidade %s e faceta %s",
                entity, facet));
    }

    private <T> T narrowCorbaObject(Class<T> type, org.omg.CORBA.Object object) {
        String helper = type.getName() + "Helper";
        Class helperClass;
        Method method;
        try {
            helperClass = Class.forName(helper);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("A classe helper " + helper + " não foi encontrada");
        }
        try {
            method = helperClass.getDeclaredMethod("narrow", org.omg.CORBA.Object.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("O método de narrow não foi encontrado na classe helper (foi gerado por um ORB?)");
        }
        if (!method.getReturnType().equals(type)) {
            throw new RuntimeException("O narrow do método helper retorna um tipo diferente (" +
                    method.getReturnType() + ") do informado (" + type.getName() + ")");
        }
        try {
            return (T) method.invoke(null, object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Impossível acessar o tipo " + type.getName(), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Erro ao efetuar narrow", e);
        }
    }

    /**
     * Loga as informações da cadeia de chamadas do OpenBus 2.0
     *
     * @return
     */
    public final String getCallChain() {
        final OpenBusContext context;
        try {
            context = (OpenBusContext) getORB().resolve_initial_references("OpenBusContext");
        }
        catch (InvalidName e) {
            throw new IllegalStateException(e);
        }

        String message = "";
        for (LoginInfo info : context.getCallerChain().originators()) {
            message += info.entity + "->";
        }

        if (!message.isEmpty()) {
            message += context.getCallerChain().caller().entity;
        }
        else {
            message = context.getCallerChain().caller().entity;
        }

        return message;
    }

    /**
     * Desconecta do barramento.
     */
    public synchronized void disconnect() {
        logger.trace("Desconectando do barramento");
        finalized.set(true);
        destroy(assistant);
    }

    /**
     * Destrói instancia fornecida do assistant
     *
     * @param assistant
     */
    public synchronized void destroy(Assistant assistant) {
        try {
            logger.trace("Destruindo Assistant que está {}", assistant != null ? "ativo" : "inativo");
            if (assistant != null) {
                assistant.shutdown();
                if (assistant.orb() != null) {
                    assistant.orb().shutdown(true);
                    assistant.orb().destroy();
                }
            }
        }
        catch (Throwable e) {
            final String message = String.format("Erro ao se desconectar do OpenBus (%s:%d)", openbusAddress, openbusPort);
            logger.warn(message, e);
        }
        finally {
            assistant = null;
        }
    }

    protected synchronized Assistant getAssistant() {
        while (assistant == null && !finalized.get()) {
            try {
                connectNewAssistant(false);
            }
            catch (Throwable e) {
                throw new IllegalStateException("Falha ao (re)conectar ao OpenBus", e);
            }
        }
        if (finalized.get())
            throw new IllegalStateException("Sistema já finalizado.");
        return assistant;
    }

    /**
     * Cria um cliente openbus.
     *
     * @param host
     * @param port
     * @param entity
     * @param key
     * @return
     * @throws AdapterInactive
     * @throws InvalidName
     * @throws InterruptedException
     */
    public OpenBusServiceLocator(String host, int port, String entity, PrivateKey key)
            throws AdapterInactive, InterruptedException {
        if (assistant == null) {
            openbusAddress = host;
            openbusPort = port;
            openbusEntity = entity;
            privateKey = key;
            connectNewAssistant(true);
        }
    }

    public OpenBusServiceLocator(String host, int port, String entity, String key)
            throws AdapterInactive, InterruptedException, CryptographyException, InvalidKeySpecException, IOException {
        if (assistant == null) {
            openbusAddress = host;
            openbusPort = port;
            openbusEntity = entity;
            privateKey = Cryptography.getInstance().readKeyFromFile(key);
            connectNewAssistant(true);
        }
    }

    /**
     * Cria uma nova conexão com um assistente Openbus.
     *
     * @param force True para forçar a criação de novo assistente, destruindo um antigo ativo, se houver. False faz com
     *        que, se houver um assistente, não fazer nada.
     * @throws InterruptedException
     * @throws AdapterInactive
     */
    private synchronized void connectNewAssistant(boolean force) throws InterruptedException, AdapterInactive {

        try {

            if (!force && assistant != null) {
                OpenBusContext ctx = (OpenBusContext) assistant.orb().resolve_initial_references("OpenBusContext");
                if (ctx.getDefaultConnection() == null || ctx.getDefaultConnection().login() == null) {
                    logger.info("Assistente atual não conectado. Destruindo o atual para se conectar novamente.");
                    destroy(assistant);
                }
                else
                    // Já conectado. Como force == false, retorna sem fazer nada.
                    return;
            }

            destroy(assistant);

            AssistantParams params = createORBProperties();
            assistant = Assistant.createWithPrivateKey(openbusAddress, openbusPort, openbusEntity,
                    (RSAPrivateKey)privateKey, params);

            OpenBusContext ctx = (OpenBusContext) assistant.orb().resolve_initial_references("OpenBusContext");
            int secs = 0;
            while (ctx.getDefaultConnection() == null || ctx.getDefaultConnection().login() == null) {
                if (secs > 5) {
                    logger.info("Aguardando o assistente OpenBus se autenticar com sucesso (" + secs + " segundos...)");
                    Thread.sleep(1000); // Espera mais um segundo.
                    secs += 1;
                }
            }
            if (secs > 5)
                logger.info("Autenticação do assistente detectada!");

            poa = POAHelper.narrow(assistant.orb().resolve_initial_references("RootPOA"));
        }
        catch (InvalidName e) {
            throw new IllegalStateException("Falha interna grave.", e);
        }
        poa.the_POAManager().activate();
    }

    /**
     * Cria o ORB e seta suas propriedades e os interceptadores da BIAEP para
     * fazer delegação por confiança.
     *
     * @return
     */
    private AssistantParams createORBProperties() {

        // propriedades gerais para o ORB
        final Properties properties = new Properties();
        properties.setProperty("org.omg.CORBA.ORBClass", ORB.class.getName());
        properties.setProperty("org.omg.CORBA.ORBSingletonClass",
                ORBSingleton.class.getName());

        // propriedades para tratamento correto de conversão de charsets
        // automática no ORB
        properties.setProperty("jacorb.codeset", "on");
        properties.setProperty("jacorb.native_char_codeset", "UTF_8");
        properties.setProperty("jacorb.deferredArrayQueue", "0");
        // jacorb.poa.thread_pool_max=20
        // jacorb.poa.thread_pool_min=5
        // jacorb.poa.queue_max=100
        // jacorb.connection.client.connect_timeout=0
        properties.setProperty("jacorb.poa.thread_pool_max", "150");
        properties.setProperty("jacorb.poa.queue_max", "300");
        properties.setProperty("jacorb.connection.client.connect_timeout", "2000");

        // compatibilizando a criação do ORB com a API do OpenBus SDK
        final ORB orb =
                tecgraf.openbus.core.ORBInitializer.initORB(null, properties);

        AssistantParams params = new AssistantParams();
        params.orb = orb;
        params.callback = createOnFailureCallBack();

        //registerValueFactories((org.jacorb.orb.ORB) orb);
        return params;
    }

    /**
     * @return
     *
     *         TODO: precisamos pensar em quais execuções (situações de runtime)
     *         precisamos parar o assistant e anular sua referência
     */
    private OnFailureCallback createOnFailureCallBack() {
        return new OnFailureCallback() {
            @Override
            public void onLoginFailure(Assistant assistant, Exception except) {
                destroy(assistant);
                logger.error("Falha ao tentar se logar com o barramento.", except);
            }

            @Override
            public void onRegisterFailure(Assistant assistant, IComponent component, ServiceProperty[] properties,
                                          Exception except) {
                destroy(assistant);
                logger.error("Falha ao tentar se registrar no barramento.", except);
            }

            @Override
            public void onFindFailure(Assistant assistant, Exception except) {
                destroy(assistant);
                logger.error("Falha ao tentar se logar no barramento.", except);
            }

            @Override
            public void onStartSharedAuthFailure(Assistant assistant, Exception except) {
                logger.error("Falha ao tentar se logar no barramento.", except);
            }
        };
    }

    /**
     * @return
     */
    public POA getPOA() {
        if (poa != null) {
            return poa;
        }
        try {
            poa = POAHelper.narrow(getORB().resolve_initial_references("RootPOA"));
            poa.the_POAManager().activate();
        }
        catch (InvalidName | AdapterInactive e) {
            logger.warn(e.getMessage(), e);
        }
        return poa;
    }

    /**
     * Tenta retornar o usuário por delegação. Se não houver, recupera a última
     * entidade da cadeia de chamadas.
     *
     * @return
     */
    public String getLoggedUserID() {

        try {

            if (assistant == null) {
                logger.info("A conexão com o barramento ainda não foi estabelecida.");
                return null;
            }

            OpenBusContext context = (OpenBusContext) getORB().resolve_initial_references("OpenBusContext");

            String user = null;

            if (context.getCallerChain().originators().length >= 1) {
                return context.getCallerChain().originators()[0].entity;
            }

            return context.getCallerChain().caller().entity;

        } catch (InvalidName e) {
            logger.error("Nome inválido", e);
            return null;
        }
    }

    /**
     * @param privateKeyFile
     * @return
     */
    public RSAPrivateKey readPrivateKey(File privateKeyFile) {
        try {
            return Cryptography.getInstance().readKeyFromFile(privateKeyFile.getAbsolutePath());
        }
        catch (final GeneralSecurityException | IOException | CryptographyException e) {
            final StringWriter stringWriter = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(stringWriter);
            printWriter.printf("Erro ao ler a chave privada\n");
            printWriter.printf("Arquivo: %s.\n", privateKeyFile.getAbsolutePath());
            final String message = stringWriter.toString();
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Registra value factories necessárias para a BIAEP
     *
     * @param orb
     */
    private void registerValueFactories(org.jacorb.orb.ORB orb) {
        throw new UnsupportedOperationException("registerValueFactories: Not yet implemented");
    }


    /**
     * Recuperar o ORB atual
     *
     * @return referência ao ORB para uso.
     */
    public ORB getORB() {
        return getAssistant().orb();
    }

}
