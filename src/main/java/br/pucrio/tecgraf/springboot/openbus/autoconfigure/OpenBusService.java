package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import org.omg.PortableServer.Servant;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OpenBusService {

    String name();

    String id();

    Class<? extends Servant> servant();

}
