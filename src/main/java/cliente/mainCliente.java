package cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import servidor.simulacion_zombies.mainApplication;

public class mainCliente extends Application {
    private static final Logger logger = LogManager.getLogger(mainCliente.class);

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Inicializando cliente");
        clienteRMI cliente = new clienteRMI();

        Stage clienteStage = new Stage();
        FXMLLoader clienteLoader = new FXMLLoader(mainApplication.class.getResource("/cliente/cliente-view.fxml"));
        clienteViewController controlador = new clienteViewController(cliente);

        cliente.inicializarCliente(controlador);

        clienteLoader.setController(controlador);
        Scene clienteScene = new Scene(clienteLoader.load(), 1024, 576);
        clienteStage.setTitle("Apocalipsis - Cliente");
        clienteStage.setResizable(false);
        clienteStage.setScene(clienteScene);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double x = screenBounds.getMaxX() - 1024;
        double y = screenBounds.getMaxY() - 610;
        clienteStage.setX(x);
        clienteStage.setY(y);
        logger.info("Mostrando vista cliente");
        clienteStage.show();
    }
}
