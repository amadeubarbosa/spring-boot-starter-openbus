package br.pucrio.tecgraf.springboot.openbus;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertEquals;

/*@OpenBusApplication("springboot-sample")
@SpringBootTest
@ContextConfiguration(classes = MacumbaOpenbusConfiguration.class)*/
public class ServicesTests {

    @Autowired
    Service1 service;

    @Test
    public void runService1() {
        assertEquals("Hello from one!", service.greetsOne());
    }

}
