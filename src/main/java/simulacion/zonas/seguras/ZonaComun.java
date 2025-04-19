package simulacion.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.seres.Humano;

import java.util.concurrent.ConcurrentHashMap;

public class ZonaComun extends Refugio{
    private static final Logger logger = LogManager.getLogger(ZonaComun.class);

    private ConcurrentHashMap<String, Humano> humanos = new ConcurrentHashMap<>(10000);


}
