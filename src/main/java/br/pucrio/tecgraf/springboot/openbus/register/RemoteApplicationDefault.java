package br.pucrio.tecgraf.springboot.openbus.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scs.core.ComponentContext;
import scs.core.exception.SCSException;
import tecgraf.openbus.core.v2_0.services.offer_registry.ServiceProperty;

import java.util.*;

public class RemoteApplicationDefault implements RemoteApplication {

    private Logger log = LoggerFactory.getLogger(RemoteApplicationDefault.class);

    private String name;
    private ComponentContext componentContext;
    private Collection<ServiceProperty> properties;

    public RemoteApplicationDefault(String name, ComponentContext componentContext, ServiceProperty... properties) {
        this.name = name;
        this.componentContext = componentContext;
        this.properties = Arrays.asList(properties);
    }

    public ComponentContext getComponentContext() {
        return this.componentContext;
    }

    public ServiceProperty[] getApplicationProperties() {
        return this.properties.toArray(new ServiceProperty[]{});
    }

    public String getName() {
        return name;
    }

    private Set<RemoteService> availableRemoteServices = new HashSet<>();
    private Set<RemoteService> registeredRemoteServices = new HashSet<>();

    @Override
    public void addService(RemoteService remoteService) throws SCSException {
        componentContext.addFacet(remoteService.getName(), remoteService.getId(), remoteService.getServant());
        availableRemoteServices.add(remoteService);
        log.info("Oferta adicionada: {}", remoteService);
    }

    @Override
    public void removeService(RemoteService remoteService) {
        availableRemoteServices.remove(remoteService);
        registeredRemoteServices.remove(remoteService);
        log.info("Oferta removida: {} ", remoteService);
    }

    @Override
    public void registerService(RemoteService remoteService) {
        availableRemoteServices.remove(remoteService);
        registeredRemoteServices.add(remoteService);
        log.info("Oferta registrada: {} ", remoteService);
    }

    @Override
    public Set<RemoteService> availableOffers() {
        return new HashSet<>(availableRemoteServices);
    }

    @Override
    public Set<RemoteService> registeredOffers() {
        return new HashSet<>(registeredRemoteServices);
    }

    @Override
    public void unregisterServices() {
        Set<RemoteService> remoteServices = registeredOffers();
        log.debug("Retirando da lista de serviços registrados {} serviços", remoteServices.size());
        for (RemoteService remoteService: remoteServices) {
            removeService(remoteService);
        }
        log.info("Todos os serviços foram removidos");
    }


    @Override
    public String toString() {
        return "RemoteApplicationDefault{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RemoteApplicationDefault that = (RemoteApplicationDefault) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
