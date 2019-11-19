package br.pucrio.tecgraf.springboot.openbus;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
public class SimpleProfiler {

    private Logger log = LoggerFactory.getLogger(SimpleProfiler.class);

    /*@Around("execution(* org.omg.PortableServer.Servant+.*(..)) && args(params))")*/
    /*@Around("execution(* org.omg.PortableServer.Servant+.*(..)) && !execution(* org.omg.PortableServer.Servant+._*(..))" +
            " && args(params))")*/
    //@Around("execution(* br.pucrio.tecgraf.springboot.openbus.properties.OpenBusConfiguration.*(..)) && args(params)")
    //public Object profile(ProceedingJoinPoint call, Object[] params) throws Throwable {
    /*@Around("execution(* br.pucrio.tecgraf.springboot.openbus.Service3.*(..))")
    public Object profile(ProceedingJoinPoint call) throws Throwable {
        StopWatch clock = new StopWatch("Profiling");
        try {
            clock.start();
            return call.proceed();
        } finally {
            clock.stop();
            log.info(clock.prettyPrint());
        }
    }*/

    /*@Pointcut("")
    private void allMethods3() { }*/

    //@After("execution(* br.pucrio.tecgraf.springboot.openbus.Service3.*())")
    @Pointcut("within(br.pucrio.tecgraf.springboot.openbus..*)")
    public void applicationPackagePointCut() {}

    @Around("applicationPackagePointCut()")
    public void logAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("****************** " + proceedingJoinPoint.getSignature().getName());
    }

}