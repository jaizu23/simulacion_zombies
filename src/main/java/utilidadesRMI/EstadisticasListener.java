package utilidadesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EstadisticasListener extends Remote {
    void actualizarEstadisticas(Estadisticas estadisticas) throws RemoteException;
}
