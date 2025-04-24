package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import simulacion.entorno.Mapa;
import simulacion.exceptions.unexpectedPriorityException;

import java.util.Random;

public class Humano extends Thread implements Comparable<Humano>, Ser {
    private static final Logger log = LogManager.getLogger(Zombie.class);

    private Random r = new Random();

    private int prioridadTunel = -1;

    private String id;

    private int comida = 0;

    private Mapa mapa;

    private Boolean marcado;

    public Humano(String id, Mapa mapa) {
        this.id = id;
        this.marcado = false;
        this.mapa = mapa;
    }


    public void esperar(int tiempo) throws InterruptedException {
        sleep(tiempo);
    }
    public void marcar() {
        marcado = true;
    }

    public void run() {
        while (!mapa.isPausado()) {
            mapa.getZonaComun().prepararse(this);

            int zona = r.nextInt(0, 4);
            mapa.getTuneles()[zona].esperarSeguro(this);

            mapa.getZonasRiesgo()[zona].entrarZonaRiesgo(this, true);

            mapa.getTuneles()[zona].esperarRiesgo(this);

            mapa.getComedor().depositarComida(this);

            mapa.getDescanso().descansar(this, 2000, 4000);

            mapa.getComedor().comer(this);

            if (marcado) {
                mapa.getDescanso().descansar(this, 3000, 5000);
            }
//            try {
//                while (!Thread.currentThread().isInterrupted()) {
//                    log.info("El humano " + id + " sigue vivo.");    //temporal.
//                    sleep(1000);
//                }
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } finally {
//                log.info("El humano " + id + " ha muerto asesinado.");
//            }
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

    public Boolean getMarcado() {
        return marcado;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public int getComida() {
        return comida;
    }

    public void añadirComida (int comida) {
        this.comida += comida;
    }
}
