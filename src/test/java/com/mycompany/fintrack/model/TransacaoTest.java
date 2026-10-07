package com.mycompany.fintrack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransacaoTest {

    private static final LocalDate DATA = LocalDate.of(2026, 10, 1);

    @Test
    void criaTransacaoValida() {
        Transacao transacao = new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA);

        assertEquals("Salario", transacao.getDescricao());
        assertEquals(3000, transacao.getValor(), 0.001);
        assertEquals(TipoTransacao.RECEITA, transacao.getTipo());
        assertEquals(DATA, transacao.getData());
    }

    @Test
    void removeEspacosDaDescricao() {
        Transacao transacao = new Transacao("  Mercado  ", 50, TipoTransacao.DESPESA, DATA);

        assertEquals("Mercado", transacao.getDescricao());
    }

    @Test
    void valorZeroLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao("Mercado", 0, TipoTransacao.DESPESA, DATA));
    }

    @Test
    void valorNegativoLancaExcecao() {
        IllegalArgumentException erro = assertThrows(IllegalArgumentException.class,
                () -> new Transacao("Mercado", -5, TipoTransacao.DESPESA, DATA));

        assertEquals("O valor deve ser maior que zero.", erro.getMessage());
    }

    @Test
    void descricaoVaziaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao("   ", 10, TipoTransacao.DESPESA, DATA));
    }

    @Test
    void tipoNuloLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao("Mercado", 10, null, DATA));
    }

    @Test
    void dataNulaLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Transacao("Mercado", 10, TipoTransacao.DESPESA, null));
    }

    @Test
    void setterTambemValida() {
        Transacao transacao = new Transacao("Mercado", 10, TipoTransacao.DESPESA, DATA);

        assertThrows(IllegalArgumentException.class, () -> transacao.setValor(-1));
        assertEquals(10, transacao.getValor(), 0.001); // valor antigo foi mantido
    }

    @Test
    void transacaoMensalHerdaValidacao() {
        assertThrows(IllegalArgumentException.class,
                () -> new TransacaoMensal("Aluguel", -1, TipoTransacao.DESPESA, DATA, java.time.Month.OCTOBER));
    }
}
