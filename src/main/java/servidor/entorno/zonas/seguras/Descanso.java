package servidor.entorno.zonas.seguras;

import servidor.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import servidor.seres.Humano;

public class Descanso extends Refugio{
    private final LabelUpdateConcurrentHashMap<Humano> humanosDescanso = new LabelUpdateConcurrentHashMap<>(10000);

    public LabelUpdateConcurrentHashMap<Humano> getHumanosDescanso() {
        return humanosDescanso;
    }

    public void descansar (Humano humano, int inf, int sup) {
        String id = humano.getIdHumano();
        entrarZona(humanosDescanso, humano);
        pasarTiempo(humano, inf, sup);
        salirZona(humanosDescanso, id);
    }
}
