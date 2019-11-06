package br.pucrio.tecgraf.springboot.openbus;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
public @interface OpenBusService {

    String name();

    String interfaceName();

}
