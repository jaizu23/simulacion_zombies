package simulacion.zonas.seguras;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.util.concurrent.atomic.AtomicInteger;

public class Comedor extends Refugio{
    private final LabelUpdateConcurrentHashMap<Humano> humanosComedor = new LabelUpdateConcurrentHashMap<>(10000);

    private final IntegerProperty contadorComida = new SimpleIntegerProperty(0);

    public LabelUpdateConcurrentHashMap<Humano> getHumanosComedor() {
        return humanosComedor;
    }

    public IntegerProperty getContadorComida() {
        return contadorComida;
    }
}
