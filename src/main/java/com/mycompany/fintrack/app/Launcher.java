package com.mycompany.fintrack.app;

// Classe que NAO estende Application, usada como mainClass do projeto.
// Se o Java iniciar direto por uma subclasse de Application sem o JavaFX configurado como modulo,
// ele aborta com "JavaFX runtime components are missing". Passando por aqui, o JavaFX e
// carregado normalmente pelo classpath (e o Run Project do NetBeans funciona).
public class Launcher {

    public static void main(String[] args) {
        FinApp.main(args);
    }
}
