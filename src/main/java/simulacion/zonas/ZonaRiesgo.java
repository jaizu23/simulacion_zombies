package simulacion.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMapArray;
import simulacion.exceptions.killedHumanException;
import simulacion.seres.Humano;
import simulacion.seres.Ser;
import simulacion.seres.Zombie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger logger = LogManager.getLogger(ZonaRiesgo.class);

    private final Random r = new Random();

    private final int id;

    private final ConcurrentHashMap<String, Humano> humanosLibres = new ConcurrentHashMap<>(10000);
    private final ConcurrentHashMap<String, Humano> humanosCombatiendo = new ConcurrentHashMap<>(10000);
    private final LabelUpdateConcurrentHashMapArray<Humano> humanos = new LabelUpdateConcurrentHashMapArray<>(new ArrayList<>(List.of(humanosLibres, humanosCombatiendo)));
    private final LabelUpdateConcurrentHashMap<Zombie> zombies = new LabelUpdateConcurrentHashMap<>(10000);

    public ZonaRiesgo(int id){
        this.id = id;
    }

    public boolean hayHumanosDisponibles() {
        return !humanos.getFirst().isEmpty();
    }

    private synchronized Humano elegirVictima() {
        ArrayList<String> claves = new ArrayList<>(humanos.getFirst().keySet());
        Humano victima = humanos.getFirst().get(claves.get(r.nextInt(claves.size())));
        humanos.remove(0, victima.getIdHumano());
        humanos.put(1, victima.getIdHumano(), victima);
        return victima;
    }

    private void zombificar(Humano victima, Zombie atacante, int tiempo)  {
        if (victima.getMarcado()) {
            String nuevoId = "Z" + victima.getIdHumano().substring(1);
            victima.setAsesinado(true);
            victima.interrupt();
            try {
                victima.join(); //esperamos a que el humano muera del todo.
            } catch (InterruptedException e) {
                throw new killedHumanException();
            }
            try {
                Thread.sleep(tiempo);
            } catch (InterruptedException e) {
                logger.error("Se ha producido un error mientra {} atacaba a {}", atacante.getIdZombie(), victima.getIdHumano());
            }
            generarZombie(nuevoId, victima.getMapa());
        }
    }

    private void generarZombie(String id, Mapa mapa) {
        Zombie nuevoZombie = new Zombie(id, mapa, this.id);
        nuevoZombie.start();
        logger.info("{} ha nacido por asesinato.", nuevoZombie.getIdZombie());
    }

    public void atacar(Zombie atacante) {
        int tiempo = r.nextInt(500, 1501);
        Humano victima = elegirVictima();
        logger.info("{} está siendo atacado por el zombie {}", victima.getIdHumano(), atacante.getIdZombie());
        try {
            victima.marcar();
            logger.info("{} es marcado por el zombie {}", victima.getIdHumano(), atacante.getIdZombie());

            int posibilidades = r.nextInt(3);
            if (posibilidades < 1) {
                zombificar(victima, atacante, tiempo);
                atacante.sumarContadorMuertes();
                logger.info("{} ha muerto a manos del zombie {}", victima.getIdHumano(), atacante.getIdZombie());
            }
        } catch (Exception e) {
            if (victima.isAsesinado()) {
                throw new killedHumanException();
            } else {
                logger.error("Se ha producido un error cuando {} atacaba al humano {}", atacante.getIdZombie(), victima.getIdHumano());
            }
        }
    }

    public void recolectarComida (Humano humano){
        String id = humano.getIdHumano();
        logger.info("{} está recolectando comida en la zona de riesgo {}", id, this.id);
        try {
            Thread.sleep(r.nextInt(3000, 5000));
        } catch (InterruptedException e) {
            if (humano.isAsesinado()) {
                throw new killedHumanException();
            } else {
                logger.error("Se ha producido un error cuando {} recogia comida", id);
            }
        }
        humano.añadirComida(2);
        logger.info("{} ha terminado de recolectar comida en la zona de riesgo {}", id, this.id);
    }

    public void entrarZonaRiesgo (Ser ser) {
        String id;
        if (ser.getClass() == Humano.class) {
            id = ((Humano) ser).getIdHumano();

            humanos.put(0, id, (Humano) ser);
        } else {
            id = ((Zombie) ser).getIdZombie();

            zombies.put(id, (Zombie) ser);
        }
        logger.info("{} ha entrado en la zona de riesgo {}", id, this.id);
    }

    public void salirZonaRiesgo (String id, boolean humano) {
        if (humano) {
            humanos.remove(0, id);
        } else  {
            zombies.remove(id);
        }
        logger.info("{} ha salido de la zona de riesgo {}", id, this.id);
    }

    public LabelUpdateConcurrentHashMapArray<Humano> getHumanos() {
        return humanos;
    }

    public LabelUpdateConcurrentHashMap<Zombie> getZombies() {
        return zombies;
    }
}
