package servidor.entorno.zonas.seguras;

import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.DataUpdateConcurrentHashMap;
import servidor.seres.Humano;

public class Descanso extends Refugio{
    private final DataUpdateConcurrentHashMap<Humano> humanosDescanso;

    public Descanso (Mapa mapa) {
        humanosDescanso = new DataUpdateConcurrentHashMap<>(mapa, 10000);
    }

    public void descansar (Humano humano, int inf, int sup) {
        String id = humano.getIdHumano();
        entrarZona(humanosDescanso, humano);
        pasarTiempo(humano, inf, sup);
        salirZona(humanosDescanso, id);
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosDescanso() {
        return humanosDescanso;
    }
}
