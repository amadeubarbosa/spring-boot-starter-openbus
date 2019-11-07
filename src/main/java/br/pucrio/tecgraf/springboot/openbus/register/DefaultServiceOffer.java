package br.pucrio.tecgraf.springboot.openbus.register;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scs.core.IComponent;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

public class DefaultServiceOffer implements ServiceOffer {

	private String name;
	private IComponent component;
	private Collection<ServiceProperty> properties;

	public DefaultServiceOffer(String name, IComponent component, ServiceProperty... properties) {
		this.name = name;
		this.component = component;
		this.properties = Arrays.asList(properties);
	}

	public String getName() {
		return name;
	}

	public IComponent getComponent() {
		return this.component;
	}

	public Collection<ServiceProperty> getServiceProperties() {
		return Collections.unmodifiableCollection(this.properties);
	}

	@Override
	public String toString() {
		return "DefaultServiceOffer{" +
				"name='" + name + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefaultServiceOffer that = (DefaultServiceOffer) o;
		return name.equals(that.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}