package part3;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
                
    public static void main(String args[]) {
        
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            GridService gridService = new GridServiceImpl();
            GridService gridStub = (GridService) UnicastRemoteObject.exportObject(gridService, 0);
            BrushService brushService = new BrushServiceImpl();
            BrushService brushStub = (BrushService) UnicastRemoteObject.exportObject(brushService, 0);
            /*RegisterService registration = new RegisterServiceImpl();
            RegisterService registrationStub = (RegisterService) UnicastRemoteObject.exportObject(registration, 0);*/

            // Bind the remote object's stub in the registry
            //Registry registry = LocateRegistry.getRegistry();
            registry.rebind("grid", gridStub);
            registry.rebind("brush", brushStub);
            //registry.rebind("brush", brushStub);
            //registry.rebind("registration", registrationStub);

            
            System.out.println("Objects registered.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}