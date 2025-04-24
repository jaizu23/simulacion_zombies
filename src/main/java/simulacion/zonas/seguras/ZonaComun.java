package simulacion.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.Random;

public class ZonaComun extends Refugio{
    private final LabelUpdateConcurrentHashMap<Humano> humanosComun = new LabelUpdateConcurrentHashMap<>(10000);

    public void prepararse(Humano humano) {
        String id = humano.getIdHumano();
        entrarZona(humanosComun, humano);
        pasarTiempo(humano, 1000, 2000);
        salirZona(humanosComun, id);
    }

    public LabelUpdateConcurrentHashMap<Humano> getHumanosComun() {
        return humanosComun;
    }
}
