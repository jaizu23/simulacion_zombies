package servidor.zonas.seguras;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import servidor.seres.Humano;

import java.util.Random;

public abstract class Refugio {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final Random r = new Random();

    protected void pasarTiempo (Humano humano, int inf, int sup) {
        String id = humano.getIdHumano();
        try {
            logger.info("{} está pasando tiempo en la zona {}", id, this.getClass().getSimpleName());
            Thread.sleep(r.nextInt(inf, sup));
        } catch (InterruptedException e) {
            logger.error("Error mientras el {} pasaba tiempo en la zona {}: {}", id, this.getClass().getSimpleName(), e);
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
