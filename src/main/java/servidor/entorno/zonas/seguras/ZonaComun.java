package servidor.entorno.zonas.seguras;

import servidor.entorno.Mapa;
import servidor.estructuras_de_datos.DataUpdateConcurrentHashMap;
import servidor.seres.Humano;

public class ZonaComun extends Refugio{
    private final DataUpdateConcurrentHashMap<Humano> humanosComun;

    public ZonaComun (Mapa mapa) {
        humanosComun = new DataUpdateConcurrentHashMap<>(mapa, 10000);
    }

    public void prepararse(Humano humano) {
        String id = humano.getIdHumano();
        entrarZona(humanosComun, humano);
        pasarTiempo(humano, 1000, 2000);
        humano.comprobarPausado();
        salirZona(humanosComun, id);
    }

    public DataUpdateConcurrentHashMap<Humano> getHumanosComun() {
        return humanosComun;
    }
}
