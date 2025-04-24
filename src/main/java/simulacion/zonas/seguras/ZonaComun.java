package simulacion.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.concurrent.ConcurrentHashMap;

public class ZonaComun extends Refugio{
    private static final Logger logger = LogManager.getLogger(ZonaComun.class);

    private final LabelUpdateConcurrentHashMap<Humano> humanosComun = new LabelUpdateConcurrentHashMap<>(10000);

    public LabelUpdateConcurrentHashMap<Humano> getHumanosComun() {
        return humanosComun;
    }
}
