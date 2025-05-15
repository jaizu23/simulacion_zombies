package servidor.entorno.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.DataUpdateConcurrentHashMap;
import servidor.exceptions.killedHumanException;
import servidor.seres.Humano;
import servidor.seres.Zombie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger logger = LogManager.getLogger(ZonaRiesgo.class);

    private final Random r = new Random();

    private final int zona;

    private final DataUpdateConcurrentHashMap<Humano> humanos;
    private final DataUpdateConcurrentHashMap<Zombie> zombies;

    private final ConcurrentHashMap<String, Humano> posiblesVictimas = new ConcurrentHashMap<>(10000);

    public ZonaRiesgo(Mapa mapa, int zona){
        this.zona = zona;
        humanos = new DataUpdateConcurrentHashMap<>(mapa, 10000);
        zombies = new DataUpdateConcurrentHashMap<>(mapa, 10000);
    }

    public void recolectarComida (Humano humano) throws killedHumanException{
        String id = humano.getIdHumano();
        logger.info("{} está recolectando comida en la zona de riesgo {}", id, this.zona);
        try {
            Thread.sleep(r.nextInt(3000, 5000));
            humano.añadirComida(2);
            logger.info("{} ha terminado de recolectar comida en la zona de riesgo {}", id, this.zona);
        } catch (InterruptedException e) {
            if (humano.getAsesinado().get() || humano.getMarcado().get()) {
                try {
                    synchronized (humano) {
                        humano.wait();
                    }
                } catch (InterruptedException e1) {
                    logger.error("Se ha producido un error mientras {} era atacado", id);
                }
                if (humano.getAsesinado().get()) {
                    salir(id, true);
                    throw new killedHumanException();
                }
                logger.info("{} ha sobrevivido de ser atacado no mortalmente", id);
            } else {
                logger.error("Se ha producido un error cuando {} recogia comida", id);
            }
        }
        humano.comprobarPausado();
    }

    public void entrarHumano (Humano humano) {
        String id = humano.getIdHumano();

        posiblesVictimas.put(id, humano);
        humanos.put(id, humano);

        humano.comprobarPausado();
        logger.info("{} ha entrado en la zona de riesgo {}", id, this.zona);
    }

    public void entrarZombie (Zombie zombie) {
        String id = zombie.getIdZombie();

        zombies.put(id, zombie);

        zombie.comprobarPausado();
        logger.info("{} ha entrado en la zona de riesgo {}", id, this.zona);
    }

    public void salir (String id, boolean humano) {
        if (humano) {
            humanos.get(id).comprobarPausado();
            posiblesVictimas.remove(id);
            humanos.remove(id);
        } else  {
            zombies.remove(id);
        }
        logger.info("{} ha salido de la zona de riesgo {}", id, this.zona);
    }

    public synchronized Humano elegirVictima() {
        ArrayList<String> claves = new ArrayList<>(posiblesVictimas.keySet());

        int nVictima = r.nextInt(claves.size());
        Humano victima = posiblesVictimas.get(claves.get(nVictima));
        posiblesVictimas.remove(victima.getIdHumano());
        return victima;
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanos() {
        return humanos;
    }

    public DataUpdateConcurrentHashMap<Zombie> getZombies() {
        return zombies;
    }

    public ConcurrentHashMap<String, Humano> getPosiblesVictimas() {
        return posiblesVictimas;
    }
}
