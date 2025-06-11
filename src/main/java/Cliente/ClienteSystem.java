package Cliente;

import User.UserCliente;
import Produto.Produto;
import Produto.ProdutoDAO;
import Produto.ProdutoSystem;
import Loja.LojaDAO;

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

    /**
     * Atualiza os dados de um cliente
     * @param cliente Cliente com os dados atualizados
     * @return true se o cliente foi atualizado com sucesso, false se ocorreu algum erro
     */
    public static boolean atualizarCliente(UserCliente cliente) {
        return ClienteDAO.atualizar(cliente);
    }

    // TODO: Remover, só está sendo usado em testes
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

    // TODO: Mover para ProdutoSystem/ProdutoInterface
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

    public static boolean avaliarProduto(UserCliente cliente, String nomeLoja, String nomeProduto, int nota, String comentario) {
        // Adiciona avaliação do produto
        boolean sucesso = ProdutoSystem.avaliarProduto(cliente, nomeLoja, nomeProduto, nota, comentario);

        if (sucesso) {
            // Atualiza pontuação
            cliente.setPontos(cliente.getPontos()+1);
            ClienteDAO.atualizar(cliente);
            System.out.println("Avaliação do produto adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação do produto.");
        }

        return sucesso;
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
            // Atualiza pontuação
            cliente.setPontos(cliente.getPontos()+1);
            ClienteDAO.atualizar(cliente);
            System.out.println("Avaliação da loja adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação da loja.");
        }

        return sucesso;
    }

    /**
     * Busca por informação da compra anterior
     * @param detalhe O tipo de informação
     * @param indice O índice da compra
     * @param cliente O cliente que efetuou a compra
     * @return A informação requerida ou null se não foi encontrada
     */
    public static String buscarDetalheCompra(String detalhe, int indice, UserCliente cliente) {
        return ClienteDAO.buscarDetalheCompra(detalhe, indice, cliente);
    }
}
