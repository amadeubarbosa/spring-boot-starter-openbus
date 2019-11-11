package br.pucrio.tecgraf.springboot.openbus.management;

public interface Produtor<T> {

    T produces() throws Exception;

}
