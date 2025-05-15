package cliente;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utilidadesRMI.Estadisticas;
import utilidadesRMI.EstadisticasListener;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

public class clienteViewController implements Initializable, EstadisticasListener {
    private clienteRMI cliente;

    @FXML
    private Label refugio;

    private ArrayList<Label>  humanosTuneles;
    @FXML
    private Label HT1;
    @FXML
    private Label HT2;
    @FXML
    private Label HT3;
    @FXML
    private Label HT4;

    private ArrayList<Label>  humanosRiesgo;
    @FXML
    private Label HR1;
    @FXML
    private Label HR2;
    @FXML
    private Label HR3;
    @FXML
    private Label HR4;

    private ArrayList<Label> zombiesRiesgo;
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

    public clienteViewController (clienteRMI cliente) {
        this.cliente = cliente;
    }

    @FXML
    private void onPausarBoton() {
        cliente.pausarReanudar();
    }

    private void initalizeElements() {
        humanosTuneles = new ArrayList<>(List.of(HT1, HT2, HT3, HT4));
        humanosRiesgo = new ArrayList<>(List.of(HR1, HR2, HR3, HR4));
        zombiesRiesgo = new ArrayList<>(List.of(ZR1, ZR2, ZR3, ZR4));
    }

    @Override
    public void actualizarEstadisticas(Estadisticas estadisticas) throws RemoteException {
        Platform.runLater(() -> {
            refugio.setText(String.valueOf(estadisticas.getHumanosRefugio().get()));
            for (int i=0; i<4; i++) {
                humanosTuneles.get(i).setText(String.valueOf(estadisticas.getHumanosTuneles()[i].get()));
                humanosRiesgo.get(i).setText(String.valueOf(estadisticas.getHumanosRiesgo()[i].get()));
                zombiesRiesgo.get(i).setText(String.valueOf(estadisticas.getZombiesRiesgo()[i].get()));
            }
            StringBuilder textTopZombies = new StringBuilder();
            CopyOnWriteArrayList<String> topZombies = estadisticas.getStringsTopZombies();

            for (int i=2; i>=0; i--) {
                textTopZombies.append(topZombies.get(i));
            }
            rankingZombies.setText(textTopZombies.toString());
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initalizeElements();
    }
}
