package com.mycompany.fintrack.model;
import java.time.Month;
import java.time.LocalDate;

public class TransacaoMensal extends Transacao{
    private Month mes;
    public TransacaoMensal(String descricao, double valor, TipoTransacao tipo, LocalDate data, Month mes){
        super(descricao, valor, tipo, data);
        this.mes = mes;
    }

    public Month getMes() {
        return mes;
    }

    public void setMes(Month mes) {
        this.mes = mes;
    }
    @Override
    public String toString(){
        return super.toString() + "\n mês: " + mes;
    }
}
