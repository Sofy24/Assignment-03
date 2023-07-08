package part3.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
        
public class Test02_Server2  {
                
    public static void main(String args[]) {
        
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            HelloService helloObj = new HelloServiceImpl();
            HelloService helloObjStub = (HelloService) UnicastRemoteObject.exportObject(helloObj, 0);

            // Bind the remote object's stub in the registry
            //Registry registry = LocateRegistry.getRegistry();
            registry.rebind("helloObj2", helloObjStub);
            
            System.out.println("Objects registered.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}