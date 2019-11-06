package br.pucrio.tecgraf.springboot.openbus;

@OpenBusService(name = "Service1", interfaceName = "IDL:Service1:1.0")
public class Service1 {

    public String greetsOne() {
        return "Hello from one!";
    }

}
