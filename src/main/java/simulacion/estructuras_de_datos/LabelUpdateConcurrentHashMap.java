package simulacion.estructuras_de_datos;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import simulacion.exceptions.uninitializedLabelUpdateConcurrentHashMap;

import java.util.concurrent.ConcurrentHashMap;

public class LabelUpdateConcurrentHashMap<V> extends ConcurrentHashMap<String, V> {

    private Label label = null;

    public LabelUpdateConcurrentHashMap() {}

    public LabelUpdateConcurrentHashMap(int n) {
        super(n);
    }

    public LabelUpdateConcurrentHashMap(Label label) {
        this.label = label;
    }

    public LabelUpdateConcurrentHashMap(Label label, int n) {
        super(n);
        this.label = label;
    }

    @Override
    public V put(@NotNull String key,@NotNull V value) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            Platform.runLater(() -> label.setText(String.join(", ", keySet()))); // Ya es Thread-safe
            return super.put(key, value); // Ya es Thread-safe
        }
    }

    public V remove(@NotNull String key) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            Platform.runLater(() -> label.setText(String.join(", ", keySet()))); // Ya es Thread-safe
            return super.remove(key); // Ya es Thread-safe
        }
    }

    public Label getLabel() {
        return label;
    }

    public synchronized void setLabel(@NotNull Label label) {
        this.label = label;
    }
}
