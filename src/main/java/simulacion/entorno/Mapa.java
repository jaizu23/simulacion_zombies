package simulacion.entorno;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.seres.*;
import simulacion.zonas.seguras.*;
import simulacion.zonas.*;

import java.util.Random;

public class Mapa extends Thread{
    private static final Logger logger = LogManager.getLogger(Mapa.class);

    private final Random r = new Random();

    private final Comedor comedor = new Comedor();
    private final ZonaComun zonaComun = new ZonaComun();
    private final Descanso descanso = new Descanso();

    private final ZonaRiesgo[] zonasRiesgo = new ZonaRiesgo[4];
    private final Tunel[] tuneles = new Tunel[4];

    private boolean pausado = false;

    public Mapa () {
        for (int i = 0; i < 4; i++) {
            zonasRiesgo[i] = new ZonaRiesgo(i);
            tuneles[i] = new Tunel(i);
        }
    }

    public void run () {
        Zombie zombie = new Zombie("Z0000", this);
        zombie.start();
        for (int i = 1; i < 10000; i++) {
            Humano humano = new Humano("H" + String.format("%04d", i),this);
            humano.start();
            logger.info(humano.getIdHumano() + " ha nacido.");
            try {
                sleep(r.nextInt(500, 2000));
            } catch (InterruptedException e) {
                logger.error("Se ha interrumpido un hilo mientras esperaba al crear los humanos{}", String.valueOf(e));
            }
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
        return pausado;
    }

    public void setPausado(boolean pausado) {
        this.pausado = pausado;
    }
}
