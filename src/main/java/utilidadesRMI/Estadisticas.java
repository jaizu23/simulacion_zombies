package utilidadesRMI;

import org.jetbrains.annotations.NotNull;
import servidor.entorno.Mapa;
import servidor.entorno.zonas.Tunel;
import servidor.seres.Zombie;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Estadisticas implements Serializable {
    private AtomicInteger humanosRefugio = new AtomicInteger(0);
    private AtomicInteger[] humanosTuneles = new AtomicInteger[4];
    private AtomicInteger[] humanosRiesgo = new AtomicInteger[4];
    private AtomicInteger[] zombiesRiesgo = new AtomicInteger[4];
    private Zombie[] topZombies = new Zombie[3];

    public Estadisticas () {
        for (int i = 0; i < 4; i++) {
            humanosTuneles[i] = new AtomicInteger(0);
            humanosRiesgo[i] = new AtomicInteger(0);
            zombiesRiesgo[i] = new AtomicInteger(0);
        }
        for (int i = 0; i < 3; i++) {
            topZombies[i] = new Zombie();
        }
    }

    public Estadisticas (@NotNull Estadisticas estadisticas) {
        humanosRefugio.set(estadisticas.getHumanosRefugio().get());
        for (int i = 0; i < 4; i++) {
            humanosTuneles[i].set(estadisticas.getHumanosTuneles()[i].get());
            humanosRiesgo[i].set(estadisticas.getHumanosRiesgo()[i].get());
            zombiesRiesgo[i].set(estadisticas.getZombiesRiesgo()[i].get());
        }
        for (int i = 0; i < 3; i++) {
            topZombies[i] = estadisticas.getTopZombies()[i];
        }
    }

    public synchronized void actualizar (Mapa mapa) {
        int HR = mapa.getComedor().getHumanosComedor().size() + mapa.getDescanso().getHumanosDescanso().size() + mapa.getZonaComun().getHumanosComun().size();
        humanosRefugio.set(HR);

        for (int i = 0; i < 4; i++) {
            Tunel esteTunel = mapa.getTuneles()[i];
            int humanoPasando = esteTunel.getIdHumanoTunel().get() == null ? 0 : 1;
            int humanosEsteTunel = humanoPasando + esteTunel.getHumanosEsperando().size() +
                    esteTunel.getHumanosSeguros().size() + esteTunel.getHumanosRiesgo().size();

            humanosTuneles[i].set(humanosEsteTunel);

            humanosRiesgo[i].set(mapa.getZonasRiesgo()[i].getHumanos().size());
            zombiesRiesgo[i].set(mapa.getZonasRiesgo()[i].getZombies().size());
        }
    }

    public synchronized void checkAddTopZombie (Zombie zombie) {
        if (zombie.getContadorMuertes() <= topZombies[0].getContadorMuertes()) {
            return;
        }
        ArrayList<Zombie> zombiesCandidatos = new ArrayList<>(4);
        for (int i = 0; i < 3; i++) {
            Zombie z = topZombies[i];
            zombiesCandidatos.set(i, z);
        }
        zombiesCandidatos.set(4, zombie);

        zombiesCandidatos.sort(Comparator.comparingInt(Zombie::getContadorMuertes));
        for (int i = 0; i < 3; i++) {
            topZombies[i] = zombiesCandidatos.get(i+1);
        }
    }

    public AtomicInteger getHumanosRefugio() {
        return humanosRefugio;
    }

    public void setHumanosRefugio(AtomicInteger humanosRefugio) {
        this.humanosRefugio = humanosRefugio;
    }

    public AtomicInteger[] getHumanosTuneles() {
        return humanosTuneles;
    }

    public void setHumanosTuneles(AtomicInteger[] humanosTuneles) {
        this.humanosTuneles = humanosTuneles;
    }

    public AtomicInteger[] getHumanosRiesgo() {
        return humanosRiesgo;
    }

    public void setHumanosRiesgo(AtomicInteger[] humanosRiesgo) {
        this.humanosRiesgo = humanosRiesgo;
    }

    public AtomicInteger[] getZombiesRiesgo() {
        return zombiesRiesgo;
    }

    public void setZombiesRiesgo(AtomicInteger[] zombiesRiesgo) {
        this.zombiesRiesgo = zombiesRiesgo;
    }

    public Zombie[] getTopZombies() {
        return topZombies;
    }
}
