package simulacion.zonas.seguras;

import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

public class Descanso extends Refugio{
    private final LabelUpdateConcurrentHashMap<Humano> humanosDescanso = new LabelUpdateConcurrentHashMap<>(10000);

    public LabelUpdateConcurrentHashMap<Humano> getHumanosDescanso() {
        return humanosDescanso;
    }

    public void descansar (Humano humano, int inf, int sup) {
        String id = humano.getIdHumano();
        entrarZona(humanosDescanso, humano);
        pasarTiempo(id, inf, sup);
        salirZona(humanosDescanso, id);
    }
}
