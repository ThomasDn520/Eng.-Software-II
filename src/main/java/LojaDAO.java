import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LojaDAO {
    public static void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS loja (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "senha TEXT NOT NULL, " +
                "cnpj TEXT UNIQUE NOT NULL);";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela de lojas : " + e.getMessage());
        }
    }

    public static boolean validarLogin(String email, String senha) {
        String sql = "SELECT * FROM admins WHERE email = ? AND senha = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            pstmt.setString(1, email);
            pstmt.setString(2, senha);
            ResultSet rs = pstmt.executeQuery();

            boolean existe = rs.next();
            rs.close();
            return existe;
        } catch (Exception e) {
            System.out.println("Erro ao validar login: " + e.getMessage());
        }
        return false;
    }
    public static UserLoja buscarPorEmail(String email) {
        String sql = "SELECT * FROM loja WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserLoja(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("cnpj")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar Loja " + e.getMessage());
        }
        return null;
    }
    public static boolean atualizar(UserLoja loja) {
        String sql = "UPDATE loja SET nome = ?, senha = ? WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loja.getNome());
            stmt.setString(2, loja.getSenha());
            stmt.setString(3, loja.getEmail());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Dados da loja atualizados com sucesso!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar loja: " + e.getMessage());
        }
        return false;
    }

    public static boolean removerLoja( String cnpj) {
        String sql = "DELETE FROM loja WHERE cnpj = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cnpj);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                return true;
            } else {
                System.out.println("Nenhuma loja  encontrado com este CNPJ");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir Loja: " + e.getMessage());
        }
        return false;
    }

    //criar listagem de lojas ainda


}
