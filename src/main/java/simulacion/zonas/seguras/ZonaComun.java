package simulacion.zonas.seguras;

import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

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
