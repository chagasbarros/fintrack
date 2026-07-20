package com.mycompany.fintrack.model;
import java.time.Month;

public class TransacaoMensal extends Transacao{
    private Month mes;
    public TransacaoMensal(String descricao, double valor, String tipo, Month mes){
        super(descricao, valor, tipo);
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
        return super.toString() + " mês: " + mes;
    }
}
