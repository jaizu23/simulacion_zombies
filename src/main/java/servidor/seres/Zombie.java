package servidor.seres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Zombie extends Thread {
    private static final Logger logger = LogManager.getLogger(Zombie.class);

    private Random r = new Random();

    private String idZombie;
    private Mapa mapa;
    private int zona = -1;
    private AtomicInteger contadorMuertes = new AtomicInteger(0);

    public Zombie (String id, int contadorMuertes) {
        this.idZombie = id;
        this.contadorMuertes.set(contadorMuertes);
    }

    public Zombie(String id, Mapa mapa) {
        this.idZombie = id;
        this.mapa = mapa;
        mapa.getEstadisticas().checkAddTopZombie(this);
        mapa.getServidor().actualizarEstadisticas();
    }

    public Zombie(String id, Mapa mapa, int zona) {
        this.idZombie = id;
        this.mapa = mapa;
        this.zona = zona;
        mapa.getEstadisticas().checkAddTopZombie(this);
        mapa.getServidor().actualizarEstadisticas();
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
                comprobarPausado();
                int nuevaPosicion = r.nextInt(3);
                if (nuevaPosicion >= zona) {
                    nuevaPosicion++;
                }
                comprobarPausado();
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


    private void realizarAtaque (Humano victima, int tiempo) {
        victima.interrupt();
        try {
            Thread.sleep(tiempo);
        } catch (InterruptedException e) {
            logger.error("Se ha producido un error mientras {} atacaba mortalmente a {}", idZombie, victima.getIdHumano());
        }
        synchronized (victima) {
            victima.notify();
        }
    }

    private void zombificar (Humano victima, int tiempo) {
        String nuevoId = "Z" + victima.getIdHumano().substring(1);
        victima.getAsesinado().set(true);
        logger.info("{} va a interrumpir a {}", idZombie, victima.getIdHumano());
        realizarAtaque(victima, tiempo);
        generarZombie(nuevoId);
    }

    private void generarZombie (String id) {
        Zombie nuevoZombie = new Zombie(id, mapa, zona);
        nuevoZombie.start();
        logger.info("{} ha nacido por asesinato.", nuevoZombie.getIdZombie());
    }

    public void atacar() {
        comprobarPausado();
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
                realizarAtaque(victima, duracionAtaque);
                sleep(duracionAtaque);
            }
        } catch (Exception e) {
            logger.error("Se ha producido un error cuando {} atacaba al humano {}", idZombie, victima.getIdHumano());
        }
    }

    public void comprobarPausado () {
        while(mapa.isPausado()) {
            mapa.getLockPausado().lock();
            try {
                mapa.getConditionPausado().await();
            } catch (InterruptedException e) {
                logger.error("La ejecución de {} ha sido interrumpida mientras estaba pausado", idZombie);
            } finally {
                mapa.getLockPausado().unlock();
            }
        }
    }

    public void sumarContadorMuertes() {
        contadorMuertes.set(contadorMuertes.get() + 1);
        mapa.getEstadisticas().checkAddTopZombie(this);
        mapa.getServidor().actualizarEstadisticas();
    }

    public String getIdZombie() {
        return idZombie;
    }

    public int getContadorMuertes() {
        return contadorMuertes.get();
    }
}
