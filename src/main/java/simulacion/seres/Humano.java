package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import simulacion.entorno.Mapa;
import simulacion.exceptions.killedHumanException;
import simulacion.exceptions.unexpectedPriorityException;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Humano extends Thread implements Comparable<Humano>, Ser {
    private static final Logger logger = LogManager.getLogger(Zombie.class);

    private final Random r = new Random();

    private int prioridadTunel = -1;

    private final String id;

    private int comida = 0;

    private final Mapa mapa;

    private int duracionAtaque;

    private final AtomicBoolean haPasadoTunel = new AtomicBoolean();
    private final AtomicBoolean marcado = new AtomicBoolean(false);
    private final AtomicBoolean asesinado = new AtomicBoolean(false);

    public Humano(String id, Mapa mapa) {
        this.id = id;
        this.mapa = mapa;
    }

    public void run() {
        while (!mapa.isPausado()) {
            try {
                try {
                    mapa.getZonaComun().prepararse(this);

                    int zona = r.nextInt(0, 4);

                    haPasadoTunel.set(false);
                    while (!haPasadoTunel.get()) {
                        mapa.getTuneles()[zona].esperarSeguro(this);
                    }

                    mapa.getZonasRiesgo()[zona].entrarZonaRiesgo(this);
                    mapa.getZonasRiesgo()[zona].recolectarComida(this);
                    mapa.getZonasRiesgo()[zona].salirZonaRiesgo(id, true);

                    haPasadoTunel.set(false);
                    while (!haPasadoTunel.get()) {
                        mapa.getTuneles()[zona].esperarRiesgo(this);
                    }
                    mapa.getComedor().depositarComida(this);

                    mapa.getDescanso().descansar(this, 2000, 4000);

                    mapa.getComedor().comer(this);

                    if (marcado.get()) {
                        mapa.getDescanso().descansar(this, 2000, 4000);
                        marcado.set(false);
                    }
                } catch (killedHumanException e) {
                    logger.info("{} ha sido asesinado.", id);
                    break;
                }
            } catch (Exception e) {
                logger.error("La ejecución de {} ha sido interrumpida inesperadamente: {}", id, e);
            }
        }
    }

    public void serAtacado () {
        try {
            interrupted();
            sleep(duracionAtaque);
        } catch (InterruptedException e) {
            logger.error("La ejecución de {} ha sido interrumpida inesperadamente mientras era atacado de forma no mortal: {}", id, e);
        }
    }

    @Override
    public int compareTo(@NotNull Humano otro) {
        if (this.prioridadTunel == -1) {
            throw new unexpectedPriorityException();
        } else {
            return Integer.compare(prioridadTunel, otro.getPrioridadTunel());
        }
    }

    public int getPrioridadTunel() {
        return prioridadTunel;
    }

    public void setPrioridadTunel(int prioridadTunel) {
        this.prioridadTunel = prioridadTunel;
    }

    public String getIdHumano() {
        return id;
    }

    public int getComida() {
        return comida;
    }

    public void añadirComida (int comida) {
        this.comida += comida;
    }

    public synchronized AtomicBoolean getMarcado() {
        return marcado;
    }

    public synchronized AtomicBoolean getAsesinado() {
        return asesinado;
    }

    public void setDuracionAtaque(int duracionAtaque) {
        this.duracionAtaque = duracionAtaque;
    }

    public AtomicBoolean getHaPasadoTunel() {
        return haPasadoTunel;
    }
}
