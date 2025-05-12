package interfacesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioRMI extends Remote {
    public void pausarReanudar() throws RemoteException;
}
