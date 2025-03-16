import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public static void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS admins (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "senha TEXT NOT NULL)";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar tabela: " + e.getMessage());
        }
    }

    public static boolean validarLogin(int id, String senha) {
        String sql = "SELECT * FROM admins WHERE id = ? AND senha = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;

            pstmt.setInt(1, id);
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

    public static void cadastrarAdmin(String nome, String email, String senha) {
        String sql = "INSERT INTO admins (nome, email, senha) VALUES (?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (conn == null) return;

            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, senha);
            pstmt.executeUpdate();
            System.out.println("Admin cadastrado com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar admin: " + e.getMessage());
        }
    }

    public static List<UserAdmin> listarTodos() {
        List<UserAdmin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admins";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserAdmin admin = new UserAdmin(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha")
                );
                admins.add(admin);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar admins: " + e.getMessage());
        }
        return admins;
    }

    public static UserAdmin buscarPorId(int id) {
        String sql = "SELECT * FROM admins WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new UserAdmin(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha")
                );
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar admin por ID: " + e.getMessage());
        }
        return null;
    }
}
