package utilidadesRMI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.seres.Zombie;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Estadisticas implements Serializable {
    private static final Logger logger = LogManager.getLogger(Estadisticas.class);

    private final AtomicInteger humanosRefugio = new AtomicInteger(0);
    private final AtomicInteger[] humanosTuneles = new AtomicInteger[4];
    private final AtomicInteger[] humanosRiesgo = new AtomicInteger[4];
    private final AtomicInteger[] zombiesRiesgo = new AtomicInteger[4];

    private final CopyOnWriteArrayList<String> stringsTopZombies= new CopyOnWriteArrayList<>();
    private final transient ArrayList<Zombie> topZombies = new ArrayList<>();
    private final transient ArrayList<Zombie> zombiesCandidatos = new ArrayList<>();

    public Estadisticas () {
        for (int i = 0; i < 4; i++) {
            humanosTuneles[i] = new AtomicInteger(0);
            humanosRiesgo[i] = new AtomicInteger(0);
            zombiesRiesgo[i] = new AtomicInteger(0);
            zombiesCandidatos.add(new Zombie("Ninguno", -1));
        }
        for (int i = 0; i < 3; i++) {
            topZombies.add(new Zombie("Ninguno", -1));
            stringsTopZombies.add("");
        }
    }

    public synchronized void checkAddTopZombie (Zombie zombie) {
        logger.info("Eligiendo el top de zombies");
        if (zombie.getContadorMuertes() <= topZombies.getFirst().getContadorMuertes()) {
            return;
        }
        if (!topZombies.contains(zombie)) {
            for (int i = 0; i < 3; i++) {
                zombiesCandidatos.set(i, topZombies.get(i));
            }
            zombiesCandidatos.set(3, zombie);

            zombiesCandidatos.sort(Comparator.comparingInt(Zombie::getContadorMuertes));

            for (int i = 0; i < 3; i++) {
                StringBuilder stringZombie = new StringBuilder();
                topZombies.set(i, zombiesCandidatos.get(i+1));
                stringsTopZombies.set(i, stringZombie.append(topZombies.get(i).getIdZombie()).append(" - ").append(topZombies.get(i).getContadorMuertes()).append(" muertes\n").toString());
            }
        } else {
            topZombies.sort(Comparator.comparingInt(Zombie::getContadorMuertes));
            for (int i = 0; i < 3; i++) {
                StringBuilder stringZombie = new StringBuilder();
                stringsTopZombies.set(i, stringZombie.append(topZombies.get(i).getIdZombie()).append(" - ").append(topZombies.get(i).getContadorMuertes()).append(" muertes\n").toString());
            }
        }
    }

    public AtomicInteger getHumanosRefugio() {
        return humanosRefugio;
    }

    public AtomicInteger[] getHumanosTuneles() {
        return humanosTuneles;
    }

    public AtomicInteger[] getHumanosRiesgo() {
        return humanosRiesgo;
    }

    public AtomicInteger[] getZombiesRiesgo() {
        return zombiesRiesgo;
    }

    public CopyOnWriteArrayList<String> getStringsTopZombies() {
        return stringsTopZombies;
    }
}
