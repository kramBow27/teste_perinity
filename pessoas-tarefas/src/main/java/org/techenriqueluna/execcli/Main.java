package org.techenriqueluna.execcli;

import org.postgresql.util.PSQLException;

import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_SUPERUSER = "postgres";
    private static final String DEFAULT_DB = "pessoas_tarefas";
    private static String superUser = DEFAULT_SUPERUSER;
    private static String superPassword;
    private static final String dbName = DEFAULT_DB;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (!isPsqlInstalled()) {
            System.out.println("PostgreSQL não detectado. Deseja instalar via winget? (s/n)");
            String resp = scanner.nextLine();
            if (resp.equalsIgnoreCase("s")) {
                installPostgres();
                System.out.println("Instalação concluída. Pressione Enter para continuar...");
                scanner.nextLine();
            } else {
                System.err.println("PostgreSQL é necessário. Saindo.");
                return;
            }
        }

        // Apenas superuser e senha
        System.out.printf("Usuário superuser (default %s): ", DEFAULT_SUPERUSER);
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) {
            superUser = input;
        }

        System.out.print("Senha do usuário " + superUser + ": ");
        superPassword = scanner.nextLine();

        try {
            createDatabase();
            createSchemaAndSeed();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("\n=== MENU ===");
            System.out.println("1 - Listar Pessoas");
            System.out.println("2 - Listar Tarefas Pendentes");
            System.out.println("3 - Criar Pessoa de Teste");
            System.out.println("4 - Criar Tarefa de Teste");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opt = scanner.nextLine();
            switch (opt) {
                case "1":
                    listPessoas();
                    break;
                case "2":
                    listPendentes();
                    break;
                case "3":
                    seedPessoaTeste();
                    break;
                case "4":
                    seedTarefaTeste();
                    break;
                case "0":
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static boolean isPsqlInstalled() {
        try {
            Process p = new ProcessBuilder("psql", "--version").start();
            return p.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static void installPostgres() {
        try {
            System.out.println("Executando winget install PostgreSQL.PostgreSQL...");
            Process p = new ProcessBuilder(
                    "winget", "install", "--id", "PostgreSQL.PostgreSQL", "-e"
            ).inheritIO().start();
            p.waitFor();
        } catch (Exception e) {
            System.err.println("Falha ao instalar: " + e.getMessage());
        }
    }

    private static Connection getConnection(String database) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/" + database;
        return DriverManager.getConnection(url, superUser, superPassword);
    }

    private static void createDatabase() throws SQLException {
        System.out.println("Criando banco '" + dbName + "' se não existir...");
        try (Connection conn = getConnection("postgres");
             Statement st = conn.createStatement()) {
            try {
                st.executeUpdate("CREATE DATABASE \"" + dbName + "\";");
                System.out.println("Banco criado com sucesso.");
            } catch (PSQLException e) {
                // SQLState 42P04 = “database already exists”
                if ("42P04".equals(e.getSQLState())) {
                    System.out.println("Banco '" + dbName + "' já existe. Continuando...");
                } else {
                    throw e;
                }
            }
        }
    }

    private static void createSchemaAndSeed() throws SQLException {
        try (Connection conn = getConnection(dbName);
             Statement st = conn.createStatement()) {
            // criar tabelas (nomes fixos Pessoa e Tarefa)
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Pessoa (" +
                            "id SERIAL PRIMARY KEY, nome TEXT NOT NULL, departamento TEXT NOT NULL" +
                            ");"
            );
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Tarefa (" +
                            "id SERIAL PRIMARY KEY, titulo TEXT NOT NULL, descricao TEXT, " +
                            "duracao INT NOT NULL, finalizado BOOLEAN NOT NULL DEFAULT FALSE, " +
                            "prazo DATE NOT NULL, departamento TEXT NOT NULL, " +
                            "pessoa_id INT REFERENCES Pessoa(id)" +
                            ");"
            );
            // inserir dados de teste
            st.executeUpdate("INSERT INTO Pessoa(nome, departamento) VALUES ('Teste1','TI');");
            st.executeUpdate("INSERT INTO Pessoa(nome, departamento) VALUES ('Teste2','RH');");
            st.executeUpdate(
                    "INSERT INTO Tarefa(titulo,descricao,duracao,finalizado,prazo,departamento,pessoa_id) " +
                            "VALUES ('T1','Desc1',3,false,'2025-12-31','TI',1);"
            );
            System.out.println("Schema e dados de teste criados.");
        }
    }

    private static void listPessoas() {
        try (Connection conn = getConnection(dbName);
             ResultSet rs = conn.createStatement().executeQuery(
                     "SELECT id,nome,departamento FROM Pessoa"
             )) {
            System.out.println("--- Pessoas ---");
            while (rs.next()) {
                System.out.printf("%d: %s [%s]\n",
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getString("departamento")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void listPendentes() {
        try (Connection conn = getConnection(dbName);
             ResultSet rs = conn.createStatement().executeQuery(
                     "SELECT id,titulo,pessoa_id FROM Tarefa WHERE finalizado = FALSE"
             )) {
            System.out.println("--- Tarefas pendentes ---");
            while (rs.next()) {
                System.out.printf("%d: %s (Pessoa %d)%n",
                        rs.getLong("id"),
                        rs.getString("titulo"),
                        rs.getLong("pessoa_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedPessoaTeste() {
        try (Connection conn = getConnection(dbName)) {
            conn.createStatement().executeUpdate(
                    "INSERT INTO Pessoa(nome,departamento) VALUES ('Seed','Teste');"
            );
            System.out.println("Pessoa de teste criada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedTarefaTeste() {
        try (Connection conn = getConnection(dbName)) {
            conn.createStatement().executeUpdate(
                    "INSERT INTO Tarefa(titulo,descricao,duracao,finalizado,prazo,departamento,pessoa_id) " +
                            "VALUES ('SeedTask','Desc',2,false,'2025-12-31','Teste', " +
                            "(SELECT id FROM Pessoa ORDER BY id DESC LIMIT 1)" +
                            ");"
            );
            System.out.println("Tarefa de teste criada.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
