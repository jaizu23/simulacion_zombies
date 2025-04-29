package simulacion.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;

import java.util.Random;

public class Zombie extends Thread {
    private static final Logger logger = LogManager.getLogger(Zombie.class);

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
        if (zona == -1) {
            zona = r.nextInt(4);
        }
        try {
            while (!mapa.isPausado()) {
                mapa.getZonasRiesgo()[zona].entrarZombie(this);
                if (hayHumanosDisponibles()) {
                    atacar();
                }
                int espera = r.nextInt(2000, 3001);
                Thread.sleep(espera);
                int nuevaPosicion = r.nextInt(3);
                if (nuevaPosicion >= zona) {
                    nuevaPosicion++;
                }
                mapa.getZonasRiesgo()[zona].salir(idZombie, false);
                zona = nuevaPosicion;
            }
        } catch (Exception e) {
            logger.error("Se ha interrumpido inesperadamente la ejecución de {}", idZombie);
        }

    }

    public boolean hayHumanosDisponibles() {
        return (!mapa.getZonasRiesgo()[zona].getPosiblesVictimas().isEmpty());
    }

    private void zombificar (Humano victima, int tiempo) {
        String nuevoId = "Z" + victima.getIdHumano().substring(1);
        victima.getAsesinado().set(true);
        logger.info("{} va a interrumpir a {}", idZombie, victima.getIdHumano());
        victima.interrupt();
        try {
            victima.join(); //esperamos a que el humano muera completamente.
        } catch (InterruptedException e) {
            logger.error("Error de {} esperando mientras {} moría", idZombie, victima.getIdHumano());
        }
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            logger.error("Se ha producido un error mientras {} atacaba mortalmente a {}", idZombie, victima.getIdHumano());
        }
        generarZombie(nuevoId);
    }

    private void generarZombie (String id) {
        Zombie nuevoZombie = new Zombie(id, mapa, zona);
        nuevoZombie.start();
        logger.info("{} ha nacido por asesinato.", nuevoZombie.getIdZombie());
    }

    private void notificar_ataque (Humano victima, int duracionAtaque) {
        victima.setDuracionAtaque(duracionAtaque);
        victima.interrupt();
    }

    public void atacar() {
        int duracionAtaque = r.nextInt(500, 1501);
        Humano victima = mapa.getZonasRiesgo()[zona].elegirVictima();
        logger.info("{} está siendo atacado por el zombie {} (número de muertes: {})", victima.getIdHumano(), idZombie, contadorMuertes);
        try {
            victima.getMarcado().set(true);
            logger.info("{} es marcado por el zombie {}", victima.getIdHumano(), idZombie);

            int posibilidades = r.nextInt(3);
            if (posibilidades < 1) {
                logger.info("{} va a ser asesinado a manos del zombie {}", victima.getIdHumano(), idZombie);
                zombificar(victima, duracionAtaque);
                sumarContadorMuertes();
                logger.info("{} ha muerto a manos del zombie {}", victima.getIdHumano(), idZombie);
            } else {
                logger.info("{} va a ser atacado no mortalmente por el zombie {}", victima.getIdHumano(), idZombie);
                notificar_ataque(victima, duracionAtaque);
                sleep(duracionAtaque);
            }
        } catch (Exception e) {
            logger.error("Se ha producido un error cuando {} atacaba al humano {}", idZombie, victima.getIdHumano());
        }
    }
}
