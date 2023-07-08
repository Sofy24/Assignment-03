package part3;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GridService extends Remote {

    void register (PixelArt identifier) throws RemoteException;
    //void exit (PixelArt identifier);

    void setPixel(Integer x, Integer y, Integer color) throws RemoteException;

    //void setMouse(Integer x, Integer y);
}
