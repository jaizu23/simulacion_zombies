package simulacion.seres;

import simulacion.entorno.Mapa;
import simulacion.exceptions.unexpectedPriorityException;

public class Humano extends Thread implements Comparable<Humano> {
    private int prioridadTunel = -1;

    private String id;
    private Mapa mapa;
    private Boolean marcado;

    public Humano(String id, Mapa mapa) {
        this.id = id;
        this.marcado = false;
        this.mapa = mapa;
    }
    public void run() {
        System.out.println("Prueba");
    }

    @Override
    public int compareTo(Humano otro) {
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
