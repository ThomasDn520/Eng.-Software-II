package Admin;

import User.UserAdmin;
import Loja.LojaInterface;

import java.util.Scanner;

public class AdminInterface {

    private Scanner scanner = new Scanner(System.in);
    private AdminSystem adminSystem;

    public AdminInterface() {
        this.adminSystem = new AdminSystem();
    }


    public void criarAdmTeste() {
        adminSystem.criarAdminDiretamente("Admin1", "admin1@email.com", "123");
        adminSystem.criarAdminDiretamente("Admin2", "admin2@email.com", "123");
    }


    public void menuAdmin(UserAdmin admin) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n===== Painel do Administrador =====");
            System.out.println("1. Criar novo admin");
            System.out.println("2. Listar admins");
            System.out.println("3. Listar clientes");
            System.out.println("4. Deletar clientes");
            System.out.println("5. Listar lojas");
            System.out.println("6. Deletar Lojas");
            System.out.println("7. Ver nota da loja");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        adminSystem.criarAdmin(scanner);
                        break;
                    case 2:
                        adminSystem.listarAdmins();
                        break;
                    case 3:
                        adminSystem.listarClientes();
                        break;
                    case 4:
                        adminSystem.removerCliente(scanner);
                        break;
                    case 5:
                        adminSystem.listarLojas();
                        break;
                    case 6:
                        adminSystem.removerLoja(scanner);
                        break;
                    case 7:
                        System.out.print("Digite o nome da loja para ver a nota: ");
                        String nomeLoja = scanner.nextLine();
                        LojaInterface.exibirNotaLoja(nomeLoja);
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next();
            }
        }
    }

    public void loginAdmin() {
        int tentativas = 0;
        final int MAX_TENTATIVAS = 5;

        while (tentativas < MAX_TENTATIVAS) {
            System.out.println("\n--- LOGIN ADMIN ---");

            // Captura ID
            int id = lerIdAdmin(scanner);

            // Captura senha
            System.out.print("Digite sua senha: ");
            String senha = scanner.nextLine();

            // Validação
            UserAdmin admin = AdminDAO.autenticar(id, senha);

            if (admin != null) {
                System.out.println("\nBem-vindo, " + admin.getNome() + "!");
                menuAdmin(admin);
                return;
            } else {
                tentativas++;
                System.out.println("\nCredenciais inválidas! Tentativas restantes: " + (MAX_TENTATIVAS - tentativas));
            }
        }

        System.out.println("\nNúmero máximo de tentativas excedido. Acesso bloqueado temporariamente.");
    }

    public int lerIdAdmin(Scanner scanner) {
        while (true) {
            try {
                System.out.print("ID do Admin: ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um número válido!");
            }
        }
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
