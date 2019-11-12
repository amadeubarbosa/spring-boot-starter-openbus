package br.pucrio.tecgraf.springboot.openbus.management;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import scs.core.exception.SCSException;

@Component
public class OpenBusApplicationListener {

	private Logger logger = LoggerFactory.getLogger(OpenBusApplicationListener.class);

	private OpenBusApplicationInstance openBusApplicationInstance;

	public OpenBusApplicationListener(BeanFactory beanFactory) {
		// OpenBus instance
		openBusApplicationInstance = beanFactory.getBean(OpenBusApplicationInstance.class);
	}

	@EventListener
	public void onStart(ContextRefreshedEvent event) throws SCSException {
		// Inicia a instância do componente
		openBusApplicationInstance.start();
	}

	@EventListener
	public void onClose(ContextClosedEvent event) {
		// Sinaliza parada para o registro
		openBusApplicationInstance.stop();
	}

}
