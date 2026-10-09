package com.mycompany.fintrack.controller;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.repository.RepositorioGenerico;
import com.mycompany.fintrack.utils.Formatador;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// Controller de relatorio.fxml: so le as transacoes recebidas, nao acessa o banco
public class RelatorioController {

    private static final DateTimeFormatter FORMATO_MES =
            DateTimeFormatter.ofPattern("MMMM 'de' yyyy", Locale.of("pt", "BR"));

    @FXML private CheckBox chkMesAtual;
    @FXML private Label lblPeriodo;
    @FXML private Label lblQuantidade;
    @FXML private Label lblReceitas;
    @FXML private Label lblDespesas;
    @FXML private Label lblSaldo;
    @FXML private Button btnFechar;

    private RepositorioGenerico<Transacao> repositorio = new RepositorioGenerico<>();

    @FXML
    private void initialize() {
        // Marcar ou desmarcar o CheckBox recalcula os totais
        chkMesAtual.setOnAction(evento -> atualizar());
        btnFechar.setOnAction(evento -> ((Stage) btnFechar.getScene().getWindow()).close());
    }

    // Aceita List<? extends Transacao>: funciona tambem com uma lista de TransacaoMensal
    public void setTransacoes(List<? extends Transacao> transacoes) {
        repositorio = RepositorioGenerico.de(transacoes);
        atualizar();
    }

    private void atualizar() {
        List<Transacao> selecionadas;
        if (chkMesAtual.isSelected()) {
            YearMonth mesAtual = YearMonth.now();
            // filtrar recebe um Predicate: a regra de "quais transacoes entram"
            selecionadas = repositorio.filtrar(t -> YearMonth.from(t.getData()).equals(mesAtual));
            lblPeriodo.setText("Periodo: " + mesAtual.format(FORMATO_MES));
        } else {
            selecionadas = repositorio.listar();
            lblPeriodo.setText("Periodo: todas as transacoes");
        }

        double receitas = FinTracker.somarPorTipo(selecionadas, TipoTransacao.RECEITA);
        double despesas = FinTracker.somarPorTipo(selecionadas, TipoTransacao.DESPESA);
        double saldo = FinTracker.calcularSaldo(selecionadas);

        lblQuantidade.setText(String.valueOf(selecionadas.size()));
        lblReceitas.setText(Formatador.formatarMoeda(receitas));
        lblDespesas.setText(Formatador.formatarMoeda(despesas));
        lblSaldo.setText(Formatador.formatarMoeda(saldo));
        lblSaldo.getStyleClass().removeAll("saldo-positivo", "saldo-negativo");
        lblSaldo.getStyleClass().add(saldo >= 0 ? "saldo-positivo" : "saldo-negativo");
    }
}
