package com.mycompany.fintrack.model;

public class Transacao {
    private String descricao;
    private double valor;
    private String tipo;
    
    public Transacao(String descricao, double valor, String tipo){
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    @Override
    public String toString(){
        return "Descricao: " + descricao +
                "\n    Valor: " + valor + 
                "\n    Tipo: " + tipo;
    }
    
}
