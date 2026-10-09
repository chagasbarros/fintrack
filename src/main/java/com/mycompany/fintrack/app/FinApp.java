package com.mycompany.fintrack.app;

import com.mycompany.fintrack.controller.PrincipalController;
import com.mycompany.fintrack.dao.Conexao;
import com.mycompany.fintrack.dao.TransacaoDAO;
import java.sql.Connection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Ponto de entrada da interface grafica. Ciclo de vida: init() -> start(Stage) -> stop()
public class FinApp extends Application {

    private Connection conexao;

    @Override
    public void start(Stage stage) throws Exception {
        // Uma conexao para o app inteiro, aberta ao iniciar e fechada em stop()
        conexao = Conexao.getConexao();
        Conexao.criarTabela(conexao);

        // FXMLLoader le o .fxml, cria os componentes e o controller indicado em fx:controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/fintrack/view/principal.fxml"));
        Parent raiz = loader.load();

        // O controller so recebe o DAO depois do load(), por isso ele carrega os dados em setDao()
        PrincipalController controller = loader.getController();
        controller.setDao(new TransacaoDAO(conexao));

        Scene cena = new Scene(raiz, 800, 500);
        cena.getStylesheets().add(getClass().getResource("/com/mycompany/fintrack/view/estilo.css").toExternalForm());

        stage.setTitle("FinTrack - Controle Financeiro");
        stage.setScene(cena);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (conexao != null) {
            conexao.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
