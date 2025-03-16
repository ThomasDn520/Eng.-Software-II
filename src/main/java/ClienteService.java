import java.util.Scanner;

public class ClienteService {
    private static Scanner scanner = new Scanner(System.in);

    public static void cadastrarCliente() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();

        if (ClienteRepository.buscarPorEmail(email) != null) {
            System.out.println("Erro: E-mail já cadastrado!");
            return;
        }

        Cliente cliente = new Cliente(0, nome, email, senha, cpf);
        ClienteRepository.adicionar(cliente);
    }

    public static void login() {
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Cliente cliente = ClienteRepository.buscarPorEmail(email);
        if (cliente == null || !cliente.getSenha().equals(senha)) {
            System.out.println("E-mail ou senha inválidos!");
            return;
        }

        System.out.println("Bem-vindo, " + cliente.getNome() + "!");
        menuCliente(cliente);
    }

    private static void menuCliente(Cliente cliente) {
        while (true) {
            System.out.println("\n=== MENU CLIENTE ===");
            System.out.println("1. Atualizar Nome");
            System.out.println("2. Atualizar Senha");
            System.out.println("3. Excluir Conta");
            System.out.println("4. Logout");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();
            switch (opcao) {
                case "1":
                    System.out.print("Novo Nome: ");
                    cliente.setNome(scanner.nextLine());
                    ClienteRepository.atualizar(cliente);
                    break;
                case "2":
                    System.out.print("Nova Senha: ");
                    cliente.setSenha(scanner.nextLine());
                    ClienteRepository.atualizar(cliente);
                    break;
                case "3":
                    ClienteRepository.remover(cliente.getEmail());
                    return;
                case "4":
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }
}
