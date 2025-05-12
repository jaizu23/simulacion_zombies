package servidor.simulacion_zombies;

import interfacesRMI.EstadisticasListener;
import interfacesRMI.ServicioRMI;
import servidor.entorno.Estadisticas;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class servidorRMI extends UnicastRemoteObject implements ServicioRMI {
    Estadisticas estadisticas;
    public servidorRMI() throws RemoteException {

    }
    @Override
    public void pausarReanudar() throws RemoteException {

    }

    private void notificarListener () {
        Estadisticas estadisticas = new Estadisticas(this.estadisticas); // Copiamos por seguridad
        
    }
}
