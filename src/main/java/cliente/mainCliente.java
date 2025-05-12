package cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import servidor.simulacion_zombies.mainApplication;

public class mainCliente extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Stage clienteStage = new Stage();
        FXMLLoader clienteLoader = new FXMLLoader(mainApplication.class.getResource("cliente-view.fxml"));
        clienteLoader.setController(new clienteViewController());
        Scene clienteScene = new Scene(clienteLoader.load(), 1024, 576);
        clienteStage.setTitle("Apocalipsis - Cliente");
        clienteStage.setResizable(false);
        clienteStage.setScene(clienteScene);
        clienteStage.show();
    }
}
