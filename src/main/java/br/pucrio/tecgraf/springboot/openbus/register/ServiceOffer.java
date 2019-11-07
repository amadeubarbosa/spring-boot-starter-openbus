package br.pucrio.tecgraf.springboot.openbus.register;

import scs.core.IComponent;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.util.Collection;

public interface ServiceOffer {

	String getName();

	IComponent getComponent();

	Collection<ServiceProperty> getServiceProperties();

}