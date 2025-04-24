package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;

import java.util.Random;

public class Zombie extends Thread implements Ser{
    private static final Logger log = LogManager.getLogger(Zombie.class);

    Random r = new Random();

    public String getIdZombie() {
        return idZombie;
    }

    private final String idZombie;
    private final Mapa mapa;
    private int zona = -1;
    private int contadorMuertes;

    public int getContadorMuertes() {
        return contadorMuertes;
    }

    public void sumarContadorMuertes() {
        contadorMuertes += 1;
    }


    public Zombie(String id, Mapa mapa) {
        this.idZombie = id;
        this.mapa = mapa;
        this.contadorMuertes = 0;
    }

    public Zombie(String id, Mapa mapa, int zona) {
        this.idZombie = id;
        this.mapa = mapa;
        this.zona = zona;
        this.contadorMuertes = 0;
    }

    public void run() {
        int pos;
        if (zona == -1) {
            pos = r.nextInt(4);
        } else {
            pos = zona;
        }
        try {
            while (!mapa.isPausado()) {
                mapa.getZonasRiesgo()[pos].entrarZonaRiesgo(this);
                if (this.mapa.getZonasRiesgo()[pos].hayHumanosDisponibles()) {
                    this.mapa.getZonasRiesgo()[pos].atacar(this);
                }
                int espera = r.nextInt(2000, 3001);
                Thread.sleep(espera);
                int nuevaPosicion = r.nextInt(3);
                if (nuevaPosicion >= pos) {
                    nuevaPosicion++;
                }
                mapa.getZonasRiesgo()[pos].salirZonaRiesgo(idZombie, false);
                pos = nuevaPosicion;
            }
        } catch (Exception e) {
            log.error("Se ha interrumpido la ejecución del Zombie: {}", idZombie);
        }

    }
}
