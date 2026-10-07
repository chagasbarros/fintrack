package com.mycompany.fintrack.controller;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import com.mycompany.fintrack.repository.RepositorioGenerico;
import java.util.Collection;
import java.util.List;

public class FinTracker {

    private final RepositorioGenerico<Transacao> repositorio = new RepositorioGenerico<>();

    public void adicionarTransacao(Transacao transacao){
        repositorio.adicionar(transacao);
    }

    // Aceita List<Transacao>, List<TransacaoMensal>, Set<TransacaoMensal>...
    public void adicionarTransacoes(Collection<? extends Transacao> transacoes){
        repositorio.adicionarTodos(transacoes);
    }

    // Devolve uma visão somente leitura: quem chama não consegue alterar a lista interna
    public List<Transacao> listarTransacoes(){
        return repositorio.listar();
    }

    public List<Transacao> filtrarPorTipo(TipoTransacao tipo){
        return repositorio.filtrar(transacao -> transacao.getTipo() == tipo);
    }

    public double calcularSaldoTotal(){
        return calcularSaldo(repositorio.listar());
    }

    // indice começa em 1, como é mostrado ao usuário
    public Transacao removerTransacao(int indice){
        if(indice < 1 || indice > repositorio.tamanho()){
            throw new IndexOutOfBoundsException("Indice invalido: " + indice);
        }
        return repositorio.remover(indice - 1);
    }

    // ? extends Transacao: funciona com qualquer lista de Transacao ou de subclasses (somente leitura)
    public static double calcularSaldo(List<? extends Transacao> transacoes){
        return somarPorTipo(transacoes, TipoTransacao.RECEITA)
                - somarPorTipo(transacoes, TipoTransacao.DESPESA);
    }

    public static double somarPorTipo(List<? extends Transacao> transacoes, TipoTransacao tipo){
        double total = 0;
        for(Transacao transacao : transacoes) {
            if(transacao.getTipo() == tipo){
                total += transacao.getValor();
            }
        }
        return total;
    }
}
