package br.pucrio.tecgraf.springboot.openbus.register;

import org.omg.PortableServer.Servant;

/**
 * Existe uma impedância semântica entre o OpenBus e as idéias enterprise atuais que impede a incorporação
 * imediata de conceitos relacionados ao OpenBus quando é realizado o desenvolvimento. Essa abstração, bem como
 * o {@link RemoteApplication} faz a ponte para o imediato reconhecimento de itens arquiteturais.
 */
public interface RemoteService {

	/**
	 * Aplicação ao qual a faceta está associada
	 * @return aplicação remota (componente)
	 */
	RemoteApplication getApplication();

	/**
	 * Stub ao qual esse componente é representado e é chamado via RPC.
	 * @return servant orb
	 */
	Servant getServant();

	/**
	 * Nome da faceta completa representada no openbus (normalmente é o ID do Helper gerado no stub).
	 * @return nome da faceta
	 */
	String getId();

	/**
	 * Nome para ajudar a localização interna do componente
	 * @return
	 */
	String getName();

}