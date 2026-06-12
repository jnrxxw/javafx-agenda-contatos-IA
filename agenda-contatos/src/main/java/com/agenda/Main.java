package com.agenda;

import com.agenda.dao.ConexaoDB;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

 /// Ponto de entrada do aplicativo.

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // ── Passo 1: Conectar (e configurar automaticamente, se for a 1ª vez) ──
        if (ConexaoDB.getConexao() == null) {
            mostrarErrodeConexao();
            return; // não abre a tela
        }

        // ── Passo 2: Carregar e exibir a tela principal ───────────────────────
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/agenda/main-view.fxml")
        );

        Scene scene = new Scene(loader.load(), 850, 580);
        scene.getStylesheets().add(
            getClass().getResource("/com/agenda/style.css").toExternalForm()
        );

        stage.setTitle("Agenda de Contatos");
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(480);
        stage.show();
    }

    /** Fecha a conexão quando o usuário fechar o app */
    @Override
    public void stop() {
        ConexaoDB.fecharConexao();
    }


     /// Exibe uma janela de erro  quando ocorre um erro na conexao

    private void mostrarErrodeConexao() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro de Conexão");
        alert.setHeaderText("Não foi possível conectar ao PostgreSQL");
        alert.setContentText(
            "Verifique os seguintes pontos:\n\n" +
            "  1. O PostgreSQL está instalado e rodando?\n" +
            "     → Procure 'Services' no Windows ou rode:\n" +
            "       sudo service postgresql start  (Linux/Mac)\n\n" +
            "  2. A senha está correta?\n" +
            "     → Arquivo: src/main/java/com/agenda/dao/ConexaoDB.java\n" +
            "     → Linha:   private static final String SENHA = \"...\";\n\n" +
            "  3. O usuário 'postgres' tem permissão para criar bancos?"
        );
        alert.showAndWait();
        Platform.exit(); // fecha o app após o usuário clicar em OK
    }

    public static void main(String[] args) {
        launch(args);
    }
}
