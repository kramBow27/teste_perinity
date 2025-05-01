package org.techenriqueluna.execcli;

import org.postgresql.util.PSQLException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    private static final String DEFAULT_SUPERUSER = "postgres";
    private static final String DEFAULT_DB = "pessoas_tarefas";
    private static String superUser = DEFAULT_SUPERUSER;
    private static String superPassword;
    private static final String dbName = DEFAULT_DB;
    private static final String BASE_URL = "http://localhost:8080";

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

        System.out.printf("Usuário superuser (default %s): ", DEFAULT_SUPERUSER);
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) superUser = input;
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
            System.out.println("1 - Listar Pessoas (BD)");
            System.out.println("2 - Listar Tarefas Pendentes (BD)");
            System.out.println("3 - Criar Pessoa de Teste (BD)");
            System.out.println("4 - Criar Tarefa de Teste (BD)");
            System.out.println("5 - Adicionar Pessoa (API)");
            System.out.println("6 - Atualizar Pessoa (API)");
            System.out.println("7 - Remover Pessoa (API)");
            System.out.println("8 - Listar Pessoas com Horas (API)");
            System.out.println("9 - Média de Gastos (API)");
            System.out.println("10 - Adicionar Tarefa (API)");
            System.out.println("11 - Alocar Tarefa (API)");
            System.out.println("12 - Finalizar Tarefa (API)");
            System.out.println("13 - Pendentes (API)");
            System.out.println("14 - Todas Pendentes (API)");
            System.out.println("15 - Departamentos (API)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String opt = scanner.nextLine();
            switch (opt) {
                case "1": listPessoas(); break;
                case "2": listPendentes(); break;
                case "3": seedPessoaTeste(); break;
                case "4": seedTarefaTeste(); break;
                case "5": apiAddPessoa(scanner); break;
                case "6": apiUpdatePessoa(scanner); break;
                case "7": apiDeletePessoa(scanner); break;
                case "8": apiListPessoasHoras(); break;
                case "9": apiMediaGastos(scanner); break;
                case "10": apiAddTarefa(scanner); break;
                case "11": apiAlocarTarefa(scanner); break;
                case "12": apiFinalizarTarefa(scanner); break;
                case "13": apiPendentes(); break;
                case "14": apiTodasPendentes(); break;
                case "15": apiDepartamentos(); break;
                case "0": System.out.println("Saindo..."); return;
                default: System.out.println("Opção inválida.");
            }
        }
    }


    private static void apiAddPessoa(Scanner sc) {
        System.out.print("Nome: "); String nome = sc.nextLine();
        System.out.print("Departamento: "); String dept = sc.nextLine();
        String json = String.format("{\"nome\":\"%s\",\"departamento\":\"%s\"}", nome, dept);
        callApi("POST", "/pessoas", json);
    }

    private static void apiUpdatePessoa(Scanner sc) {
        System.out.print("ID da pessoa a atualizar: "); String id = sc.nextLine();
        System.out.print("Novo nome: "); String nome = sc.nextLine();
        System.out.print("Novo departamento: "); String dept = sc.nextLine();
        String json = String.format("{\"nome\":\"%s\",\"departamento\":\"%s\"}", nome, dept);
        callApi("PUT", "/pessoas/"+id, json);
    }

    private static void apiDeletePessoa(Scanner sc) {
        System.out.print("ID da pessoa a remover: "); String id = sc.nextLine();
        callApi("DELETE", "/pessoas/"+id, null);
    }

    private static void apiListPessoasHoras() {
        callApi("GET", "/pessoas", null);
    }

    private static void apiMediaGastos(Scanner sc) {
        System.out.print("Nome (filtro): "); String nome = sc.nextLine();
        System.out.print("Data início (YYYY-MM-DD): "); String ini = sc.nextLine();
        System.out.print("Data fim (YYYY-MM-DD): "); String fim = sc.nextLine();
        String query = String.format("/pessoas/gastos?nome=%s&inicio=%s&fim=%s", nome, ini, fim);
        callApi("GET", query, null);
    }

    private static void apiAddTarefa(Scanner sc) {
        System.out.print("Título: "); String t = sc.nextLine();
        System.out.print("Descrição: "); String d = sc.nextLine();
        System.out.print("Prazo (YYYY-MM-DD): "); String p = sc.nextLine();
        System.out.print("Departamento: "); String dept = sc.nextLine();
        System.out.print("Duração: "); String dur = sc.nextLine();
        String json = String.format(
                "{\"titulo\":\"%s\",\"descricao\":\"%s\",\"prazo\":\"%s\",\"departamento\":\"%s\",\"duracao\":%s}",
                t,d,p,dept,dur
        );
        callApi("POST", "/tarefas", json);
    }

    private static void apiAlocarTarefa(Scanner sc) {
        System.out.print("ID tarefa: "); String tid = sc.nextLine();
        System.out.print("ID pessoa: "); String pid = sc.nextLine();
        callApi("PUT", "/tarefas/alocar/"+tid+"?pessoa="+pid, null);
    }

    private static void apiFinalizarTarefa(Scanner sc) {
        System.out.print("ID tarefa: "); String tid = sc.nextLine();
        callApi("PUT", "/tarefas/finalizar/"+tid, null);
    }

    private static void apiPendentes() {
        callApi("GET", "/tarefas/pendentes", null);
    }

    private static void apiTodasPendentes() {
        callApi("GET", "/tarefas/todaspendentes", null);
    }

    private static void apiDepartamentos() {
        callApi("GET", "/departamentos", null);
    }

    private static void callApi(String method, String path, String body) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(body != null);
            if (body != null) {
                try (OutputStream os = con.getOutputStream()) {
                    os.write(body.getBytes());
                }
            }
            int code = con.getResponseCode();
            System.out.println("HTTP " + code + " " + con.getResponseMessage());
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    con.getInputStream()))) {
                br.lines().forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar API: " + e.getMessage());
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
            // tabelas
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

            st.executeUpdate("INSERT INTO Pessoa(nome, departamento) VALUES ('Teste1','TI');");
            st.executeUpdate("INSERT INTO Pessoa(nome, departamento) VALUES ('Teste2','RH');");
            st.executeUpdate(
                    "INSERT INTO Tarefa(titulo,descricao,duracao,finalizado,prazo,departamento,pessoa_id) " +
                            "VALUES ('T1','Desc1',3,false,'2025-12-31','TI',1);"
            );
            System.out.println("Schema e dados de teste criados.");
        }
    }

}
