package servidor.estructuras_de_datos;

import javafx.application.Platform;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import servidor.exceptions.uninitializedLabelUpdateConcurrentHashMap;

import java.util.concurrent.ConcurrentHashMap;

public class LabelUpdateConcurrentHashMap<V> extends ConcurrentHashMap<String, V> {

    private Label label = null;

    public LabelUpdateConcurrentHashMap() {}

    public LabelUpdateConcurrentHashMap(int n) {
        super(n);
    }

    @Override
    public V put(@NotNull String key,@NotNull V value) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            V result = super.put(key, value);
            updateLabel();
            return result; // Ya es Thread-safe
        }
    }

    public V remove(@NotNull String key) {
        if (label == null) {
            throw new uninitializedLabelUpdateConcurrentHashMap();
        } else {
            V result = super.remove(key);
            updateLabel();
            return result; // Ya es Thread-safe
        }
    }

    private void updateLabel() {
        Platform.runLater(() -> {
            label.setText(String.join(", ", keySet())); // Ya es Thread-safe
        });
    }

    public Label getLabel() {
        return label;
    }

    public synchronized void setLabel(@NotNull Label label) {
        this.label = label;
    }
}
