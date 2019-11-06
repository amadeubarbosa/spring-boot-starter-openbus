package br.pucrio.tecgraf.springboot.openbus.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OpenBusPostApplicationListener implements BeanPostProcessor {

	private Logger logger = LoggerFactory.getLogger(OpenBusPostApplicationListener.class);

	private OpenBusRegistrator openBusRegistrator;

	public OpenBusPostApplicationListener(OpenBusRegistrator openBusRegistrator) {
		this.openBusRegistrator = openBusRegistrator;
	}

	@EventListener
	public void onStart(ContextStartedEvent event) {
		// Ativa o poa raiz
		openBusRegistrator.activatePOA();
		// Cria o assistente
		openBusRegistrator.initializeEngine();
		// Register service
		openBusRegistrator.registerServices();
		// Inicializa o ORB
		openBusRegistrator.startOrb();
		System.out.println("APPLICATION ID: " + event.getApplicationContext().getId());
	}

	@EventListener
	public void onClose(ContextClosedEvent event) {
		// Sinaliza parada para o registro
		openBusRegistrator.stop();
	}

}
