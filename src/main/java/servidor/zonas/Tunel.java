package servidor.zonas;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import servidor.exceptions.killedHumanException;
import servidor.seres.Humano;

import java.util.concurrent.*;

public class Tunel extends Thread{
    private static final Logger logger = LogManager.getLogger(Tunel.class);
    private final int zona;

    private final StringProperty idHumanoTunel = new SimpleStringProperty();

    private final LabelUpdateConcurrentHashMap<Humano> humanosSeguros = new LabelUpdateConcurrentHashMap<>(10000);
    private final LabelUpdateConcurrentHashMap<Humano> humanosEsperando = new LabelUpdateConcurrentHashMap<>(3);
    private final LabelUpdateConcurrentHashMap<Humano> humanosRiesgo = new LabelUpdateConcurrentHashMap<>(10000);

    private final PriorityBlockingQueue<Humano> colaTunel = new PriorityBlockingQueue<>();

    private final CyclicBarrier grupoCompletado = new CyclicBarrier(3);
    private final Semaphore grupoTunel = new Semaphore(3);

    private final Mapa mapa;

    public Tunel (int zona, Mapa mapa) {
        this.zona = zona;
        this.mapa = mapa;
    }

    public void esperarSeguro (Humano humano){
        try {
            String idHumano = humano.getIdHumano();
            humanosSeguros.put(idHumano, humano);
            logger.info("{} está esperando a encontrar grupo para pasar por el túnel {} para entrar a la zona de riesgo", idHumano, zona);

            grupoTunel.acquire(); // No deja pasar a 3 hilos a la barrera (de forma que la barrera bajaría) hasta que todos los hilos del grupo anterior han pasado
            grupoCompletado.await();
            logger.info("{} ha encontrado grupo para pasar por el túnel {} a la zona de riesgo", idHumano, zona);

            humanosSeguros.remove(idHumano);
            humanosEsperando.put(idHumano, humano);

            humano.setPrioridadTunel(1);
            colaTunel.put(humano);
            try {
                synchronized (humano) {
                    humano.wait(); // Espera hasta ser notificado por el túnel por haber conseguido pasar
                }
            } catch (InterruptedException e) {
                if (!humano.getHaPasadoTunel().get()) {
                    logger.error("{} ha sido interrumpido mientras pasaba el túnel {} desde el lado seguro", idHumano, zona);
                }
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            logger.error("{} ha sido interrumpido mientras esperaba a pasar el tunel {} esperando en la zona segura {}", humano.getIdHumano(), zona, e.getMessage());
        }
    }

    public void esperarRiesgo (Humano humano) throws killedHumanException {
        String idHumano = humano.getIdHumano();
        logger.info("{} está esperando a a pasar por el túnel {} para volver a la zona segura", idHumano, zona);

        humanosRiesgo.put(idHumano, humano);
        humano.setPrioridadTunel(0);
        colaTunel.put(humano);
        try {
            synchronized (humano) {
                humano.wait(); // Espera hasta ser notificado por el túnel por haber conseguido pasar
            }
        } catch (InterruptedException e) {
            if (humano.getAsesinado().get()) {
                throw new killedHumanException();
            } else if (!humano.getMarcado().get() && !humano.getHaPasadoTunel().get()) {
                logger.error("{} ha sido interrumpido inesperadamente al pasar el túnel {}", humano.getIdHumano(), zona);
            }
        }
    }

    private void pasarTunel() throws InterruptedException {
        Humano humano = colaTunel.take();
        String idHumano = humano.getIdHumano();
        mapa.getZonasRiesgo()[zona].getPosiblesVictimas().remove(idHumano);
        boolean ladoSeguro = humanosEsperando.containsKey(idHumano); // Si el humano que pasa está esperando en el lado seguro liberaremos un permiso del semáforo pase lo que pase al final
        try {
            humanosEsperando.remove(idHumano);
            humanosRiesgo.remove(idHumano);
            logger.info("{} está pasando por el tunel {}", idHumano, zona);

            Platform.runLater(() -> idHumanoTunel.set(idHumano));
            sleep(1000);
            Platform.runLater(() -> idHumanoTunel.set(""));

            logger.info("{} ha salido del tunel {}", idHumano, zona);
            humano.getHaPasadoTunel().set(true);
            humano.interrupt();
        } catch (InterruptedException e) {
            logger.error("{} ha sido interrumpido mientras pasaba por el tunel {}", idHumano, zona);
        } finally {
            if (ladoSeguro) {
                grupoTunel.release();
            }
        }
    }

    public void run () {
        while (!mapa.isPausado()) {
            try {
                pasarTunel();
            } catch (InterruptedException e) {
                logger.error("Ha ocurrido un error en la ejecución del túnel {}", zona);
            }
        }
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
