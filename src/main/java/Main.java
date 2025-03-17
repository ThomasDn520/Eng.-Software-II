import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        AdminInterface Interface = new AdminInterface();

        AdminDAO.criarTabela();
        Database.criarTabela();

        Scanner scanner = new Scanner(System.in);

        try {
            while (true) {
                menu();

                System.out.print("Escolha uma opção (1-4): ");
                if (scanner.hasNextInt()) {
                    int opcao = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcao) {
                        case 1:
                            Interface.loginAdmin();
                            break;
                        case 2:
                            System.out.println("Ainda não implementado (loja)");
                            break;
                        case 3:
                            ClienteService.menuCliente();
                            break;
                        case 4:
                            System.out.println("Encerrando o sistema...");
                            scanner.close();
                            return;  // Encerra o programa corretamente
                        default:
                            System.out.println("Opção inválida! Digite um número entre 1 e 4.");
                    }
                } else {
                    System.out.println("Entrada inválida! Digite um número entre 1 e 4.");
                    scanner.next(); // Consumir entrada inválida
                }
            }
        } catch (IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        } finally {
            scanner.close(); // Garante que o scanner será fechado
        }
    }

    public static void menu() {
        System.out.println("\n==== Fazer Cadastro/Login como ====");
        System.out.println("1 - Admin");
        System.out.println("2 - Loja");
        System.out.println("3 - Cliente");
        System.out.println("4 - Sair do Sistema");
    }
}
