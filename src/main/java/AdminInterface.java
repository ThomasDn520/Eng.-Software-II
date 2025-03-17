import java.util.Scanner;

public class AdminInterface {

        private Scanner scanner = new Scanner(System.in);
        private AdminSystem adminSystem;

        public AdminInterface() {
            this.adminSystem = new AdminSystem();
        }

        public void menuAdmin(UserAdmin admin) {
            boolean continuar = true;
            while (continuar) {
                System.out.println("\n===== Painel do Administrador =====");
                System.out.println("1. Criar novo admin");
                System.out.println("2. Listar admins");
                System.out.println("3. Sair");
                System.out.println("4. Listar clientes");
                System.out.println("5. Deletar clientes");
                System.out.print("Escolha uma opção: ");

                if (scanner.hasNextInt()) {
                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            System.out.print("Nome: ");
                            String nome = scanner.nextLine();
                            System.out.print("Email: ");
                            String email = scanner.nextLine();
                            System.out.print("Senha: ");
                            String senha = scanner.nextLine();
                            adminSystem.criarAdmin(nome, email, senha);
                            break;
                        case 2:
                            adminSystem.listarAdmins();
                            break;
                        case 3:
                            continuar = false;
                            System.out.println("Saindo...");
                            break;
                        case 4:
                            adminSystem.listarClientes();
                            break;
                        case 5:
                            adminSystem.removerCliente(scanner);
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
            while (tentativas < 5) {
                System.out.print("\nDigite seu ID de Admin: ");
                if (scanner.hasNextInt()) {
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Digite sua senha: ");
                    String senha = scanner.nextLine();

                    UserAdmin admin = adminSystem.autenticarAdmin(id, senha);
                    if (admin != null) {
                        System.out.println("Bem-vindo, " + admin.getNome() + "!");
                        menuAdmin(admin);
                        return;
                    } else {
                        System.out.println("ID ou senha incorretos!");
                        tentativas++;
                    }
                } else {
                    System.out.println("Entrada inválida! Digite um número.");
                    scanner.next();
                }
            }
            System.out.println("Número de tentativas excedido. Retornando ao menu inicial...");
        }



}
