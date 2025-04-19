package simulacion.seres;

import simulacion.entorno.Mapa;

public class Zombie extends Thread{
    public String idZombie;
    public Mapa mapa;


    public Zombie(String id, Mapa mapa) {
        this.idZombie = id;
        this.mapa = mapa;
    }

    public void run() {

    }
}
