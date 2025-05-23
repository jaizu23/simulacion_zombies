package servidor.simulacion_zombies;

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
        logger.info("Inciando servidor");
        servidorRMI servidor = new servidorRMI();
        Mapa mapa = new Mapa(servidor);
        servidor.setMapa(mapa);

        servidor.inicializarServidor();

        FXMLLoader mainLoader = new FXMLLoader(mainApplication.class.getResource("main-view.fxml"));
        mainLoader.setController(new mainViewController(mapa));
        Scene mainScene = new Scene(mainLoader.load(), 1024, 576);

        mainStage.setTitle("Apocalipsis");
        mainStage.setResizable(false);
        mainStage.setScene(mainScene);
        mainStage.setX(0);
        mainStage.setY(0);
        mainStage.show();

        logger.info("Mostrando vista servidor");
        mapa.start();
    }
}