package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import simulacion.entorno.Mapa;
import simulacion.exceptions.unexpectedPriorityException;

public class Humano extends Thread implements Comparable<Humano> {
    private static final Logger log = LogManager.getLogger(Zombie.class);

    private int prioridadTunel = -1;

    private String id;

    public Mapa getMapa() {
        return mapa;
    }

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
        mapa.getTuneles()[0].esperarSeguro(this);
        try {
            while(!Thread.currentThread().isInterrupted()) {
                log.info("El humano " + id + " sigue vivo.");
                sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("El humano " + id + " ha muerto asesinado.");
        }
    }

    @Override
    public int compareTo(@NotNull Humano otro) {
        if (this.prioridadTunel == -1) {
            throw new unexpectedPriorityException();
        } else {
            return this.prioridadTunel - otro.prioridadTunel;
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
}
