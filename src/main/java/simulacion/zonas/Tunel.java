package simulacion.zonas;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class Tunel {
    private static final Logger logger = LogManager.getLogger(Tunel.class);
    private final int id;

    private StringProperty idHumanoTunel = new SimpleStringProperty();

    private final LabelUpdateConcurrentHashMap<Humano> humanosSeguros = new LabelUpdateConcurrentHashMap<>(10000);
    private final LabelUpdateConcurrentHashMap<Humano> humanosEsperando = new LabelUpdateConcurrentHashMap<>(3);
    private final LabelUpdateConcurrentHashMap<Humano> humanosRiesgo = new LabelUpdateConcurrentHashMap<>(10000);

    private PriorityBlockingQueue<Humano> colaTunel = new PriorityBlockingQueue<>();

    private CyclicBarrier barrera = new CyclicBarrier(3);
    private Semaphore semaforo = new Semaphore(3);

    public Tunel (int id) {
        this.id = id;
    }

    private synchronized void pasar () {
        try {
            String idHumano = colaTunel.take().getIdHumano();
            try {
                boolean ladoSeguro = humanosEsperando.remove(idHumano) != null; // Si el humano a borrar está en el lado seguro liberaremos un permiso del semáforo
                humanosRiesgo.remove(idHumano);
                logger.info("{} está pasando por el tunel {}", idHumano, id);
                Platform.runLater(() -> idHumanoTunel.set(idHumano));
                sleep(1000);
                Platform.runLater(() -> idHumanoTunel.set(""));
                logger.info("{} ha salido del tunel {}", idHumano, id);

                if (ladoSeguro) {
                    semaforo.release();
                }
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
            humanosSeguros.put(idHumano, humano);
            logger.info("{} está esperando a encontrar grupo para pasar por el túnel {} para entrar a la zona de riesgo", idHumano, id);

            semaforo.acquire(); // No deja pasar a 3 hilos a la barrera (de forma que la barrera bajaría) hasta que todos los hilos del grupo anterior han pasado
            barrera.await();
            logger.info("{} ha encontrado grupo para pasar por el túnel {} a la zona de riesgo", idHumano, id);

            humanosSeguros.remove(idHumano);

            humanosEsperando.put(idHumano, humano);

            humano.setPrioridadTunel(1);
            colaTunel.add(humano);

            pasar();
        } catch (InterruptedException | BrokenBarrierException e) {
            logger.warn("El humano {} ha sido interrumpido mientras esperaba a pasar el tunel {}: {}", humano.getIdHumano(), id, e.getMessage());
        }
    }

    public void esperarRiesgo (Humano humano) {
        humano.setPrioridadTunel(0);
        colaTunel.add(humano);

        String idHumano = humano.getIdHumano();
        logger.info("{} está esperando a a pasar por el túnel {} para volver a la zona segura", idHumano, id);
        humanosRiesgo.put(idHumano, humano);

        pasar();
    }

    public int getId() {
        return id;
    }

    public StringProperty getIdHumanoTunel() {
        return idHumanoTunel;
    }

    public LabelUpdateConcurrentHashMap<Humano> getHumanosSeguros() {
        return humanosSeguros;
    }

    public LabelUpdateConcurrentHashMap<Humano> getHumanosEsperando() {
        return humanosEsperando;
    }

    public LabelUpdateConcurrentHashMap<Humano> getHumanosRiesgo() {
        return humanosRiesgo;
    }
}
