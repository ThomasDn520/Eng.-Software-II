package Cliente;

import User.User;
import User.UserCliente;
import Loja.LojaInterface;
import com.google.gson.JsonArray;

import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class ClienteInterface {

    protected Scanner scanner = new Scanner(System.in);
    protected ClienteSystem clienteSystem;

    public ClienteInterface() {
        this.clienteSystem = new ClienteSystem();
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void setClienteSystem(ClienteSystem clienteSystem) {
        this.clienteSystem = clienteSystem;
    }

    public void menuCliente(UserCliente cliente) {
        int opcao = 0;

        do {
            System.out.println("\n===== Painel do Cliente =====");
            System.out.println("Bem-vindo, " + cliente.getNome() + "!");

            System.out.println("\n1. Buscar itens");
            System.out.println("2. Carrinho de compras");
            System.out.println("3. Atualizar dados");
            System.out.println("4. Histórico de compras");
            System.out.println("5. Ver nota da loja");
            System.out.println("0. Sair do sistema");
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
                        historicoCliente(cliente);
                        break;
                    case 5:
                        System.out.print("Digite o nome da loja para ver a nota: ");
                        String nomeLoja = scanner.nextLine();
                        LojaInterface.exibirNotaLoja(nomeLoja);
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next();
            }

        } while (opcao != 0);
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
        else {
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

    /**
     * Apresenta o histórico de compras do cliente
     * @param cliente
     * @return false se ocorreu algum erro, senão true
     */
    public boolean historicoCliente(UserCliente cliente) {
        int opcao = 0;
        do {
            if(!ClienteSystem.exibirHistoricoCliente(cliente)) {
                return false;
            }

            System.out.println("Escolha o Nº da compra para mais opções, ou 0 (zero) para voltar: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
                if (opcao != 0)
                    detalheCompra(cliente, opcao-1);
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Tente novamente.");
            }
        } while (opcao != 0);

        System.out.println("Voltando ao menu do cliente...");
        return true;
    }

    /**
     * Apresenta as opções de uma compra anterior
     * @param cliente O cliente logado
     * @param indiceHistorico O número da compra
     * @return false se ocorreu algum erro, senão true
     */
    public boolean detalheCompra(UserCliente cliente, int indiceHistorico) {
        int opcao = 0;
        do {
            System.out.println("1. Avaliar produto");
            System.out.println("2. Avaliar loja");
            System.out.println("0. Voltar");
            System.out.println("Escolha uma opcao: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Tente novamente.");
                continue;
            }

            switch(opcao) {
                case 1:
                    avaliarProduto(cliente, indiceHistorico);
                    return true;
                case 2:
                    avaliarLoja(cliente, indiceHistorico);
                    return true;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        } while (opcao != 0);

        return true;
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

    /**
     * Efetua a avaliação de loja em uma compra
     * @param cliente O cliente que efetuou a compra
     * @param indiceCompra O índice da compra
     */
    protected void avaliarLoja(UserCliente cliente, int indiceCompra) {
        String nomeLoja = ClienteSystem.buscarDetalheCompra("loja", indiceCompra, cliente);
        if(nomeLoja == null) {
            System.out.println("A compra Nº " + indiceCompra + " não existe.");
            return;
        }

        System.out.print("Digite uma nota para a loja (1 a 5): ");
        int nota = 0;
        try {
            nota = Integer.parseInt(scanner.nextLine());
            if (nota < 1 || nota > 5) {
                System.out.println("Nota inválida. Deve ser entre 1 e 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número entre 1 e 5.");
            return;
        }

        System.out.print("Digite um comentário (opcional): ");
        String comentario = scanner.nextLine();

        // Agora chama o método com 4 argumentos
        boolean sucesso = ClienteSystem.avaliarLoja(cliente, nomeLoja, nota, comentario);
        if (sucesso) {
            System.out.println("Avaliação registrada com sucesso!");
        } else {
            System.out.println("Não foi possível avaliar a loja. Verifique o nome e tente novamente.");
        }
    }

    /**
     * Efetua a avaliação do produto em uma compra
     * @param cliente O cliente que efetuou a compra
     * @param indiceCompra O índice da compra
     */
    protected void avaliarProduto(UserCliente cliente, int indiceCompra) {
        String nomeLoja = ClienteSystem.buscarDetalheCompra("loja", indiceCompra, cliente);
        String nomeProduto = ClienteSystem.buscarDetalheCompra("produto", indiceCompra, cliente);
        if(nomeLoja == null || nomeProduto == null) {
            System.out.println("A compra Nº " + indiceCompra + " não existe.");
            return;
        }

        System.out.print("Digite uma nota para o produto (1 a 5): ");
        int nota = 0;
        try {
            nota = Integer.parseInt(scanner.nextLine());
            if (nota < 1 || nota > 5) {
                System.out.println("Nota inválida. Deve ser entre 1 e 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite uma avaliação entre 1 e 5.");
            return;
        }

        System.out.print("Digite um comentário (opcional): ");
        String comentario = scanner.nextLine();

        // Agora chama o método com 4 argumentos
        boolean sucesso = ClienteSystem.avaliarProduto(cliente, nomeLoja, nomeProduto, nota, comentario);
        if (sucesso) {
            System.out.println("Avaliação registrada com sucesso!");
        } else {
            System.out.println("Não foi possível avaliar a loja. Verifique o nome e tente novamente.");
        }
    }

}

