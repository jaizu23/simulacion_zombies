package utilidadesRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface EstadisticasListener extends Remote {
    public void actualizarEstadisticas (Estadisticas estadisticas) throws RemoteException;
}
