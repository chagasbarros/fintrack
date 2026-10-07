package com.mycompany.fintrack.dao;

import com.mycompany.fintrack.model.TipoTransacao;
import com.mycompany.fintrack.model.Transacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

// DAO (Data Access Object): unica classe que conhece o SQL da tabela transacoes
public class TransacaoDAO {

    private static final Logger LOG = Logger.getLogger(TransacaoDAO.class.getName());

    private final Connection conexao;

    // A conexao vem de fora: o app passa a do arquivo e os testes passam uma em memoria
    public TransacaoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public void inserir(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes (descricao, valor, tipo, data) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, transacao.getDescricao());
            ps.setDouble(2, transacao.getValor());
            ps.setString(3, transacao.getTipo().name());
            ps.setString(4, transacao.getData().toString()); // ISO: 2026-10-07
            ps.executeUpdate();

            // id gerado pelo AUTOINCREMENT volta para o objeto
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    transacao.setId(chaves.getInt(1));
                }
            }
        }
    }

    public List<Transacao> listarTodas() throws SQLException {
        String sql = "SELECT id, descricao, valor, tipo, data FROM transacoes ORDER BY data, id";
        List<Transacao> transacoes = new ArrayList<>();
        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                transacoes.add(mapear(rs));
            }
        }
        return transacoes;
    }

    // Optional deixa explicito que a transacao pode nao existir (em vez de devolver null)
    public Optional<Transacao> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, descricao, valor, tipo, data FROM transacoes WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // Retorna false se nenhuma linha tinha esse id
    public boolean atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE transacoes SET descricao = ?, valor = ?, tipo = ?, data = ? WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setString(1, transacao.getDescricao());
            ps.setDouble(2, transacao.getValor());
            ps.setString(3, transacao.getTipo().name());
            ps.setString(4, transacao.getData().toString());
            ps.setInt(5, transacao.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";
        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Transacao de banco: ou todas sao gravadas, ou nenhuma
    public void inserirTodas(List<? extends Transacao> transacoes) throws SQLException {
        conexao.setAutoCommit(false);
        try {
            for (Transacao transacao : transacoes) {
                inserir(transacao);
            }
            conexao.commit();
        } catch (SQLException e) {
            conexao.rollback();
            LOG.log(Level.SEVERE, "Falha ao inserir lote de transacoes; rollback executado", e);
            throw e;
        } finally {
            conexao.setAutoCommit(true);
        }
    }

    // Converte a linha atual do ResultSet em um objeto Transacao
    private Transacao mapear(ResultSet rs) throws SQLException {
        Transacao transacao = new Transacao(
                rs.getString("descricao"),
                rs.getDouble("valor"),
                TipoTransacao.valueOf(rs.getString("tipo")),
                LocalDate.parse(rs.getString("data")));
        transacao.setId(rs.getInt("id"));
        return transacao;
    }
}
