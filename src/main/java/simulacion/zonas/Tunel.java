package simulacion.zonas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.seres.Humano;

import java.util.HashMap;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class Tunel {
    private static final Logger logger = LogManager.getLogger(Tunel.class);
    private final int id;

    private String idHumanoTunel;

    private final ObservableMap<String, Humano> humanosSeguros = FXCollections.observableMap(new ConcurrentHashMap<>(10000));
    private final ObservableMap<String, Humano> humanosEsperando = FXCollections.observableMap(new ConcurrentHashMap<>(3));
    private final ObservableMap<String, Humano> humanosRiesgo = FXCollections.observableMap(new ConcurrentHashMap<>(10000));

    private PriorityBlockingQueue<Humano> colaTunel = new PriorityBlockingQueue<>();

    private CyclicBarrier barrera = new CyclicBarrier(3);
    private Semaphore semaforo = new Semaphore(3);

    public Tunel (int id) {
        this.id = id;
    }

    private void pasar () {
        try {
            String idHumano = colaTunel.take().getIdHumano();
            try {
                logger.info("{} está pasando por el tunel {}", idHumano, id);
                this.idHumanoTunel = idHumano;
                sleep(1000);
                logger.info("{} ha salido del tunel {}", idHumano, id);
            } catch (InterruptedException e) {
                logger.warn("El humano {} ha sido interrumpido mientras pasaba el tunel {}: {}", idHumano, id, e.getMessage());
            }
        } catch (InterruptedException e) {
            logger.error("Error al coger un id de humano: {}", e.getMessage());
        }
    }

    public void esperarSeguro (Humano humano) {
        try {
            String idHumano = humano.getIdHumano();
            synchronized (humanosSeguros) {
                humanosSeguros.put(idHumano, humano);
            }
            logger.info("{} está esperando a encontrar grupo para pasar por el túnel {} para entrar a la zona de riesgo", idHumano, id);

            semaforo.acquire(); // No deja pasar a 3 hilos a la barrera (de forma que la barrera bajaría) hasta que todos los hilos del grupo anterior han pasado
            barrera.await();
            logger.info("{} ha encontrado grupo para pasar por el túnel {} a la zona de riesgo", idHumano, id);

            synchronized (humanosSeguros) {
                humanosSeguros.remove(idHumano);
            }
            synchronized (humanosEsperando) {
                humanosEsperando.put(idHumano, humano);
            }

            humano.setPrioridadTunel(1);
            colaTunel.add(humano);

            pasar();

            semaforo.release();
            synchronized (humanosEsperando) {
                humanosEsperando.remove(idHumano);
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            logger.warn("El humano {} ha sido interrumpido mientras esperaba a pasar el tunel {}: {}", humano.getIdHumano(), id, e.getMessage());
        }
    }

    public void esperarRiesgo (Humano humano) {
        humano.setPrioridadTunel(0);
        colaTunel.add(humano);

        String idHumano = humano.getIdHumano();
        logger.info("{} está esperando a a pasar por el túnel {} para volver a la zona segura", idHumano, id);
        synchronized (humanosRiesgo) {
            humanosRiesgo.put(idHumano, humano);
        }

        pasar();
        synchronized (humanosRiesgo) {
            humanosRiesgo.remove(idHumano);
        }
    }

    public int getId() {
        return id;
    }

    public String getIdHumanoTunel() {
        return idHumanoTunel;
    }

    public ObservableMap<String, Humano> getHumanosSeguros() {
        return humanosSeguros;
    }

    public ObservableMap<String, Humano> getHumanosEsperando() {
        return humanosEsperando;
    }

    public ObservableMap<String, Humano> getHumanosRiesgo() {
        return humanosRiesgo;
    }
}
