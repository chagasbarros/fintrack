package com.mycompany.fintrack.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Centraliza o acesso ao banco: se um dia trocar de SQLite para MySQL, so esta classe muda
public class Conexao {

    // Arquivo criado na pasta onde o programa roda (no NetBeans, a pasta do projeto)
    private static final String URL = "jdbc:sqlite:fintrack.db";

    private static final String SQL_CRIAR_TABELA = """
            CREATE TABLE IF NOT EXISTS transacoes (
                id        INTEGER PRIMARY KEY AUTOINCREMENT,
                descricao TEXT    NOT NULL,
                valor     REAL    NOT NULL CHECK (valor > 0),
                tipo      TEXT    NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
                data      TEXT    NOT NULL
            )
            """;

    private Conexao() {
        // classe utilitaria: nao deve ser instanciada
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Recebe a conexao para funcionar tanto com o arquivo quanto com o banco em memoria dos testes
    public static void criarTabela(Connection conexao) throws SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(SQL_CRIAR_TABELA);
        }
    }
}
