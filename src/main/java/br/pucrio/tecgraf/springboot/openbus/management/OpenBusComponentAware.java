package br.pucrio.tecgraf.springboot.openbus.management;

import scs.core.ComponentContext;

public interface OpenBusComponentAware {

    void setComponentContext(ComponentContext componentContext);

}
