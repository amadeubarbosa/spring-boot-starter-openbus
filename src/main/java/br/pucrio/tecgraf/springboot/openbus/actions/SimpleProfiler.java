package br.pucrio.tecgraf.springboot.openbus.actions;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class SimpleProfiler {

    private Logger log = LoggerFactory.getLogger(SimpleProfiler.class);

    /*@Around("execution(* org.omg.PortableServer.Servant+.*(..)) && args(params))")*/
    /*@Around("execution(* org.omg.PortableServer.Servant+.*(..)) && !execution(* org.omg.PortableServer.Servant+._*(..))" +
            " && args(params))")*/
    //@Around("execution(* br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration.*(..)) && args(params)")
    //public Object profile(ProceedingJoinPoint call, Object[] params) throws Throwable {
    @Around("execution(* br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration.getName())")
    public Object profile(ProceedingJoinPoint call) throws Throwable {
        StopWatch clock = new StopWatch("Profiling");
        try {
            clock.start();
            return call.proceed();
        } finally {
            clock.stop();
            log.info(clock.prettyPrint());
        }
    }

}
