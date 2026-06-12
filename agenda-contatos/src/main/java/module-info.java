
module com.agenda {

    // Bibliotecas
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    exports com.agenda;

    // Pacotes  JavaFX
    opens com.agenda to javafx.fxml;
    opens com.agenda.controller to javafx.fxml;
    opens com.agenda.model to javafx.base;   // necessário para PropertyValueFactory na tabela
}
