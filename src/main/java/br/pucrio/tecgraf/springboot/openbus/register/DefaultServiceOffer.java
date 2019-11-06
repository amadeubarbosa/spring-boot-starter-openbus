package br.pucrio.tecgraf.springboot.openbus.register;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scs.core.IComponent;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

public class DefaultServiceOffer implements ServiceOffer {

	private IComponent component;
	private Collection<ServiceProperty> properties;

	public DefaultServiceOffer(IComponent component, ServiceProperty... properties) {
		this.component = component;
		this.properties = new LinkedList();
		ServiceProperty[] arr$ = properties;
		int len$ = properties.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			ServiceProperty property = arr$[i$];
			this.properties.add(property);
		}

	}

	public IComponent getComponent() {
		return this.component;
	}

	public Collection<ServiceProperty> getServiceProperties() {
		return Collections.unmodifiableCollection(this.properties);
	}

}