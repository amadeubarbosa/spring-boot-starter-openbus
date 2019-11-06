package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ComponentScan("br.pucrio.tecgraf.springboot")
public @interface OpenBusApplication {

    String value();

    byte major() default 0;

    byte minor() default 0;

    byte patch() default 0;

}
