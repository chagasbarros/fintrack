package com.mycompany.fintrack.controller;

import com.mycompany.fintrack.dao.TransacaoDAO;
import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;

// Controller de transacao-form.fxml: a mesma tela cria (transacao == null) ou edita uma transacao
public class TransacaoFormController {

    private static final Logger LOG = Logger.getLogger(TransacaoFormController.class.getName());
    // STRICT rejeita datas inexistentes como 31/02 (mesma regra do console)
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    @FXML private Label lblTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private TextField txtValor;
    @FXML private ComboBox<TipoTransacao> cbTipo;
    @FXML private DatePicker dpData;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private TransacaoDAO dao;
    private Transacao transacao; // null = nova
    private boolean salvo;

    @FXML
    private void initialize() {
        cbTipo.getItems().setAll(TipoTransacao.values());
        dpData.setConverter(new LocalDateStringConverter(FORMATO_DATA, FORMATO_DATA));
        dpData.setValue(LocalDate.now());

        btnSalvar.setOnAction(evento -> salvar());
        btnCancelar.setOnAction(evento -> fechar());
    }

    public void setDao(TransacaoDAO dao) {
        this.dao = dao;
    }

    // Recebe a transacao a editar e preenche os campos; com null, o formulario fica em modo "nova"
    public void setTransacao(Transacao transacao) {
        this.transacao = transacao;
        if (transacao == null) {
            return;
        }
        lblTitulo.setText("Editar transacao");
        txtDescricao.setText(transacao.getDescricao());
        txtValor.setText(String.valueOf(transacao.getValor()).replace('.', ','));
        cbTipo.setValue(transacao.getTipo());
        dpData.setValue(transacao.getData());
    }

    // Quem abriu a janela consulta isto para saber se precisa recarregar a tabela
    public boolean foiSalvo() {
        return salvo;
    }

    private void salvar() {
        try {
            // Criar um objeto novo reaproveita a validacao do modelo (Parte 1):
            // se algum campo for invalido, o construtor lanca IllegalArgumentException
            Transacao dados = new Transacao(txtDescricao.getText(), lerValor(), cbTipo.getValue(), lerData());

            if (transacao == null) {
                dao.inserir(dados);
            } else {
                dados.setId(transacao.getId());
                dao.atualizar(dados);
            }
            salvo = true;
            fechar();
        } catch (NumberFormatException e) {
            mostrarAviso("Valor invalido. Use apenas numeros, por exemplo 1200,50.");
        } catch (DateTimeParseException e) {
            mostrarAviso("Data invalida. Use o formato dd/mm/aaaa.");
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao salvar transacao", e);
            mostrarAviso("Nao foi possivel salvar no banco: " + e.getMessage());
        }
    }

    // Aceita virgula ou ponto como separador decimal
    private double lerValor() {
        return Double.parseDouble(txtValor.getText().trim().replace(',', '.'));
    }

    // Le o texto digitado (o DatePicker so atualiza o valor ao apertar Enter ou sair do campo)
    private LocalDate lerData() {
        String texto = dpData.getEditor().getText();
        return texto.isBlank() ? null : LocalDate.parse(texto.trim(), FORMATO_DATA);
    }

    private void mostrarAviso(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.initOwner(btnSalvar.getScene().getWindow());
        alerta.setTitle("Dados invalidos");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void fechar() {
        ((Stage) btnSalvar.getScene().getWindow()).close();
    }
}
