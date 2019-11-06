package br.pucrio.tecgraf.springboot.openbus.register;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import scs.core.ComponentContext;
import scs.core.ComponentId;
import scs.core.exception.SCSException;

import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.util.*;

@Component
public class ServiceOfferRegister {

	private Map<String, String> serviceProperties;
	private ORB orb;
	private POA poa;
	private byte major;
	private byte minor;
	private byte patch;
	private List<Servant> services = new ArrayList<>();

	public ServiceOfferRegister(ORB orb, POA poa, @Qualifier("major") byte major,
								@Qualifier("minor") byte minor, @Qualifier("patch") byte patch,
								@Qualifier("servicesProperties") Map<String, String> serviceProperties) {
		this.orb = orb;
		this.poa = poa;
		this.major = major;
		this.minor = minor;
		this.patch = patch;
		this.serviceProperties = serviceProperties;
	}

	public Collection<ServiceOffer> create() throws SCSException {
		String platformSpecification = String.format("java-%s", System.getProperty("java.specification.version"));
		// TODO Capturar o nome de @OpenBusApplication
		ComponentContext ctxAsync = new ComponentContext(orb, poa, new ComponentId(
			"test-openbus-change-name",
			major, minor, patch, platformSpecification));

		/*ctxAsync.addFacet("TransferMonitor", TransferMonitorHelper.id(), new TransferMonitorPOATie(transferMonitor));
		ctxAsync.addFacet("TransferAgent", TransferAgentHelper.id(), new TransferAgentPOATie(transferAgent));*/
		System.out.println("REGISTER THE SERVICES");

		ServiceProperty[] propertiesAsync = new ServiceProperty[services.size()];
		int i = 0;
		for (String key: serviceProperties.keySet()) {
			propertiesAsync[i++] = new ServiceProperty(key, serviceProperties.get(key));
		}

		ServiceOffer resAsync = new DefaultServiceOffer(
			ctxAsync.getIComponent(),
			propertiesAsync);

		return Collections.singletonList(resAsync);
	}

}
