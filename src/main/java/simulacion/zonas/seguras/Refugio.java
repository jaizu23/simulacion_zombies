package simulacion.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.Random;

public abstract class Refugio {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private Random r = new Random();

    protected void pasarTiempo (String id, int inf, int sup) {
        try {
            logger.info("El humano {} está pasando tiempo en la zona {}", id, this.getClass().getSimpleName());
            Thread.sleep(r.nextInt(inf, sup));
        } catch (InterruptedException e) {
            logger.error("Error mientras el humano {} pasaba tiempo en la zona {}: {}", id, this.getClass().getSimpleName(), e);
        }
    }

    protected void entrarZona (LabelUpdateConcurrentHashMap<Humano> listaZona, Humano humano) {
        String id = humano.getIdHumano();
        listaZona.put(id, humano);
    }

    protected void salirZona (LabelUpdateConcurrentHashMap<Humano> listaZona, String id) {
        listaZona.remove(id);
    }
}
