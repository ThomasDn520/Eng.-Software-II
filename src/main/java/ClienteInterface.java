import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ClienteInterface {

    private Scanner scanner = new Scanner(System.in);
    private ClienteSystem clienteSystem;

    public ClienteInterface() {
        this.clienteSystem = new ClienteSystem();
    }

    public static void loginCadastroCliente() {
        while (true) {
            System.out.println("\n==== LOGIN/CADATRO CLIENTE ====");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Fazer Login");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção (1-4): ");

            Scanner scanner = new Scanner(System.in);
            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();
                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();

                    ClienteSystem.criarCliente(nome, email, senha, cpf);
                    break;
                case 2:
                    loginCliente(scanner);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    public static void loginCliente(Scanner scanner) {

                System.out.print("E-mail: ");
                String email = scanner.nextLine();
                System.out.print("Senha: ");
                String senha = scanner.nextLine();

                String sql = "SELECT * FROM clientes WHERE email = ? AND senha = ?";

                try (Connection conn = Database.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, email);
                    stmt.setString(2, senha);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("\n Login bem-sucedido! Bem-vindo, " + rs.getString("nome") + "!");

                    } else {
                        System.out.println("\n E-mail ou senha incorretos. Tente novamente.");
                    }
                } catch (SQLException e) {
                    System.out.println("Erro ao fazer login: " + e.getMessage());
                }
        }

    }

