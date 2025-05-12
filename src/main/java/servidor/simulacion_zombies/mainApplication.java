package servidor.simulacion_zombies;

import cliente.clienteViewController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servidor.entorno.Mapa;

import java.io.IOException;

public class mainApplication extends Application {
    private static final Logger logger = LogManager.getLogger(mainApplication.class);

    @Override
    public void start(Stage mainStage) throws IOException {
        Mapa mapa = new Mapa();

        FXMLLoader mainLoader = new FXMLLoader(mainApplication.class.getResource("main-view.fxml"));
        mainLoader.setController(new mainViewController(mapa));
        Scene mainScene = new Scene(mainLoader.load(), 1024, 576);
        mainStage.setTitle("Apocalipsis");
        mainStage.setResizable(false);
        mainStage.setScene(mainScene);
        mainStage.show();

        mapa.start();
    }

    public static void main(String[] args) {
        launch();
    }
}