package servidor.simulacion_zombies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import utilidadesRMI.EstadisticasListener;
import utilidadesRMI.ServicioRMI;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class servidorRMI extends UnicastRemoteObject implements ServicioRMI {
    private static final Logger logger = LogManager.getLogger(servidorRMI.class);

    private Mapa mapa;

    private EstadisticasListener listener;

    public servidorRMI() throws RemoteException {
        super();
    }

    public void inicializarServidor () {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("ServicioRMI", this);
        } catch (Exception e) {
            logger.error("Ha ocurrido un error al inicializar el servidor del servicio RMI");
        }
    }

    @Override
    public void registrarListener(EstadisticasListener listener) throws RemoteException {
        this.listener = listener;
    }

    @Override
    public void pausarReanudar() throws RemoteException {
        if (mapa.isPausado()) {
            logger.info("Reanudando partida");
            mapa.setPausado(false);
            mapa.getLockPausado().lock();
            try {
                mapa.getConditionPausado().signalAll();
            } finally {
                mapa.getLockPausado().unlock();
            }
        } else {
            logger.info("Pausando partida");
            mapa.setPausado(true);
        }
    }

    public void actualizarEstadisticas () {
        if (listener != null) {
            mapa.actualizarEstadisticas();
            try {
                listener.actualizarEstadisticas(mapa.getEstadisticas());
            } catch (RemoteException e) {
                logger.error("El servidor fue interrumpido mientras intentaba actualizar las estadisticas en el cliente");
            }
        }
    }

    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
    }
}
