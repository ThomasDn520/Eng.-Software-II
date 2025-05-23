package Cliente;

import User.UserCliente;
import Produto.Produto;
import Produto.ProdutoDAO;
import Loja.LojaDAO;

import java.util.List;
import java.util.Scanner;

public class ClienteSystem {

    public static void criarCliente(String nome, String email, String senha, String cpf) {
        int id = ClienteDAO.cadastrarCliente(nome, email, senha, cpf);
        if (id > 0) {
            System.out.println("Cliente criado com sucesso! ID: " + id);
        } else {
            System.out.println("Erro ao criar cliente.");
        }
    }

    public static UserCliente autenticarCliente(String email, String senha) {
        // Ajuste: ClienteDAO.validarLogin deve retornar boolean ou UserCliente?
        // Aqui assumo que validarLogin retorna boolean e buscarPorEmail retorna o objeto.
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

        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setSenha(senha);
        cliente.setCpf(cpf);

        if (ClienteDAO.atualizar(cliente)) {
            System.out.println("Dados do cliente atualizados com sucesso!");
        } else {
            System.out.println("Erro ao atualizar dados. Talvez o e-mail já esteja em uso.");
        }
    }

    public static boolean buscarProdutoPorNome(UserCliente cliente, Scanner scanner, String nomeBusca) {
        Produto[] produtos = ProdutoDAO.buscarTodosProdutos();
        boolean encontrado = false;

        System.out.println("\n=== RESULTADOS DA BUSCA ===");

        for (Produto produto : produtos) {
            if (produto.getNome().toLowerCase().contains(nomeBusca.toLowerCase())) {
                System.out.println("Nome: " + produto.getNome());
                System.out.println("Loja: " + produto.getLoja());
                System.out.println("Valor: R$" + produto.getValor());
                System.out.println("Tipo: " + produto.getTipo());
                System.out.println("Marca: " + produto.getMarca());
                System.out.println("Descrição: " + produto.getDescricao());
                System.out.println("-----------------------------");

                encontrado = true;
            }
        }

        if (!encontrado) {
            System.out.println("Nenhum produto encontrado.");
            return false;
        }

        System.out.println("\nDigite o nome e loja do produto para adicionar ao carrinho:");

        System.out.print("Nome do produto: ");
        String nomeProdutoBusca = scanner.nextLine().trim();

        System.out.print("Nome da loja: ");
        String nomeLojaBusca = scanner.nextLine().trim();

        for (Produto produto : produtos) {
            if (produto.getNome().equalsIgnoreCase(nomeProdutoBusca) &&
                    produto.getLoja().equalsIgnoreCase(nomeLojaBusca)) {
                ClienteDAO.adicionarProdutoAoCarrinho(cliente, produto, 1);
                System.out.println("Produto adicionado ao carrinho.");
                return true;
            }
        }

        System.out.println("Produto não encontrado para adição no carrinho.");
        return false;
    }

    public static boolean exibirCarrinho(UserCliente cliente) {
        return ClienteDAO.exibirItensCarrinho(cliente);
    }

    public static boolean removerProduto(UserCliente cliente, String itemRemover) {
        if (ClienteDAO.exibirItensCarrinho(cliente)) {
            return ClienteDAO.removerProdutoDoCarrinho(cliente, itemRemover);
        }
        return false;
    }

    public static boolean exibirHistoricoCliente(UserCliente cliente) {
        return ClienteDAO.exibirHistoricoCompras(cliente);
    }

    public static boolean efetuarCompra(UserCliente cliente, Scanner scanner) {
        return ClienteDAO.efetuarCompra(cliente, scanner);
    }

    public static void avaliarProduto(UserCliente cliente, Scanner scanner) {
        System.out.println("\n=== Avaliar Produto ===");

        System.out.print("Nome da loja do produto: ");
        String nomeLoja = scanner.nextLine().trim();

        System.out.print("Nome do produto: ");
        String nomeProduto = scanner.nextLine().trim();

        int nota;
        try {
            System.out.print("Nota (1 a 5): ");
            nota = Integer.parseInt(scanner.nextLine().trim());
            if (nota < 1 || nota > 5) {
                System.out.println("Nota inválida. A nota deve estar entre 1 e 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para nota. Use apenas números de 1 a 5.");
            return;
        }

        System.out.print("Comentário (opcional): ");
        String comentario = scanner.nextLine().trim();

        boolean sucesso = ProdutoDAO.adicionarAvaliacao(cliente, nomeLoja, nomeProduto, nota, comentario);

        if (sucesso) {
            System.out.println("Avaliação adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação. Verifique se o nome da loja e produto estão corretos.");
        }
    }

    public static boolean avaliarLoja(UserCliente cliente, String nomeLoja, int nota, String comentario) {
        // Verifica se a loja existe
        if (!LojaDAO.existeLoja(nomeLoja)) {
            System.out.println("Loja não encontrada.");
            return false;
        }

        // Adiciona avaliação da loja
        boolean sucesso = LojaDAO.adicionarAvaliacaoLoja(cliente.getId(), nomeLoja, nota, comentario);

        if (sucesso) {
            System.out.println("Avaliação da loja adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação da loja.");
        }

        return sucesso;
    }



}
