package com.mycompany.fintrack.model;
import java.time.LocalDate;


public class Transacao {
    private int id;
    private String descricao;
    private TipoTransacao tipo;
    private double valor;
    private LocalDate data;
    
    public Transacao(String descricao, double valor, TipoTransacao tipo, LocalDate data){
        setDescricao(descricao);
        setValor(valor);
        setTipo(tipo);
        setData(data);
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()){
            throw new IllegalArgumentException("A descrição e obrigatoria");
        }
        this.descricao = descricao.trim();
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if(valor <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero");
        }
        this.valor = valor;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        if(tipo == null){
            throw new IllegalArgumentException("O tipo e obrigatorio");
        }
    }
    
    public LocalDate getData(){
        return data;
    }
    
    public void setData(LocalDate data){
        if( data == null){
            throw new IllegalArgumentException("A data e obrigatoria");
        }
        this.data = data;
    }
    
    @Override
    public String toString(){
        return "Descricao: " + descricao +
                "\n    Valor: " + valor + 
                "\n    Tipo: " + tipo;
    }
    
}
