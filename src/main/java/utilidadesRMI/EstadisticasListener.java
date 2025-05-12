package utilidadesRMI;

import java.rmi.Remote;

public interface EstadisticasListener extends Remote {
    public void actualizarEstadisticas (Estadisticas estadisticas);
}
