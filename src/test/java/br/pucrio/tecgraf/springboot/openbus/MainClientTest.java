package br.pucrio.tecgraf.springboot.openbus;

import org.junit.Test;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;

import tecgraf.openbus.exception.CryptographyException;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;

/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class MainClientTest {

    /*@Autowired
    OpenBusConfiguration conf;*/

    @Test
    public void testConnection() throws AdapterInactive, InterruptedException, CryptographyException, InvalidKeySpecException, IOException {
        OpenBusServiceLocator locator = new OpenBusServiceLocator("macumba", 21000,
                "springboot-openbus-teste", "/home/valtoni/.ssh/private-key.pkcs8");
        ServiceOperations1 serviceOperations1 = locator.locate(ServiceOperations1.class,"springboot-openbus-teste", "Service1");
        System.out.println(serviceOperations1.greetsOne());
        ServiceOperations2 serviceOperations2 = locator.locate(ServiceOperations2.class,"springboot-openbus-teste", "Service2");
        System.out.println(serviceOperations2.greetsTwo());
        ServiceOperations3 serviceOperations3 = locator.locate(ServiceOperations3.class,"springboot-openbus-teste", "Service3");
        System.out.println(serviceOperations3.greetsThree());
    }

}
