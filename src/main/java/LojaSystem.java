import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class LojaSystem {

    public static void criarLoja(String nome, String email, String senha, String CNPJ){

    }

    public UserLoja autenticarLoja(String email, String senha){
        if (LojaDAO.validarLogin(email, senha)) {
            return LojaDAO.buscarPorEmail(email);
        }
        return null;
    }


    //criar atualização da Loja
    public static void atualizarLoja(Scanner scanner, UserLoja loja) {
        System.out.println("\n===== Atualização de Dados =====");
        System.out.println("Deixe em branco para manter os dados atuais.");

        System.out.print("Novo nome (" + loja.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = loja.getNome();

        System.out.print("Novo e-mail (" + loja.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = loja.getEmail();

        System.out.print("Nova senha: ");
        String senha = scanner.nextLine().trim();
        if (senha.isEmpty()) senha = loja.getSenha();

        System.out.print("Novo CNPJ (" + loja.getCnpj() + "): ");
        String cnpj = scanner.nextLine().trim();
        if (cnpj.isEmpty()) cnpj = loja.getCnpj();

        String sql = "UPDATE clientes SET nome = ?, email = ?, senha = ?, cnpj = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senha);
            stmt.setString(4, cnpj);
            stmt.setInt(5, loja.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Dados atualizados com sucesso!");
                // Atualiza o objeto do cliente logado
                loja.setNome(nome);
                loja.setEmail(email);
                loja.setSenha(senha);
                loja.setCnpj(cnpj);
            } else {
                System.out.println("Erro ao atualizar os dados.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

}
