package br.pucrio.tecgraf.springboot.openbus.autoconfigure;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OpenBusApplication {

    String value();

    String version() default "";

    byte major() default -1;

    byte minor() default -1;

    byte patch() default -1;

}
