import java.sql.*;

public class teste {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:database.db"; // Substitua pelo caminho correto do seu banco

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            // Resetar os IDs e limpar a tabela clientes
            stmt.executeUpdate("DELETE FROM clientes;");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name='clientes';");

            // Consultar todas as tabelas do banco
            ResultSet rsTabelas = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table'");

            while (rsTabelas.next()) {
                String tabela = rsTabelas.getString("name");
                System.out.println("Tabela: " + tabela);
                listarDadosTabela(conn, tabela);
                System.out.println("--------------------------------------------------\n");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tabelas e dados: " + e.getMessage());
        }
    }

    private static void listarDadosTabela(Connection conn, String tabela) {
        String sql = "SELECT * FROM " + tabela;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int colunas = metaData.getColumnCount();

            // Imprimir cabeçalhos das colunas
            for (int i = 1; i <= colunas; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println("\n------------------------------------");

            // Imprimir os dados da tabela
            while (rs.next()) {
                for (int i = 1; i <= colunas; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar dados da tabela " + tabela + ": " + e.getMessage());
        }
    }
}
