package servidor.simulacion_zombies;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import servidor.entorno.Mapa;

import java.net.URL;
import java.util.ResourceBundle;

public class clienteViewController implements Initializable {
    @FXML
    private Label refugio;

    @FXML
    private Label HT1;
    @FXML
    private Label HT2;
    @FXML
    private Label HT3;
    @FXML
    private Label HT4;

    @FXML
    private Label HR1;
    @FXML
    private Label HR2;
    @FXML
    private Label HR3;
    @FXML
    private Label HR4;

    @FXML
    private Label ZR1;
    @FXML
    private Label ZR2;
    @FXML
    private Label ZR3;
    @FXML
    private Label ZR4;

    @FXML
    private Label rankingZombies;
    @FXML
    private Button pausarEjecucion;

    private Mapa mapa;

    public clienteViewController(Mapa mapa) {
        this.mapa = mapa;
    }

    private void initalizeElements() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initalizeElements();
    }
}
