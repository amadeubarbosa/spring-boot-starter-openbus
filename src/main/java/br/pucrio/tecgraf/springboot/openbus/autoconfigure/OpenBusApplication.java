package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OpenBusApplication {

    String value();

    String version();

    byte major() default 0;

    byte minor() default 0;

    byte patch() default 0;

}
