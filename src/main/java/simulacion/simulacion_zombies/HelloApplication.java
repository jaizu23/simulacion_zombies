package simulacion.simulacion_zombies;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import simulacion.entorno.Mapa;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final Logger logger = LogManager.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        Mapa mapa = new Mapa();
        logger.info("Test log");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        fxmlLoader.setController(new HelloController(mapa));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 576);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        mapa.start();
    }

    public static void main(String[] args) {
        launch();
    }
}