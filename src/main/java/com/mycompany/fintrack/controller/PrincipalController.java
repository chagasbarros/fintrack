package com.mycompany.fintrack.controller;

import com.mycompany.fintrack.dao.TransacaoDAO;
import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.utils.Formatador;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Controller da tela principal.fxml: o FXML diz COMO a tela e, esta classe diz O QUE ela faz
public class PrincipalController {

    private static final Logger LOG = Logger.getLogger(PrincipalController.class.getName());
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // @FXML liga cada campo ao componente com o mesmo fx:id no arquivo .fxml
    @FXML private TableView<Transacao> tabela;
    @FXML private TableColumn<Transacao, LocalDate> colData;
    @FXML private TableColumn<Transacao, String> colDescricao;
    @FXML private TableColumn<Transacao, Double> colValor;
    @FXML private TableColumn<Transacao, TipoTransacao> colTipo;
    @FXML private Label lblSaldo;
    @FXML private Button btnNova;
    @FXML private Button btnEditar;
    @FXML private Button btnRemover;
    @FXML private Button btnRelatorio;

    // ObservableList avisa a TableView sempre que muda, e a tabela se redesenha sozinha
    private final ObservableList<Transacao> transacoes = FXCollections.observableArrayList();
    private TransacaoDAO dao;

    // Chamado automaticamente pelo FXMLLoader depois de preencher os campos @FXML
    @FXML
    private void initialize() {
        // PropertyValueFactory("data") chama getData() de cada Transacao
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        // CellFactory controla como o valor aparece na celula
        colData.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate data, boolean vazia) {
                super.updateItem(data, vazia);
                setText(vazia || data == null ? null : data.format(FORMATO_DATA));
            }
        });
        colValor.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean vazia) {
                super.updateItem(valor, vazia);
                getStyleClass().removeAll("valor-receita", "valor-despesa");
                Transacao transacao = getTableRow() == null ? null : getTableRow().getItem();
                if (vazia || valor == null || transacao == null) {
                    setText(null);
                    return;
                }
                setText(Formatador.formatarMoeda(valor));
                getStyleClass().add(transacao.getTipo() == TipoTransacao.RECEITA ? "valor-receita" : "valor-despesa");
            }
        });

        tabela.setItems(transacoes);
        tabela.setPlaceholder(new Label("Nenhuma transacao cadastrada."));

        // Editar e Remover so ficam habilitados quando ha uma linha selecionada
        btnEditar.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());
        btnRemover.disableProperty().bind(tabela.getSelectionModel().selectedItemProperty().isNull());

        // Eventos: setOnAction recebe o que fazer quando o botao for clicado
        btnNova.setOnAction(evento -> abrirFormulario(null));
        btnEditar.setOnAction(evento -> abrirFormulario(tabela.getSelectionModel().getSelectedItem()));
        btnRemover.setOnAction(evento -> removerSelecionada());
        btnRelatorio.setOnAction(evento -> abrirRelatorio());

        // Duplo clique numa linha preenchida tambem abre a edicao
        tabela.setRowFactory(t -> {
            TableRow<Transacao> linha = new TableRow<>();
            linha.setOnMouseClicked(evento -> {
                if (evento.getClickCount() == 2 && !linha.isEmpty()) {
                    abrirFormulario(linha.getItem());
                }
            });
            return linha;
        });
    }

    // Recebe o DAO depois do load() do FXML (initialize ja rodou, mas ainda sem dados)
    public void setDao(TransacaoDAO dao) {
        this.dao = dao;
        carregarTabela();
    }

    private void carregarTabela() {
        try {
            transacoes.setAll(dao.listarTodas());
            atualizarSaldo();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao carregar transacoes", e);
            mostrarErro("Nao foi possivel carregar as transacoes.", e);
        }
    }

    private void atualizarSaldo() {
        // Reaproveita a regra da Parte 2: ObservableList<Transacao> e uma List<? extends Transacao>
        double saldo = FinTracker.calcularSaldo(transacoes);
        lblSaldo.setText(Formatador.formatarMoeda(saldo));
        lblSaldo.getStyleClass().removeAll("saldo-positivo", "saldo-negativo");
        lblSaldo.getStyleClass().add(saldo >= 0 ? "saldo-positivo" : "saldo-negativo");
    }

    // transacao == null abre o formulario vazio (Nova); senao, preenchido (Editar)
    private void abrirFormulario(Transacao transacao) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/fintrack/view/transacao-form.fxml"));
            Stage janela = criarJanelaModal(loader, transacao == null ? "Nova transacao" : "Editar transacao");

            // getController() devolve a instancia criada pelo load(): e assim que passamos dados entre telas
            TransacaoFormController form = loader.getController();
            form.setDao(dao);
            form.setTransacao(transacao);

            // showAndWait() bloqueia ate a janela fechar; so entao recarregamos a tabela
            janela.showAndWait();
            if (form.foiSalvo()) {
                carregarTabela();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Erro ao abrir o formulario", e);
            mostrarErro("Nao foi possivel abrir o formulario.", e);
        }
    }

    private void abrirRelatorio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/fintrack/view/relatorio.fxml"));
            Stage janela = criarJanelaModal(loader, "Relatorio");

            RelatorioController relatorio = loader.getController();
            relatorio.setTransacoes(transacoes);
            janela.showAndWait();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Erro ao abrir o relatorio", e);
            mostrarErro("Nao foi possivel abrir o relatorio.", e);
        }
    }

    // Janela modal: bloqueia a tela principal enquanto estiver aberta
    private Stage criarJanelaModal(FXMLLoader loader, String titulo) throws IOException {
        Scene cena = new Scene(loader.load());
        cena.getStylesheets().addAll(tabela.getScene().getStylesheets()); // mesmo estilo.css

        Stage janela = new Stage();
        janela.setTitle(titulo);
        janela.setScene(cena);
        janela.initOwner(tabela.getScene().getWindow());
        janela.initModality(Modality.WINDOW_MODAL);
        janela.setResizable(false);
        return janela;
    }

    private void removerSelecionada() {
        Transacao selecionada = tabela.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Remover transacao");
        confirmacao.setHeaderText("Remover \"" + selecionada.getDescricao() + "\"?");
        confirmacao.setContentText("Esta acao nao pode ser desfeita.");

        // showAndWait() pausa aqui ate o usuario responder
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            dao.excluir(selecionada.getId());
            transacoes.remove(selecionada);
            atualizarSaldo();
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao remover transacao " + selecionada.getId(), e);
            mostrarErro("Nao foi possivel remover a transacao.", e);
        }
    }

    private void mostrarErro(String mensagem, Exception e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Erro");
        alerta.setHeaderText(mensagem);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
    }
}
