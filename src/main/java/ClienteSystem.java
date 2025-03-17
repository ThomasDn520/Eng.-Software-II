import java.sql.*;
import java.util.Scanner;

public class ClienteSystem {

    public static void criarCliente(String nome, String email, String senha, String cpf) {
        ClienteDAO.cadastrarCliente(nome, email, senha, cpf);
    }


    public static void atualizarCliente(Scanner scanner) {
        System.out.print("Informe o ID do cliente: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Novo e-mail: ");
        String email = scanner.nextLine();
        System.out.print("Nova senha: ");
        String senha = scanner.nextLine();
        System.out.print("Novo CPF: ");
        String cpf = scanner.nextLine();

        String sql = "UPDATE clientes SET nome = ?, email = ?, senha = ?, cpf = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, cpf);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            System.out.println("Cliente atualizado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }
}
