package com.mycompany.fintrack.controller;

import com.mycompany.fintrack.model.Transacao;
import java.util.ArrayList;

public class FinTracker {
    
    private ArrayList<Transacao> transacoes = new ArrayList<>();
    
    public void adicionarTransacao(Transacao transacao){
        transacoes.add(transacao);
    }
    
    public void listarTransacoes(){
        
        int indice = 1;
        
        if(transacoes.isEmpty()){
            System.out.println("Nenhuma transação cadastrada.");
            return;
        }
        
        for(Transacao transacao : transacoes){
            System.out.println(indice + " - " + transacao);
            indice++;
        }
    }
    
    public double calcularSaldoTotal(){
        double saldo = 0;
        
        for(Transacao transacao : transacoes) {
            if(transacao.getTipo().equals("RECEITA")){
                saldo += transacao.getValor();
            } else {
                saldo -= transacao.getValor();
            }
        }
        return saldo;
    }
    
    public void removerTransacao(int indice){
        
        if(indice >= 1 && indice <= transacoes.size()){
            transacoes.remove(indice - 1);
        }else{
            System.out.println("Índice inválido");
        }
    }
}









