package cliente;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import utilidadesRMI.EstadisticasListener;
import utilidadesRMI.ServicioRMI;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class clienteRMI {
    private static final Logger logger = LogManager.getLogger(clienteRMI.class);

    private ServicioRMI servicio;

    public void inicializarCliente (clienteViewController listener) {
        try {
            servicio = (ServicioRMI) Naming.lookup("rmi://localhost/ServicioRMI");

            EstadisticasListener stub = (EstadisticasListener) UnicastRemoteObject.exportObject(listener, 0);

            servicio.registrarListener(stub);
        } catch (Exception e) {
            logger.error("Ha ocurrido un error al inicializar el cliente del servicio RMI");
        }
    }

    public void pausarReanudar() {
        try {
            servicio.pausarReanudar();
        } catch (RemoteException e) {
            logger.error("Ha ocurrido un error al pausar/reanudar el juego remotamente");
        }
    }
}
