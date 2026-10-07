package com.mycompany.fintrack.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

// Repositorio em memoria que funciona para qualquer tipo de registro (T)
public class RepositorioGenerico<T> {

    private final List<T> itens = new ArrayList<>();

    public void adicionar(T item) {
        itens.add(item);
    }

    // ? extends T: a colecao recebida PRODUZ itens do tipo T (ou de subtipos), so lemos dela
    public void adicionarTodos(Collection<? extends T> novos) {
        itens.addAll(novos);
    }

    // ? super T: a colecao recebida CONSOME itens do tipo T, so escrevemos nela
    public void copiarPara(Collection<? super T> destino) {
        destino.addAll(itens);
    }

    // indice comeca em 0
    public T remover(int indice) {
        if (indice < 0 || indice >= itens.size()) {
            throw new IndexOutOfBoundsException("Indice invalido: " + indice);
        }
        return itens.remove(indice);
    }

    // Visao somente leitura: quem chama nao consegue alterar a lista interna
    public List<T> listar() {
        return Collections.unmodifiableList(itens);
    }

    // ? super T: um criterio para Transacao tambem serve para filtrar TransacaoMensal
    public List<T> filtrar(Predicate<? super T> criterio) {
        return itens.stream().filter(criterio).toList();
    }

    public int tamanho() {
        return itens.size();
    }

    // Metodo generico: <E> e declarado no proprio metodo, e o tipo e inferido pelo argumento
    public static <E> RepositorioGenerico<E> de(Collection<? extends E> itensIniciais) {
        RepositorioGenerico<E> repositorio = new RepositorioGenerico<>();
        repositorio.adicionarTodos(itensIniciais);
        return repositorio;
    }

    // ?: curinga livre, quando o tipo dos elementos nao importa
    public static int contar(Collection<?> qualquerColecao) {
        return qualquerColecao.size();
    }
}
