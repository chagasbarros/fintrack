package com.mycompany.fintrack.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.model.TransacaoMensal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FinTrackerTest {

    // double nao representa todos os decimais exatamente, por isso comparamos com uma margem
    private static final double DELTA = 0.001;
    private static final LocalDate DATA = LocalDate.of(2026, 10, 1);

    private FinTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new FinTracker();
    }

    @Test
    void saldoComecaZerado() {
        assertEquals(0, tracker.calcularSaldoTotal(), DELTA);
    }

    @Test
    void saldoSomaReceitasESubtraiDespesas() {
        tracker.adicionarTransacao(new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA));
        tracker.adicionarTransacao(new Transacao("Aluguel", 1200.50, TipoTransacao.DESPESA, DATA));

        assertEquals(1799.50, tracker.calcularSaldoTotal(), DELTA);
    }

    @Test
    void saldoPodeSerNegativo() {
        tracker.adicionarTransacao(new Transacao("Aluguel", 1200, TipoTransacao.DESPESA, DATA));

        assertEquals(-1200, tracker.calcularSaldoTotal(), DELTA);
    }

    @Test
    void somaPorTipo() {
        List<Transacao> lista = List.of(
                new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA),
                new Transacao("Freela", 500, TipoTransacao.RECEITA, DATA),
                new Transacao("Mercado", 300, TipoTransacao.DESPESA, DATA));

        assertEquals(3500, FinTracker.somarPorTipo(lista, TipoTransacao.RECEITA), DELTA);
        assertEquals(300, FinTracker.somarPorTipo(lista, TipoTransacao.DESPESA), DELTA);
    }

    @Test
    void calcularSaldoAceitaListaDeSubclasse() { // List<? extends Transacao>
        List<TransacaoMensal> mensais = List.of(
                new TransacaoMensal("Salario", 3000, TipoTransacao.RECEITA, DATA, Month.OCTOBER),
                new TransacaoMensal("Aluguel", 1000, TipoTransacao.DESPESA, DATA, Month.OCTOBER));

        assertEquals(2000, FinTracker.calcularSaldo(mensais), DELTA);
    }

    @Test
    void filtraPorTipo() {
        tracker.adicionarTransacao(new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA));
        tracker.adicionarTransacao(new Transacao("Aluguel", 1200, TipoTransacao.DESPESA, DATA));
        tracker.adicionarTransacao(new Transacao("Mercado", 300, TipoTransacao.DESPESA, DATA));

        assertEquals(2, tracker.filtrarPorTipo(TipoTransacao.DESPESA).size());
        assertEquals(1, tracker.filtrarPorTipo(TipoTransacao.RECEITA).size());
    }

    @Test
    void removeComIndiceDoUsuario() { // indice comeca em 1
        tracker.adicionarTransacao(new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA));
        tracker.adicionarTransacao(new Transacao("Aluguel", 1200, TipoTransacao.DESPESA, DATA));

        Transacao removida = tracker.removerTransacao(1);

        assertEquals("Salario", removida.getDescricao());
        assertEquals(1, tracker.listarTransacoes().size());
        assertEquals(-1200, tracker.calcularSaldoTotal(), DELTA);
    }

    @Test
    void removerIndiceInvalidoLancaExcecao() {
        tracker.adicionarTransacao(new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA));

        assertThrows(IndexOutOfBoundsException.class, () -> tracker.removerTransacao(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tracker.removerTransacao(2));
        assertEquals(1, tracker.listarTransacoes().size()); // nada foi removido
    }
}
