package simulacion.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.seres.Humano;
import simulacion.seres.Zombie;
import simulacion.zonas.seguras.ZonaComun;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger log = LogManager.getLogger(ZonaRiesgo.class);

    private int id;
    private ConcurrentHashMap<String, Humano> humanosLibres = new ConcurrentHashMap<>(10000);
    private ConcurrentHashMap<String, Humano> humanosCombatiendo = new ConcurrentHashMap<>(10000);
    private ConcurrentHashMap<String, Zombie> zombies = new ConcurrentHashMap<>(10000);

    public ZonaRiesgo(int id){
        this.id = id;
    }

    public Boolean hayHumanosDisponibles() {
        return !humanosLibres.isEmpty();
    }

    private synchronized Humano elegirVictima() {
        ArrayList<Humano> listaHumanos = new ArrayList<>(humanosLibres.values());
        Random rand = new Random();
        int posVictima = rand.nextInt(listaHumanos.size());
        Humano victima = listaHumanos.get(posVictima);
        humanosLibres.remove(victima.getIdHumano());
        humanosCombatiendo.put(victima.getIdHumano(), victima);
        return victima;
    }

    public void atacar(Zombie atacante) {
        Humano victima = elegirVictima();
        log.info("El humano: "+victima.getIdHumano()+" está siendo atacado por el zombie: "+atacante.getIdZombie());
        try {
            int tiempo = 500 + (new Random().nextInt(1001));
            victima.esperar(tiempo);
            atacante.esperar(tiempo);
            Random rand = new Random();
            int posibilidades = rand.nextInt(3);
            if (posibilidades >= 1) {
                victima.marcar();
            }else {

            }

        } catch (InterruptedException e) {
            log.error("Se ha producido un error cuando el zombie "+atacante.getIdZombie()+" atacaba al humano "+victima.getIdHumano());
        }
    }

    public void entrarZonaRiesgo(){

    }
    public void salirZonaRiesgo(){

    }
}
