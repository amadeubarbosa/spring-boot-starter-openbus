package br.pucrio.tecgraf.springboot.openbus;

@OpenBusService(name = "Service2", interfaceName = "IDL:Service1:2.0")
public class Service2 {

    public String greetsTwo() {
        return "Hello from two!";
    }

}
