package simulacion.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ZonaComun extends Refugio{
    private static final Logger logger = LogManager.getLogger(ZonaComun.class);

    private Random r = new Random();

    private final LabelUpdateConcurrentHashMap<Humano> humanosComun = new LabelUpdateConcurrentHashMap<>(10000);

    public void prepararse(Humano humano) {
        String id = humano.getIdHumano();
        entrarZona(humanosComun, humano);
        pasarTiempo(id, 1000, 2000);
        salirZona(humanosComun, id);
    }

    public LabelUpdateConcurrentHashMap<Humano> getHumanosComun() {
        return humanosComun;
    }
}
