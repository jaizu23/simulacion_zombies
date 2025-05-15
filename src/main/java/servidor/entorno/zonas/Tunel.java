package servidor.entorno.zonas;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.DataUpdateConcurrentHashMap;
import servidor.exceptions.killedHumanException;
import servidor.seres.Humano;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Tunel {
    private static final Logger logger = LogManager.getLogger(Tunel.class);
    private final int zona;

    private final StringProperty idHumanoTunel = new SimpleStringProperty();

    private final DataUpdateConcurrentHashMap<Humano> humanosSeguros;
    private final DataUpdateConcurrentHashMap<Humano> humanosEsperando;
    private final DataUpdateConcurrentHashMap<Humano> humanosRiesgo;

    private final Lock lockTunel = new ReentrantLock();
    private final Lock lockCondition = new ReentrantLock();
    private final Condition hayHumanosRiesgo = lockCondition.newCondition();

    private final CyclicBarrier grupoCompletado = new CyclicBarrier(3);
    private final Semaphore grupoTunel = new Semaphore(3);

    public Tunel (int zona, Mapa mapa) {
        this.zona = zona;
        humanosSeguros = new DataUpdateConcurrentHashMap<>(mapa, 10000);
        humanosEsperando = new DataUpdateConcurrentHashMap<>(mapa, 3);
        humanosRiesgo = new DataUpdateConcurrentHashMap<>(mapa, 10000);
    }

    public void esperarSeguro (Humano humano){
        try {
            String idHumano = humano.getIdHumano();
            humanosSeguros.put(idHumano, humano);
            logger.info("{} está esperando a encontrar grupo para pasar por el túnel {} para entrar a la zona de riesgo", idHumano, zona);

            humano.comprobarPausado();

            grupoTunel.acquire(); // No deja pasar a 3 hilos a la barrera (de forma que la barrera bajaría) hasta que todos los hilos del grupo anterior han pasado
            grupoCompletado.await();
            logger.info("{} ha encontrado grupo para pasar por el túnel {} a la zona de riesgo", idHumano, zona);

            humanosSeguros.remove(idHumano);
            humanosEsperando.put(idHumano, humano);

            humano.comprobarPausado();

            lockCondition.lock();
            try {
                boolean haPasado = false;
                while (!haPasado) {
                    while (!humanosRiesgo.isEmpty()) {
                        hayHumanosRiesgo.await();
                    }
                    if (lockTunel.tryLock()) {
                        try {
                            pasarTunel(humano, true);
                        } finally {
                            lockTunel.unlock();
                            hayHumanosRiesgo.signalAll();
                            haPasado = true;
                        }
                    }
                }
            } finally {
                lockCondition.unlock();
            }

        } catch (InterruptedException | BrokenBarrierException e) {
            logger.error("Ha ocurrido un error mientras {} esperaba a pasar el tunel {} desde la zona segura", humano.getIdHumano(), zona);
        }
    }

    public void esperarRiesgo (Humano humano) throws killedHumanException {
        String idHumano = humano.getIdHumano();
        logger.info("{} está esperando a a pasar por el túnel {} para volver a la zona segura", idHumano, zona);

        humanosRiesgo.put(idHumano, humano);

        humano.comprobarPausado();

        pasarTunel(humano, false);
        lockCondition.lock();
        try {
            hayHumanosRiesgo.signalAll();
        } finally {
            lockCondition.unlock();
        }
    }

    private synchronized void pasarTunel(Humano humano, boolean ladoSeguro) {
        String idHumano = humano.getIdHumano();
        try {
            humanosEsperando.remove(idHumano);
            humanosRiesgo.remove(idHumano);
            logger.info("{} está pasando por el tunel {}", idHumano, zona);

            Platform.runLater(() -> idHumanoTunel.set(idHumano));
            Thread.sleep(1000);
            humano.comprobarPausado();
            Platform.runLater(() -> idHumanoTunel.set(""));
            logger.info("{} ha salido del tunel {}", idHumano, zona);
        } catch (InterruptedException e) {
            logger.error("{} ha sido interrumpido mientras pasaba por el tunel {}", idHumano, zona);
        } finally {
            if (ladoSeguro) {
                grupoTunel.release();
            }
        }
    }

    public StringProperty getIdHumanoTunel() {
        return idHumanoTunel;
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosSeguros() {
        return humanosSeguros;
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosEsperando() {
        return humanosEsperando;
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosRiesgo() {
        return humanosRiesgo;
    }
}
