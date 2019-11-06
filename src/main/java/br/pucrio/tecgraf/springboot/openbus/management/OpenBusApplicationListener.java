package br.pucrio.tecgraf.springboot.openbus.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OpenBusApplicationListener {

	private Logger logger = LoggerFactory.getLogger(OpenBusApplicationListener.class);

	private OpenBusRegistrator openBusRegistrator;

	public OpenBusApplicationListener(BeanFactory beanFactory) {
		// Instantiate registrator bean
		openBusRegistrator = beanFactory.getBean(OpenBusRegistrator.class);
	}

	@EventListener
	public void onStart(ContextStartedEvent event) {
		System.out.println("APPLICATION ID: " + event.getApplicationContext().getId());
		// Ativa o poa raiz
		openBusRegistrator.activatePOA();
		// Cria o assistente
		openBusRegistrator.initializeEngine();
		// Register service
		openBusRegistrator.registerServices();
		// Inicializa o ORB
		openBusRegistrator.startOrb();
	}

	@EventListener
	public void onClose(ContextClosedEvent event) {
		// Sinaliza parada para o registro
		openBusRegistrator.stop();
	}

}
