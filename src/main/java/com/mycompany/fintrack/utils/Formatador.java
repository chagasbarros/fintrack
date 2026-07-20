package com.mycompany.fintrack.utils;
import java.text.NumberFormat;
import java.util.Locale;

public class Formatador {
    public static String formatarMoeda(double valor){
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formato.format(valor);
    }
}
