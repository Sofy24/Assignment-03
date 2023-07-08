package part3;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface PixelArt extends Remote {

    void receiveGrid(Map<Pair<Integer, Integer>, Integer> grid) throws RemoteException;

    void configuration() throws RemoteException;

    BrushManager.Brush getLocalBrush() throws RemoteException;

    void receiveBrushes(Set<BrushManager.Brush> brushes) throws RemoteException;

    UUID getClientId() throws RemoteException;
}
