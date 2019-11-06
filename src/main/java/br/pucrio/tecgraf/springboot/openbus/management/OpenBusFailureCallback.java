package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.OpenBusProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import scs.core.IComponent;
import tecgraf.openbus.assistant.Assistant;
import tecgraf.openbus.assistant.OnFailureCallback;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;
import tecgraf.openbus.core.v2_0.services.offer_registry.UnauthorizedFacets;

import java.util.Arrays;

@Component
class OpenBusFailureCallback implements OnFailureCallback {

	private Logger logger = LoggerFactory.getLogger(OpenBusFailureCallback.class);

	private OpenBusProperties openBusProperties;

	public OpenBusFailureCallback(OpenBusProperties openBusProperties) {
		this.openBusProperties = openBusProperties;
	}

	@Override
	public void onRegisterFailure(Assistant assistant, IComponent component, ServiceProperty[] properties,
		Exception exception) {
		String details = "";
		if (exception instanceof UnauthorizedFacets) {
			UnauthorizedFacets unauthorizedFacets = (UnauthorizedFacets) exception;
			details = String.format("(Facetas não autorizadas %s) ", Arrays.deepToString(unauthorizedFacets.facets));
		}
		logger.warn("Falha ao registrar componente nos OpenBus. {}Propriedades: {}", details, openBusProperties, exception);
	}

	@Override
	public void onLoginFailure(Assistant assistant, Exception exception) {
		logger.warn("O processo de login falhou. Propriedades: {}", openBusProperties, exception);
	}

	@Override
	public void onStartSharedAuthFailure(Assistant assistant, Exception exception) {
		logger.warn("A autenticação compartilhada falhou", exception);
	}

	@Override
	public void onFindFailure(Assistant assistant, Exception exception) {
		logger.warn("Falha no assistente do OpenBus.", exception);
	}

}