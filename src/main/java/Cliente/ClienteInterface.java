package Cliente;

import User.User;
import User.UserCliente;

import java.util.Scanner;

public class ClienteInterface {

    protected Scanner scanner = new Scanner(System.in);
    protected ClienteSystem clienteSystem;

    public ClienteInterface() {
        this.clienteSystem = new ClienteSystem();
    }

    public void menuCliente(UserCliente cliente) {
        int opcao = 0;

        do {
            System.out.println("\n===== Painel do Cliente =====");
            System.out.println("Bem-vindo, " + cliente.getNome() + "!");

            System.out.println("\n1. Buscar itens");
            System.out.println("2. Carrinho de compras");
            System.out.println("3. Atualizar dados");
            System.out.println("4. Sair do sistema");
            System.out.println("5. Historico de compras");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine(); // limpa o buffer

                switch (opcao) {
                    case 1:
                        buscarProdutoNome(cliente, scanner);
                        break;
                    case 2:
                        menuCarrinho(cliente);
                        break;
                    case 3:
                        clienteSystem.atualizarCliente(scanner, cliente);
                        break;
                    case 4:
                        System.out.println("Saindo...");
                        break;
                    case 5:
                        historicoCliente(cliente);
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next();
            }

        } while (opcao != 4);
    }


    public void menuCarrinho(UserCliente cliente) {
        int opcao = 0;

        do {

            System.out.println("\n===== Painel Carrinho de Compras =====");
            System.out.println("Carrinho de " + cliente.getNome() + "!");
            System.out.println("\n1. Exibir itens");
            System.out.println("2. Remover item");
            System.out.println("3. Concluir compra");
            System.out.println("4. Sair do sistema");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        exibirItensCarrinho(cliente);
                        break;
                    case 2:
                        removerItemCarrinho(cliente, scanner);
                        break;
                    case 3:
                        efetuarCompra(cliente, scanner);
                        break;
                    case 4:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Entrada inválida. Digite um número.");
                scanner.nextLine();
            }

        } while (opcao != 4);
    }


    public boolean buscarProdutoNome(UserCliente cliente, Scanner scanner){
        System.out.print("\nDigite o nome do produto que deseja buscar: ");
        String nomeBusca = scanner.nextLine().trim().toLowerCase();
        if(ClienteSystem.buscarProdutoPorNome(cliente, scanner, nomeBusca)){
            return true;
        }
        else{
            return false;
        }
    }

    public static boolean efetuarCompra(UserCliente cliente, Scanner scanner){
        return ClienteSystem.efetuarCompra(cliente, scanner);
    }

    public static boolean exibirItensCarrinho(UserCliente cliente){
        if(ClienteSystem.exibirCarrinho(cliente)){
            return true;
        }
        return false;
    }

    public static boolean removerItemCarrinho(UserCliente cliente, Scanner scanner){
        System.out.println("Qual produto voce deseja remover do carrinho?");

        do {
            if(!ClienteSystem.exibirCarrinho(cliente)){
                break;
            }
            System.out.println("Produtos no carrinho");

            System.out.print("Digite um nome (ou um número para sair): ");
            String itemRemover = scanner.nextLine().trim();

            if (itemRemover.matches("\\d+")) {
                System.out.println("Nenhum item removido, saindo...");
                break;
            }
            if (ClienteSystem.removerProduto(cliente, itemRemover)) {
                System.out.println("Produto removido com sucesso.");
                return true;
            } else {
                System.out.println("Produto não encontrado no carrinho. Tente novamente.");
            }

        } while (true);

        return false;
    }

    public static boolean historicoCliente(UserCliente cliente){
        return  ClienteSystem.exibirHistoricoCliente(cliente);
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
                    cadastrarCliente(scanner);
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

    private void cadastrarCliente(Scanner scanner) {
        System.out.println("\nDigite 0 a qualquer momento para cancelar o cadastro.");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        if (nome.equals("0")) return;

        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        if (email.equals("0")) return;

        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        if (senha.equals("0")) return;

        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        if (cpf.equals("0")) return;

        clienteSystem.criarCliente(nome, email, senha, cpf);
    }



}

