module simulacion.simulacion_zombies {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.apache.logging.log4j;
    requires annotations;
    requires java.rmi;

    opens servidor.simulacion_zombies to javafx.fxml;
    exports servidor.simulacion_zombies;
    exports cliente;
    opens cliente to javafx.fxml;
    exports utilidadesRMI to java.rmi;
}