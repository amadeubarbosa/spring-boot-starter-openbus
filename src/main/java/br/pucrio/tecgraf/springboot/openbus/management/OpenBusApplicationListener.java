package br.pucrio.tecgraf.springboot.openbus.management;

import br.pucrio.tecgraf.springboot.openbus.orb.ORBManager;
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
	private ORBManager orbManager;

	public OpenBusApplicationListener(BeanFactory beanFactory) {
		// Instantiate registrator bean
		openBusRegistrator = beanFactory.getBean(OpenBusRegistrator.class);
		// Instantiate ORBManager bean
		orbManager = beanFactory.getBean(ORBManager.class);
	}

	@EventListener
	public void onStart(ContextStartedEvent event) {
		// Ativa o poa raiz
		orbManager.activatePOA();
		// Registra os serviços
		openBusRegistrator.registerServices();
		// Inicializa o ORB
		orbManager.startOrb();
	}

	@EventListener
	public void onClose(ContextClosedEvent event) {
		// Sinaliza parada para o registro
		openBusRegistrator.stop();
	}

}
