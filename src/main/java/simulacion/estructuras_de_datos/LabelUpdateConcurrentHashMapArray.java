package simulacion.estructuras_de_datos;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import simulacion.exceptions.uninitializedLabelUpdateConcurrentHashMap;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LabelUpdateConcurrentHashMapArray<V> extends ArrayList<ConcurrentHashMap<String, V>>{

    private Label label = null;

    public LabelUpdateConcurrentHashMapArray() {}

    public LabelUpdateConcurrentHashMapArray(ArrayList<ConcurrentHashMap<String, V>> listaMapas){
        super(listaMapas);
    }

    public LabelUpdateConcurrentHashMapArray(ArrayList<ConcurrentHashMap<String, V>> listaMapas, Label label) {
        super(listaMapas);
        this.label = label;
    }

    public V put(int n, @NotNull String key,@NotNull V value) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            updateLabel();
            return get(n).put(key, value); // Ya es Thread-safe
        }
    }

    public V remove(int n, @NotNull String key) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            updateLabel();
            return get(n).remove(key); // Ya es Thread-safe
        }
    }

    private void updateLabel() {
        ArrayList<String> clavesMapas = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            clavesMapas.add(String.join(", ", get(i).keySet()));
        }
        Platform.runLater(() -> label.setText(String.join(", ", clavesMapas))); // Ya es Thread-safe
    }

    public Label getLabel() {
        return label;
    }

    public synchronized void setLabel(@NotNull Label label) {
        this.label = label;
    }
}
