package com.agenda.dao;

import java.sql.*;


public class ConexaoDB {

    // ── Configurações ─────────────────────────────────────────────────────────
    private static final String HOST    = "localhost";
    private static final String PORTA   = "5432";
    private static final String BANCO   = "agenda_contatos";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "sua_senha_aqui" ; // ← ALTERE AQUI

    // URL do nosso banco (usado após ele ser criado)
    private static final String URL = "jdbc:postgresql://" + HOST + ":" + PORTA + "/" + BANCO;

    // URL do banco padrão do PostgreSQL (usado para CRIAR o nosso banco)
    private static final String URL_POSTGRES = "jdbc:postgresql://" + HOST + ":" + PORTA + "/postgres";

    private static Connection conexao  = null;
    private static boolean   jaConfigurou = false; // evita rodar o setup mais de uma vez


    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {

                if (!jaConfigurou) {
                    configurarBanco(); // cria banco + tabela + dados de exemplo
                    jaConfigurou = true;
                }

                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                System.out.println(" Conectado ao banco '" + BANCO + "'!");
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao conectar: " + e.getMessage());
        }
        return conexao;
    }

    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("🔌 Conexão encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }



    private static void configurarBanco() {
        System.out.println("🔧 Verificando configuração do banco de dados...");
        criarBancoDeDados();
        criarTabela();
        inserirExemplos();
    }


     /// Cria o banco "agenda_contatos" se ele ainda não existir.


    private static void criarBancoDeDados() {
        try (Connection conn = DriverManager.getConnection(URL_POSTGRES, USUARIO, SENHA);
             Statement  stmt = conn.createStatement()) {

            // CREATE DATABASE não pode rodar dentro de uma transação
            conn.setAutoCommit(true);
            stmt.executeUpdate("CREATE DATABASE " + BANCO);
            System.out.println("🗄️  Banco de dados '" + BANCO + "' criado com sucesso!");

        } catch (SQLException e) {
            // Código 42P04 = banco já existe → tudo certo, pode continuar
            if ("42P04".equals(e.getSQLState())) {
                System.out.println("ℹ️  Banco '" + BANCO + "' já existe. Continuando...");
            } else {
                System.err.println("❌ Erro ao criar banco: " + e.getMessage());
            }
        }
    }


     /// Cria a tabela "contatos" se ela não existir.

    private static void criarTabela() {
        String sql = """
                CREATE TABLE IF NOT EXISTS contatos (
                    id         SERIAL        PRIMARY KEY,
                    nome       VARCHAR(100)  NOT NULL,
                    telefone   VARCHAR(20),
                    email      VARCHAR(100),
                    endereco   VARCHAR(200),
                    criado_em  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
                )
                """;

        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
             Statement  stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("📋 Tabela 'contatos' verificada/criada!");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar tabela: " + e.getMessage());
        }
    }


     /// Insere 5 contatos de exemplo — (apenas se a tabela estiver vazia).

    private static void inserirExemplos() {
        String contar  = "SELECT COUNT(*) FROM contatos";
        String inserir = """
                INSERT INTO contatos (nome, telefone, email, endereco) VALUES
                    ('Maria Silva',      '(21) 99999-1234', 'maria@email.com',   'Rua das Flores, 123 - Rio de Janeiro/RJ'),
                    ('João Santos',      '(21) 98888-5678', 'joao@email.com',    'Av. Brasil, 456 - Volta Redonda/RJ'),
                    ('Ana Costa',        '(24) 97777-9012', 'ana@email.com',     'Rua Central, 789 - Barra Mansa/RJ'),
                    ('Carlos Souza',     '(21) 96666-3456', 'carlos@email.com',  'Av. Atlântica, 100 - Niterói/RJ'),
                    ('Beatriz Ferreira', '(24) 95555-7890', 'beatriz@email.com', 'Rua das Palmeiras, 55 - Resende/RJ')
                """;

        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
             Statement  stmt = conn.createStatement()) {

            // Primeiro verifica se já tem dados
            try (ResultSet rs = stmt.executeQuery(contar)) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    System.out.println("ℹ️  Tabela já possui dados. Pulando exemplos.");
                    return;
                }
            }

            // Tabela vazia → insere os exemplos
            stmt.executeUpdate(inserir);
            System.out.println("👥 Contatos de exemplo inseridos!");

        } catch (SQLException e) {
            System.err.println("Erro ao inserir exemplos: " + e.getMessage());
        }
    }
}
