package part3;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface PixelArt extends Remote {

    void receiveGrid(Map<Pair<Integer, Integer>, Integer> grid) throws RemoteException;

    void configuration() throws RemoteException;

}
