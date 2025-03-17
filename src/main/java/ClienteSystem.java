import java.sql.*;
import java.util.Scanner;

public class ClienteSystem {

    public static void criarCliente(String nome, String email, String senha, String cpf) {
        ClienteDAO.cadastrarCliente(nome, email, senha, cpf);
    }

    public UserCliente autenticarCliente(String email, String senha){
        if (ClienteDAO.validarLogin(email, senha)) {
            return ClienteDAO.buscarPorEmail(email);
        }
        return null;
    }

    public static void atualizarCliente(Scanner scanner, UserCliente cliente) {
        System.out.println("\n===== Atualização de Dados =====");
        System.out.println("Deixe em branco para manter os dados atuais.");

        System.out.print("Novo nome (" + cliente.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = cliente.getNome();

        System.out.print("Novo e-mail (" + cliente.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = cliente.getEmail();

        System.out.print("Nova senha: ");
        String senha = scanner.nextLine().trim();
        if (senha.isEmpty()) senha = cliente.getSenha();

        System.out.print("Novo CPF (" + cliente.getCpf() + "): ");
        String cpf = scanner.nextLine().trim();
        if (cpf.isEmpty()) cpf = cliente.getCpf();

        String sql = "UPDATE clientes SET nome = ?, email = ?, senha = ?, cpf = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, cpf);
            stmt.setInt(5, cliente.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Dados atualizados com sucesso!");
                // Atualiza o objeto do cliente logado
                cliente.setNome(nome);
                cliente.setEmail(email);
                cliente.setSenha(senha);
                cliente.setCpf(cpf);
            } else {
                System.out.println("Erro ao atualizar os dados.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }


}
