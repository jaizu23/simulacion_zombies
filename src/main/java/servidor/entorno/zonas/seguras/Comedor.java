package servidor.entorno.zonas.seguras;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.DataUpdateConcurrentHashMap;
import servidor.seres.Humano;

import java.util.concurrent.Semaphore;

public class Comedor extends Refugio{
    private static final Logger logger = LogManager.getLogger(Comedor.class);

    private final DataUpdateConcurrentHashMap<Humano> humanosComedor;

    private final IntegerProperty contadorComida = new SimpleIntegerProperty(0);
    private final Semaphore semaforoComida = new Semaphore(0);

    public Comedor (Mapa mapa) {
         humanosComedor= new DataUpdateConcurrentHashMap<>(mapa, 10000);
    }

    public void comer (Humano humano) {
        String id = humano.getIdHumano();
        entrarZona(humanosComedor, humano);
        try {
            semaforoComida.acquire();
        } catch (InterruptedException e) {
            logger.error("Error mientras {} comía", id);
        }
        synchronized (contadorComida) {
            Platform.runLater(() -> contadorComida.set(contadorComida.get() - 1));
        }
        pasarTiempo(humano, 3000, 5000);
        humano.comprobarPausado();
        salirZona(humanosComedor, id);
    }

    public void depositarComida (Humano humano) {
        int cantidadComida = humano.getComida();
        synchronized (contadorComida) {
            Platform.runLater(() -> contadorComida.set(contadorComida.get() + cantidadComida));
        }
        semaforoComida.release(cantidadComida);
        humano.añadirComida(-cantidadComida);
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosComedor() {
        return humanosComedor;
    }

    public IntegerProperty getContadorComida() {
        return contadorComida;
    }
}
