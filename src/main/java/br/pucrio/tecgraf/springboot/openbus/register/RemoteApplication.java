package br.pucrio.tecgraf.springboot.openbus.register;

import br.pucrio.tecgraf.springboot.openbus.management.OpenBusServicesRegistry;
import scs.core.ComponentContext;
import scs.core.IComponent;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

/**
 * Essa abstração representa um Componente para o openbus.
 * Existe uma impedância semântica entre o OpenBus e as idéias enterprise atuais que impede a incorporação
 * imediata de conceitos relacionados ao OpenBus quando é realizado o desenvolvimento. Essa abstração, bem como
 * o {@link RemoteService} faz a ponte para o imediato reconhecimento de itens arquiteturais.
 */
public interface RemoteApplication extends OpenBusServicesRegistry {

    /**
     * Obtém o contexto de componente remoto que representa uma aplicação no openbus.
     * @return componente openbus
     */
    ComponentContext getComponentContext();

    /**
     * Obtém as propriedades relacionadas à essa aplicação.
     * @return propriedades relacionadas
     */
    ServiceProperty[] getApplicationProperties();

}
