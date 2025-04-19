package simulacion.entorno;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.seres.*;
import simulacion.zonas.seguras.*;
import simulacion.zonas.*;

import java.util.Random;

public class Mapa extends Thread{
    private static final Logger logger = LogManager.getLogger(Mapa.class);

    private Random r = new Random();

    private Comedor comedor = new Comedor();
    private ZonaComun zonaComun = new ZonaComun();
    private Descanso descanso = new Descanso();
    private ZonaRiesgo[] zonasRiesgo = new ZonaRiesgo[4];
    private Tunel[] tueneles = new Tunel[4];


    public void run () {
        for (int i = 0; i < 10000; i++) {
            Humano humano = new Humano(this);
            humano.start();
            try {
                sleep(r.nextInt(500, 2000));
            } catch (InterruptedException e) {
                logger.error("Se ha interrumpido un hilo mientras esperaba al crear los humanos{}", String.valueOf(e));
            }
        }
        Zombie zombie = new Zombie(this);
        zombie.start();
    }
}
