package simulacion.estructuras_de_datos;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import simulacion.exceptions.uninitializedLabelUpdateConcurrentHashMap;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LabelUpdateConcurrentHashMapArray<V> extends CopyOnWriteArrayList<ConcurrentHashMap<String, V>> {

    private Label label = null;

    public LabelUpdateConcurrentHashMapArray() {}

    public LabelUpdateConcurrentHashMapArray(CopyOnWriteArrayList<ConcurrentHashMap<String, V>> listaMapas){
        super(listaMapas);
    }

    public LabelUpdateConcurrentHashMapArray(CopyOnWriteArrayList<ConcurrentHashMap<String, V>> listaMapas, Label label) {
        super(listaMapas);
        this.label = label;
    }

    public V put(int n, @NotNull String key,@NotNull V value) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            V result = get(n).put(key, value);
            updateLabel();
            return result; // Ya es Thread-safe
        }
    }

    public V remove(int n, @NotNull String key) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            V result = get(n).remove(key);
            updateLabel();
            return result; // Ya es Thread-safe
        }
    }

    private void updateLabel() {
        ArrayList<String> clavesMapas = new ArrayList<>();
        for (int i = 0; i < size(); i++) {
            ConcurrentHashMap.KeySetView<String, V> keySet = get(i).keySet();
            if (!keySet.isEmpty()) {
                clavesMapas.add(String.join(", ", keySet));
            }
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
