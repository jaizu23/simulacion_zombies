package simulacion.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMapArray;
import simulacion.seres.Humano;
import simulacion.seres.Ser;
import simulacion.seres.Zombie;
import simulacion.zonas.seguras.ZonaComun;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger logger = LogManager.getLogger(ZonaRiesgo.class);

    private Random r = new Random();

    private int id;

    private ConcurrentHashMap<String, Humano> humanosLibres = new ConcurrentHashMap<>(10000);
    private ConcurrentHashMap<String, Humano> humanosCombatiendo = new ConcurrentHashMap<>(10000);
    private LabelUpdateConcurrentHashMapArray<Humano> humanos = new LabelUpdateConcurrentHashMapArray<>(new ArrayList<>(List.of(humanosLibres, humanosCombatiendo)));
    private LabelUpdateConcurrentHashMap<Zombie> zombies = new LabelUpdateConcurrentHashMap<>(10000);

    public ZonaRiesgo(int id){
        this.id = id;
    }

    public Boolean hayHumanosDisponibles() {
        return !humanos.getFirst().isEmpty();
    }

    private synchronized Humano elegirVictima() {
        ArrayList<Humano> listaHumanos = new ArrayList<>(humanos.getFirst().values());
        Random rand = new Random();
        int posVictima = rand.nextInt(listaHumanos.size());
        Humano victima = listaHumanos.get(posVictima);
        humanos.remove(0, victima.getIdHumano());
        humanos.put(1, victima.getIdHumano(), victima);
        return victima;
    }

    private void zombificar(Humano victima) throws InterruptedException {
        if (victima.getMarcado()) {
            String nuevoId = "Z" + victima.getIdHumano().substring(1);
            victima.interrupt();
            victima.join();        //esperamos a que el humano muera del todo.
            logger.info("El humano: " + victima.getId() + " muere.");
            generarZombie(nuevoId, victima.getMapa());
        }
    }

    private void generarZombie(String id, Mapa mapa) {
        Zombie nuevoZombie = new Zombie(id, mapa);
        nuevoZombie.run();
        logger.info("El zombie " + nuevoZombie.getId() + " ha nacido por asesinato.");
    }

    public void atacar(Zombie atacante) {
        Humano victima = elegirVictima();
        logger.info("El humano: "+victima.getId()+" está siendo atacado por el zombie " + atacante.getId());
        try {
            int tiempo = 500 + (new Random().nextInt(1001));
            victima.esperar(tiempo);
            atacante.esperar(tiempo);
            victima.marcar();
            logger.info("El humano " + victima.getId() + " es marcado por el zombie " + atacante.getId());
            Random rand = new Random();
            int posibilidades = rand.nextInt(3);
            if (posibilidades < 1) {
                zombificar(victima);
                atacante.sumarContadorMuertes();
                logger.info("El humano: "+victima.getId()+" ha muerto a manos del zombie " + atacante.getId());
            }

        } catch (InterruptedException e) {
            logger.error("Se ha producido un error cuando el zombie "+atacante.getId()+" atacaba al humano "+victima.getIdHumano());
        }
    }

    public void recolectarComida (Humano humano) {
        String id = humano.getIdHumano();
        logger.info("El humano {} está recolectando comida en la zona de riesgo {}", id, this.id);
        try {
            Thread.sleep(r.nextInt(3000, 5000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        humano.añadirComida(2);
        logger.info("El humano {} ha terminado de recolectar comida en la zona de riesgo {}", id, this.id);
    }

    public void entrarZonaRiesgo (Ser ser, boolean humano){
        if (ser.getClass() == Humano.class) {
            String id = ((Humano) ser).getIdHumano();

            humanos.put(0, id, (Humano) ser);
            logger.info("El humano {} ha entrado en la zona de riesgo {}", id, this.id);

            recolectarComida((Humano) ser);

            salirZonaRiesgo(id, true);
        } else {
        }
    }

    public void salirZonaRiesgo (String id, boolean humano){
        humanos.remove(0, id);
    }

    public LabelUpdateConcurrentHashMapArray<Humano> getHumanos() {
        return humanos;
    }

    public LabelUpdateConcurrentHashMap<Zombie> getZombies() {
        return zombies;
    }
}
