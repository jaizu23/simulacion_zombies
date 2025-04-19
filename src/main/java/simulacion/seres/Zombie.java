package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;

import java.util.Random;

public class Zombie extends Thread{
    private static final Logger log = LogManager.getLogger(Zombie.class);

    public String getIdZombie() {
        return idZombie;
    }

    private String idZombie;
    private final Mapa mapa;
    private int contadorMuertes;



    public Zombie(String id, Mapa mapa) {
        this.idZombie = id;
        this.mapa = mapa;
        this.contadorMuertes = 0;
    }

    public void esperar(int tiempo) throws InterruptedException {
        sleep(tiempo);
    }

    public void run() {
        while (true) {
            try {
                Random numero = new Random();
                int pos = numero.nextInt(4);

                if (this.mapa.getZonasRiesgo()[pos].hayHumanosDisponibles()) {
                    this.mapa.getZonasRiesgo()[pos].atacar(this);
                }
                int espera = 2000 + (new Random().nextInt(1001));
                sleep(espera);

            } catch (Exception e) {
                log.error("Se ha interrumpido la ejecución del Zombie: "+ idZombie);
            }
        }
    }
}
