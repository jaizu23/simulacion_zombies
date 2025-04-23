package simulacion.simulacion_zombies;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class mainViewController implements Initializable {
    private static final Logger logger = LogManager.getLogger(mainViewController.class);

    private Mapa mapa;

    @FXML
    private Label welcomeText;

    @FXML
    private Label segurosTunel1;
    @FXML
    private Label segurosTunel2;
    @FXML
    private Label segurosTunel3;
    @FXML
    private Label segurosTunel4;

    @FXML
    private Label esperandoTunel1;
    @FXML
    private Label esperandoTunel2;
    @FXML
    private Label esperandoTunel3;
    @FXML
    private Label esperandoTunel4;

    @FXML
    private Label tunel1;
    @FXML
    private Label tunel2;
    @FXML
    private Label tunel3;
    @FXML
    private Label tunel4;

    @FXML
    private Label riesgoTunel1;
    @FXML
    private Label riesgoTunel2;
    @FXML
    private Label riesgoTunel3;
    @FXML
    private Label riesgoTunel4;

    public mainViewController(Mapa mapa) {
        this.mapa = mapa;
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private AnchorPane anchor_left;

    private void initializeLabelsData () {
        ArrayList<Label> labelsS = new ArrayList<>(List.of(segurosTunel1, segurosTunel2, segurosTunel3, segurosTunel4));
        ArrayList<Label> labelsE = new ArrayList<>(List.of(esperandoTunel1, esperandoTunel2, esperandoTunel3, esperandoTunel4));
        ArrayList<Label> labelsR = new ArrayList<>(List.of(riesgoTunel1, riesgoTunel2, riesgoTunel3, riesgoTunel4));
        ArrayList<Label> labelsT = new ArrayList<>(List.of(tunel1, tunel2, tunel3, tunel4));

        for (int i = 0; i < 1; i++) {
            LabelUpdateConcurrentHashMap<Humano> humanosST = mapa.getTuneles()[i].getHumanosSeguros();
            humanosST.setLabel(labelsS.get(i));

            LabelUpdateConcurrentHashMap<Humano> humanosET = mapa.getTuneles()[i].getHumanosEsperando();
            humanosET.setLabel(labelsE.get(i));

            LabelUpdateConcurrentHashMap<Humano> humanosRT = mapa.getTuneles()[i].getHumanosRiesgo();
            humanosRT.setLabel(labelsR.get(i));

            StringProperty humanoT = mapa.getTuneles()[i].getIdHumanoTunel();
            labelsT.get(i).textProperty().bind(humanoT);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLabelsData();
    }
}