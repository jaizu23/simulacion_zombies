package servidor.entorno;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.seres.*;
import servidor.entorno.zonas.seguras.*;
import servidor.entorno.zonas.*;
import servidor.simulacion_zombies.servidorRMI;
import utilidadesRMI.Estadisticas;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mapa extends Thread{
    private static final Logger logger = LogManager.getLogger(Mapa.class);

    private final Random r = new Random();

    private final servidorRMI servidor;

    private final Estadisticas estadisticas;

    private final Comedor comedor = new Comedor(this);
    private final ZonaComun zonaComun = new ZonaComun(this);
    private final Descanso descanso = new Descanso(this);

    private final ZonaRiesgo[] zonasRiesgo = new ZonaRiesgo[4];
    private final Tunel[] tuneles = new Tunel[4];

    private final Lock lockPausado = new ReentrantLock();
    private final Condition conditionPausado = lockPausado.newCondition();
    private final AtomicBoolean pausado = new AtomicBoolean(false);

    public Mapa (servidorRMI servidor) {
        this.servidor = servidor;
        this.estadisticas = new Estadisticas();

        for (int i = 0; i < 4; i++) {
            zonasRiesgo[i] = new ZonaRiesgo(this, i);
            tuneles[i] = new Tunel(i, this);
        }
    }

    public void run () {
        Zombie zombie = new Zombie("Z0000", this);
        zombie.start();
        for (int i = 1; i < 10000; i++) {
            comprobarPausado();
            Humano humano = new Humano("H" + String.format("%04d", i),this);
            humano.start();
            logger.info("{} ha nacido.", humano.getIdHumano());
            try {
                sleep(r.nextInt(500, 2000));
            } catch (InterruptedException e) {
                logger.error("Se ha interrumpido un hilo mientras esperaba al crear los humanos{}", String.valueOf(e));
            }
        }
    }

    private void comprobarPausado () {
        while(isPausado()) {
            getLockPausado().lock();
            try {
                getConditionPausado().await();
            } catch (InterruptedException e) {
                logger.error("La ejecución del mapa ha sido interrumpida mientras estaba pausado");
            } finally {
                getLockPausado().unlock();
            }
        }
    }

    public synchronized void actualizarEstadisticas () {
        int HR = comedor.getHumanosComedor().size() + descanso.getHumanosDescanso().size() + zonaComun.getHumanosComun().size();
        estadisticas.getHumanosRefugio().set(HR);

        for (int i = 0; i < 4; i++) {
            Tunel esteTunel = tuneles[i];
            int humanoPasando = esteTunel.getIdHumanoTunel().get() == null ? 0 : 1;
            int humanosEsteTunel = humanoPasando + esteTunel.getHumanosEsperando().size() +
                    esteTunel.getHumanosSeguros().size() + esteTunel.getHumanosRiesgo().size();

            estadisticas.getHumanosTuneles()[i].set(humanosEsteTunel);

            estadisticas.getHumanosRiesgo()[i].set(zonasRiesgo[i].getHumanos().size());
            estadisticas.getZombiesRiesgo()[i].set(zonasRiesgo[i].getZombies().size());
        }
    }

    public Comedor getComedor() {
        return comedor;
    }

    public ZonaComun getZonaComun() {
        return zonaComun;
    }

    public Descanso getDescanso() {
        return descanso;
    }

    public ZonaRiesgo[] getZonasRiesgo() {
        return zonasRiesgo;
    }

    public Tunel[] getTuneles() {
        return tuneles;
    }

    public boolean isPausado() {
        return pausado.get();
    }

    public void setPausado(boolean pausado) {
        this.pausado.set(pausado);
    }

    public Condition getConditionPausado() {
        return conditionPausado;
    }

    public Lock getLockPausado() {
        return lockPausado;
    }

    public servidorRMI getServidor() {
        return servidor;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }
}
