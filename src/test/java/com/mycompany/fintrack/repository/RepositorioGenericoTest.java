package com.mycompany.fintrack.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.model.TransacaoMensal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RepositorioGenericoTest {

    private RepositorioGenerico<Transacao> repositorio;
    private List<TransacaoMensal> mensais;

    // Roda antes de CADA teste: todo teste comeca com um repositorio novo e vazio
    @BeforeEach
    void setUp() {
        repositorio = new RepositorioGenerico<>();
        LocalDate data = LocalDate.of(2026, 10, 1);
        mensais = List.of(
                new TransacaoMensal("Salario", 3000, TipoTransacao.RECEITA, data, Month.OCTOBER),
                new TransacaoMensal("Aluguel", 1200, TipoTransacao.DESPESA, data, Month.OCTOBER));
    }

    @Test
    void comecaVazio() {
        assertEquals(0, repositorio.tamanho());
        assertTrue(repositorio.listar().isEmpty());
    }

    @Test
    void funcionaComQualquerTipo() {
        RepositorioGenerico<String> nomes = new RepositorioGenerico<>();
        nomes.adicionar("Ana");

        assertEquals("Ana", nomes.listar().get(0));
    }

    @Test
    void adicionaERemove() {
        repositorio.adicionar(mensais.get(0));
        repositorio.adicionar(mensais.get(1));

        Transacao removida = repositorio.remover(0);

        assertEquals("Salario", removida.getDescricao());
        assertEquals(1, repositorio.tamanho());
        assertEquals("Aluguel", repositorio.listar().get(0).getDescricao());
    }

    @Test
    void removerIndiceInvalidoLancaExcecao() {
        assertThrows(IndexOutOfBoundsException.class, () -> repositorio.remover(0));
        assertThrows(IndexOutOfBoundsException.class, () -> repositorio.remover(-1));
    }

    @Test
    void listarEhSomenteLeitura() {
        List<Transacao> lista = repositorio.listar();

        assertThrows(UnsupportedOperationException.class, () -> lista.add(mensais.get(0)));
    }

    @Test
    void adicionarTodosAceitaSubtipos() { // ? extends T
        repositorio.adicionarTodos(mensais);

        assertEquals(2, repositorio.tamanho());
    }

    @Test
    void copiarParaAceitaSupertipos() { // ? super T
        repositorio.adicionarTodos(mensais);
        List<Object> destino = new ArrayList<>();

        repositorio.copiarPara(destino);

        assertEquals(2, destino.size());
    }

    @Test
    void filtrarAceitaCriterioDoSupertipo() { // Predicate<? super T>
        RepositorioGenerico<TransacaoMensal> repositorioMensal = RepositorioGenerico.de(mensais);
        Predicate<Transacao> ehReceita = t -> t.getTipo() == TipoTransacao.RECEITA;

        List<TransacaoMensal> receitas = repositorioMensal.filtrar(ehReceita);

        assertEquals(1, receitas.size());
        assertEquals("Salario", receitas.get(0).getDescricao());
    }

    @Test
    void contarAceitaQualquerColecao() { // ?
        assertEquals(3, RepositorioGenerico.contar(Set.of(1, 2, 3)));
        assertEquals(2, RepositorioGenerico.contar(mensais));
    }
}
