package simulacion.seres;

import simulacion.entorno.Mapa;

public class Humano extends Thread {

    public String idHumano;
    public Mapa mapa;
    public Boolean marcado;

    public Humano(String id, Mapa mapa) {
        this.idHumano = id;
        this.marcado = false;
        this.mapa = mapa;
    }
    public void run() {
        System.out.println("Prueba");
    }
}
