package part3;


import java.rmi.Remote;

public interface RegisterService extends Remote {

    void register (String identifier);
    void exit (String identifier);

}
