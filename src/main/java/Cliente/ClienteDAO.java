package Cliente;
import User.UserCliente;
import Database.Database;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public static void criarTabela() {
        String sql = "CREATE TABLE IF NOT EXISTS clientes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "senha TEXT NOT NULL, " +
                "cpf TEXT UNIQUE NOT NULL);";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela de clientes: " + e.getMessage());
        }
    }

    public static void cadastrarCliente(String nome, String email, String senha, String cpf) {
        String sql = "INSERT INTO clientes (nome, email, senha, cpf) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, cpf);
            stmt.executeUpdate();

            System.out.println("Cliente cadastrado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    public static boolean validarLogin(String email, String senha) {
        String sql = "SELECT * FROM clientes WHERE email = ? AND senha = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

    public static UserCliente buscarPorEmail(String email) {
        String sql = "SELECT * FROM clientes WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UserCliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("cpf")
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar cliente: " + e.getMessage());
        }
        return null;
    }

    public static boolean atualizar(UserCliente cliente) {
        String sql = "UPDATE clientes SET nome = ?, email = ?, senha = ?, cpf = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEmail());
            stmt.setString(3, cliente.getSenha());
            stmt.setString(4, cliente.getCpf());
            stmt.setInt(5, cliente.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
        return false;
    }

    public static boolean remover(String email) {
        String sql = "DELETE FROM clientes WHERE email = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                return true;
            } else {
                System.out.println("Nenhum cliente encontrado com este e-mail.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir cliente: " + e.getMessage());
        }
        return false;
    }

    public static List<UserCliente> listarTodos() {
        List<UserCliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM clientes";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UserCliente cliente = new UserCliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("cpf")
                );
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar clientes: " + e.getMessage());
        }
        return clientes;
    }
}

