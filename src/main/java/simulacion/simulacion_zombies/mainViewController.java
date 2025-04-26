package simulacion.simulacion_zombies;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import simulacion.entorno.Mapa;
import simulacion.estructuras_de_datos.LabelUpdateConcurrentHashMap;
import simulacion.seres.Humano;
import simulacion.seres.Zombie;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class mainViewController implements Initializable {
    private static final Logger logger = LogManager.getLogger(mainViewController.class);

    // Labels zona segura
    @FXML
    private Label descanso;
    @FXML
    private Label comedor;
    @FXML
    private Label comida;
    @FXML
    private Label zonaComun;

    // Labels tunel
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

    // Labels zona de riesgo
    @FXML
    private Label humanos1;
    @FXML
    private Label humanos2;
    @FXML
    private Label humanos3;
    @FXML
    private Label humanos4;

    @FXML
    private Label zombies1;
    @FXML
    private Label zombies2;
    @FXML
    private Label zombies3;
    @FXML
    private Label zombies4;

    private final Mapa mapa;

    public mainViewController(Mapa mapa) {
        this.mapa = mapa;
    }

    private void initializeLabelsData () {
        // Labels zona segura
        LabelUpdateConcurrentHashMap<Humano> humanosDescanso = mapa.getDescanso().getHumanosDescanso();
        LabelUpdateConcurrentHashMap<Humano> humanosComedor = mapa.getComedor().getHumanosComedor();
        LabelUpdateConcurrentHashMap<Humano> humanosComun = mapa.getZonaComun().getHumanosComun();
        IntegerProperty contadorComida = mapa.getComedor().getContadorComida();

        humanosDescanso.setLabel(descanso);
        humanosComedor.setLabel(comedor);
        humanosComun.setLabel(zonaComun);
        comida.textProperty().bind(contadorComida.asString());

        // Labels túnel
        ArrayList<Label> labelsS = new ArrayList<>(List.of(segurosTunel1, segurosTunel2, segurosTunel3, segurosTunel4));
        ArrayList<Label> labelsE = new ArrayList<>(List.of(esperandoTunel1, esperandoTunel2, esperandoTunel3, esperandoTunel4));
        ArrayList<Label> labelsR = new ArrayList<>(List.of(riesgoTunel1, riesgoTunel2, riesgoTunel3, riesgoTunel4));
        ArrayList<Label> labelsT = new ArrayList<>(List.of(tunel1, tunel2, tunel3, tunel4));

        for (int i = 0; i < 4; i++) {
            LabelUpdateConcurrentHashMap<Humano> humanosST = mapa.getTuneles()[i].getHumanosSeguros();
            humanosST.setLabel(labelsS.get(i));

            LabelUpdateConcurrentHashMap<Humano> humanosET = mapa.getTuneles()[i].getHumanosEsperando();
            humanosET.setLabel(labelsE.get(i));

            LabelUpdateConcurrentHashMap<Humano> humanosRT = mapa.getTuneles()[i].getHumanosRiesgo();
            humanosRT.setLabel(labelsR.get(i));

            StringProperty humanoT = mapa.getTuneles()[i].getIdHumanoTunel();
            labelsT.get(i).textProperty().bind(humanoT);
        }

        // Labels zona de riesgo
        ArrayList<Label> humanosLabels = new ArrayList<>(List.of(humanos1, humanos2, humanos3, humanos4));
        ArrayList<Label> zombiesLabels = new ArrayList<>(List.of(zombies1, zombies2, zombies3, zombies4));

        for (int i = 0; i < 4; i++) {
            LabelUpdateConcurrentHashMap<Humano> humanosR = mapa.getZonasRiesgo()[i].getHumanos();
            humanosR.setLabel(humanosLabels.get(i));

            LabelUpdateConcurrentHashMap<Zombie> zombiesR = mapa.getZonasRiesgo()[i].getZombies();
            zombiesR.setLabel(zombiesLabels.get(i));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeLabelsData();
    }
}