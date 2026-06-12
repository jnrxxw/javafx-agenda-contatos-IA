package com.agenda.dao;

import com.agenda.model.Contato;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ContatoDAO {

    private final Connection conn;

    public ContatoDAO() {
        // Obtém a conexão que foi criada em ConexaoDB
        this.conn = ConexaoDB.getConexao();
    }






      /// Insere um novo contato na tabela.

    public boolean salvar(Contato contato) {
        String sql = """
                INSERT INTO contatos (nome, telefone, email, endereco)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getTelefone());
            stmt.setString(3, contato.getEmail());
            stmt.setString(4, contato.getEndereco());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar contato: " + e.getMessage());
            return false;
        }
    }


     /// Retorna todos os contatos em ordem alfabética.

    public List<Contato> listarTodos() {
        List<Contato> lista = new ArrayList<>();
        String sql = "SELECT * FROM contatos ORDER BY nome";

        try (Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(extrairContato(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar contatos: " + e.getMessage());
        }
        return lista;
    }


    public List<Contato> buscarPorNome(String nome) {
        List<Contato> lista = new ArrayList<>();
        String sql = "SELECT * FROM contatos WHERE LOWER(nome) LIKE LOWER(?) ORDER BY nome";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%"); // % = qualquer coisa antes/depois
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(extrairContato(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar contatos: " + e.getMessage());
        }
        return lista;
    }



    /**
     * Atualiza os dados de um contato já existente (identificado pelo ID).
     *
     * @return true se atualizou com sucesso, false se deu erro
     */
    public boolean atualizar(Contato contato) {
        String sql = """
                UPDATE contatos
                   SET nome = ?, telefone = ?, email = ?, endereco = ?
                 WHERE id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, contato.getNome());
            stmt.setString(2, contato.getTelefone());
            stmt.setString(3, contato.getEmail());
            stmt.setString(4, contato.getEndereco());
            stmt.setInt(5, contato.getId());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar contato: " + e.getMessage());
            return false;
        }
    }


    /**
     * Remove um contato da tabela pelo seu ID.
     *
     * @return true se excluiu com sucesso, false se deu erro
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM contatos WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir contato: " + e.getMessage());
            return false;
        }
    }


    /**
     * Lê uma linha do ResultSet e cria um objeto Contato com os dados.
     * Evita repetir esse código em cada método de busca.
     */
    private Contato extrairContato(ResultSet rs) throws SQLException {
        return new Contato(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("telefone"),
            rs.getString("email"),
            rs.getString("endereco")
        );
    }
}
