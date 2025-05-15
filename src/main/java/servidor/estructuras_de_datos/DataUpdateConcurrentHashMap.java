package servidor.estructuras_de_datos;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import servidor.entorno.Mapa;
import servidor.exceptions.uninitializedDataUpdateConcurrentHashMap;

import java.util.concurrent.ConcurrentHashMap;

public class DataUpdateConcurrentHashMap<V> extends ConcurrentHashMap<String, V> {
    private Label label = null;

    private final Mapa mapa;

    public DataUpdateConcurrentHashMap(Mapa mapa) {
        this.mapa = mapa;
    }

    public DataUpdateConcurrentHashMap(Mapa mapa, int n) {
        super(n);
        this.mapa = mapa;
    }

    @Override
    public V put(@NotNull String key,@NotNull V value) {
        if (label == null) {
            throw new uninitializedDataUpdateConcurrentHashMap();
        } else {
            V result = super.put(key, value); // Ya es Thread-safe
            updateLabel();
            updateEstadisticas();
            return result;
        }
    }

    public V remove(@NotNull String key) {
        if (label == null) {
            throw new uninitializedDataUpdateConcurrentHashMap();
        } else {
            V result = super.remove(key); // Ya es Thread-safe
            updateLabel();
            updateEstadisticas();
            return result;
        }
    }

    private void updateLabel() {
        Platform.runLater(() -> {
            label.setText(String.join(", ", keySet())); // Ya es Thread-safe
        });
    }

    private void updateEstadisticas() {
        mapa.getServidor().actualizarEstadisticas();
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(@NotNull Label label) {
        this.label = label;
    }
}
