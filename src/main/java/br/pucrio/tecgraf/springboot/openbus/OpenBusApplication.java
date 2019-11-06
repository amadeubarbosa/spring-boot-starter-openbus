package br.pucrio.tecgraf.springboot.openbus;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan("br.pucrio.tecgraf.springboot.openbus")
public @interface OpenBusApplication {

    String value();

}
