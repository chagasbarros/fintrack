package com.mycompany.fintrack.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.model.TransacaoMensal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransacaoDAOTest {

    private static final double DELTA = 0.001;
    private static final LocalDate DATA = LocalDate.of(2026, 10, 1);

    private Connection conexao;
    private TransacaoDAO dao;

    // Cada teste ganha um banco novo e vazio, que existe so na memoria
    @BeforeEach
    void setUp() throws SQLException {
        conexao = DriverManager.getConnection("jdbc:sqlite::memory:");
        Conexao.criarTabela(conexao);
        dao = new TransacaoDAO(conexao);
    }

    // Fechar a conexao apaga o banco em memoria e libera o recurso
    @AfterEach
    void tearDown() throws SQLException {
        conexao.close();
    }

    @Test
    void inserirGeraId() throws SQLException {
        Transacao salario = new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA);

        dao.inserir(salario);

        assertTrue(salario.getId() > 0);
    }

    @Test
    void inserirELerDeVolta() throws SQLException {
        dao.inserir(new Transacao("Aluguel", 1200.50, TipoTransacao.DESPESA, DATA));

        List<Transacao> todas = dao.listarTodas();

        assertEquals(1, todas.size());
        Transacao lida = todas.get(0);
        assertEquals("Aluguel", lida.getDescricao());
        assertEquals(1200.50, lida.getValor(), DELTA);
        assertEquals(TipoTransacao.DESPESA, lida.getTipo());
        assertEquals(DATA, lida.getData());
    }

    @Test
    void listarOrdenaPorData() throws SQLException {
        dao.inserir(new Transacao("Depois", 10, TipoTransacao.DESPESA, DATA.plusDays(5)));
        dao.inserir(new Transacao("Antes", 10, TipoTransacao.DESPESA, DATA));

        List<Transacao> todas = dao.listarTodas();

        assertEquals("Antes", todas.get(0).getDescricao());
        assertEquals("Depois", todas.get(1).getDescricao());
    }

    @Test
    void buscarPorIdExistente() throws SQLException {
        Transacao salario = new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA);
        dao.inserir(salario);

        Transacao encontrada = dao.buscarPorId(salario.getId()).orElseThrow();

        assertEquals("Salario", encontrada.getDescricao());
    }

    @Test
    void buscarPorIdInexistenteRetornaVazio() throws SQLException {
        assertTrue(dao.buscarPorId(999).isEmpty());
    }

    @Test
    void atualizarAlteraOsDados() throws SQLException {
        Transacao mercado = new Transacao("Mercado", 100, TipoTransacao.DESPESA, DATA);
        dao.inserir(mercado);

        mercado.setValor(250);
        mercado.setDescricao("Mercado do mes");
        boolean atualizou = dao.atualizar(mercado);

        assertTrue(atualizou);
        Transacao lida = dao.buscarPorId(mercado.getId()).orElseThrow();
        assertEquals("Mercado do mes", lida.getDescricao());
        assertEquals(250, lida.getValor(), DELTA);
    }

    @Test
    void excluirRemoveDoBanco() throws SQLException {
        Transacao mercado = new Transacao("Mercado", 100, TipoTransacao.DESPESA, DATA);
        dao.inserir(mercado);

        assertTrue(dao.excluir(mercado.getId()));
        assertTrue(dao.listarTodas().isEmpty());
    }

    @Test
    void excluirIdInexistenteRetornaFalse() throws SQLException {
        assertFalse(dao.excluir(999));
    }

    @Test
    void inserirTodasAceitaSubclasses() throws SQLException { // List<? extends Transacao>
        List<TransacaoMensal> mensais = List.of(
                new TransacaoMensal("Salario", 3000, TipoTransacao.RECEITA, DATA, Month.OCTOBER),
                new TransacaoMensal("Aluguel", 1200, TipoTransacao.DESPESA, DATA, Month.OCTOBER));

        dao.inserirTodas(mensais);

        assertEquals(2, dao.listarTodas().size());
    }

    @Test
    void inserirTodasFazRollbackSeUmaFalhar() throws SQLException {
        // Simula um dado que o banco rejeita: o CHECK (valor > 0) da tabela barra o valor negativo
        Transacao invalida = new Transacao("Invalida", 10, TipoTransacao.DESPESA, DATA) {
            @Override
            public double getValor() {
                return -1;
            }
        };
        List<Transacao> lote = List.of(
                new Transacao("Salario", 3000, TipoTransacao.RECEITA, DATA),
                invalida);

        assertThrows(SQLException.class, () -> dao.inserirTodas(lote));

        // O Salario chegou a ser inserido, mas o rollback desfez: o banco continua vazio
        assertTrue(dao.listarTodas().isEmpty());
    }

    @Test
    void preparedStatementImpedeSqlInjection() throws SQLException {
        String textoMalicioso = "x'); DROP TABLE transacoes; --";

        dao.inserir(new Transacao(textoMalicioso, 10, TipoTransacao.DESPESA, DATA));

        // A tabela continua existindo e o texto foi gravado literalmente, como um dado comum
        List<Transacao> todas = dao.listarTodas();
        assertEquals(1, todas.size());
        assertEquals(textoMalicioso, todas.get(0).getDescricao());
    }
}
