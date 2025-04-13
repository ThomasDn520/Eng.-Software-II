package Cliente;

import User.UserCliente;

import java.util.Scanner;

public class ClienteInterface {

    protected Scanner scanner = new Scanner(System.in);
    protected ClienteSystem clienteSystem;

    public ClienteInterface() {
        this.clienteSystem = new ClienteSystem();
    }

    public void menuCliente(UserCliente cliente){
        boolean continuar = true;
        while (continuar){
            System.out.println("\n===== Painel do Cliente =====");
            System.out.println("Bem vindo, " + cliente.getNome() + "!");

            System.out.println("\n1. Buscar itens");
            System.out.println("2. Buscar Lojas");
            System.out.println("3. Atualizar dados");
            System.out.println("4. Sair do sistema");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.println("Função não implementada!");
                        break;
                    case 2:
                        System.out.println("Função não implementada!");
                        break;
                    case 3:
                        clienteSystem.atualizarCliente(scanner, cliente);
                        break;
                    case 4:
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


    public void loginCadastroCliente() {
        while (true) {
            System.out.println("\n==== LOGIN/CADATRO CLIENTE ====");
            System.out.println("1. Cadastrar Cliente");
            System.out.println("2. Fazer Login");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção (1-3): ");

            Scanner scanner = new Scanner(System.in);
            int opcao = scanner.nextInt();
            scanner.nextLine();

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

                    clienteSystem.criarCliente(nome, email, senha, cpf);
                    break;
                case 2:
                    loginCliente();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }
    }

    public void loginCliente() {
        int tentativas = 0;
        while(tentativas < 5) {
            System.out.print("E-mail: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            UserCliente cliente = clienteSystem.autenticarCliente(email, senha);
            if (cliente != null) {
                menuCliente(cliente);
                return;
            } else {
                System.out.println("ID ou senha incorretos!");
                tentativas++;
            }

        } System.out.println("Número de tentativas excedido. Retornando ao menu inicial...");

    }

}

