package simulacion.zonas;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.seres.Humano;
import simulacion.seres.Zombie;
import simulacion.zonas.seguras.ZonaComun;

import java.util.concurrent.ConcurrentHashMap;

public class ZonaRiesgo {
    private static final Logger logger = LogManager.getLogger(ZonaRiesgo.class);

    private ConcurrentHashMap<String, Humano> humanos = new ConcurrentHashMap<>(10000);
    private ConcurrentHashMap<String, Zombie> zombies = new ConcurrentHashMap<>(10000);

}
