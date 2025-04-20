package simulacion.simulacion_zombies;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.seres.Humano;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    private static final Logger logger = LogManager.getLogger(HelloController.class);

    private Mapa mapa;

    @FXML
    private Label welcomeText;

    @FXML
    private Label esperandoTunel1;

    public HelloController(Mapa mapa) {
        this.mapa = mapa;
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private AnchorPane anchor_left;

    private void initializeListeners () {
        ObservableMap<String, Humano> humanosST1 = mapa.getTuneles()[0].getHumanosSeguros();
        humanosST1.addListener((MapChangeListener<String, Humano>) _ -> {
            Platform.runLater(()->esperandoTunel1.setText(String.join(", ", humanosST1.keySet())));
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeListeners();
    }
}