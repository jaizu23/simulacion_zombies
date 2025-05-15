package utilidadesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicioRMI extends Remote {
    void registrarListener(EstadisticasListener listener) throws RemoteException;
    void pausarReanudar() throws RemoteException;
}
