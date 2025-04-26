package simulacion.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.exceptions.killedHumanException;
import simulacion.seres.Humano;
import simulacion.seres.Ser;
import simulacion.seres.Zombie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger logger = LogManager.getLogger(ZonaRiesgo.class);

    private final Random r = new Random();

    private final int zona;

    private final LabelUpdateConcurrentHashMap<Humano> humanos = new LabelUpdateConcurrentHashMap<>(10000);
    private final LabelUpdateConcurrentHashMap<Zombie> zombies = new LabelUpdateConcurrentHashMap<>(10000);

    private final ConcurrentHashMap<String, Humano> posiblesVictimas = new ConcurrentHashMap<>(10000);

    public ZonaRiesgo(int zona){
        this.zona = zona;
    }

    public void recolectarComida (Humano humano){
        String id = humano.getIdHumano();
        logger.info("{} está recolectando comida en la zona de riesgo {}", id, this.zona);
        try {
            Thread.sleep(r.nextInt(3000, 5000));
            humano.añadirComida(2);
            logger.info("{} ha terminado de recolectar comida en la zona de riesgo {}", id, this.zona);
        } catch (InterruptedException e) {
            if (humano.getAsesinado().get()) {
                salirZonaRiesgo(id, true);
                throw new killedHumanException();
            } else if (humano.getMarcado().get()) {
                humano.serAtacado();
            } else {
                logger.error("Se ha producido un error cuando {} recogia comida", id);
            }
        }
    }

    public void entrarZonaRiesgo (Ser ser) {
        String id;
        if (ser.getClass() == Humano.class) {
            id = ((Humano) ser).getIdHumano();

            posiblesVictimas.put(id, (Humano) ser);
            humanos.put(id, (Humano) ser);
        } else {
            id = ((Zombie) ser).getIdZombie();

            zombies.put(id, (Zombie) ser);
        }
        logger.info("{} ha entrado en la zona de riesgo {}", id, this.zona);
    }

    public void salirZonaRiesgo (String id, boolean humano) {
        if (humano) {
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

    public LabelUpdateConcurrentHashMap<Humano> getHumanos() {
        return humanos;
    }

    public LabelUpdateConcurrentHashMap<Zombie> getZombies() {
        return zombies;
    }

    public ConcurrentHashMap<String, Humano> getPosiblesVictimas() {
        return posiblesVictimas;
    }
}
