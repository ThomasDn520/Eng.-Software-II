package Produto;

import User.User;
import User.UserLoja;

import java.util.List;
import java.util.Scanner;

public class ProdutoSystem {
    protected static Scanner scanner = new Scanner(System.in);

    /**
     * Cadastra um produto na base de dados
     * @param loja A loja que vende o produto
     * @param nome nome do produto
     * @param valor valor do produto
     * @param tipo tipo do produto
     * @param quantidade quantidade do produto
     * @param marca marca do produto
     * @param descricao descricao do produto
     */
    public static void cadastrarProduto(UserLoja loja, String nome, double valor, String tipo, int quantidade, String marca, String descricao) {
        Produto produto = new Produto(nome, valor, tipo, quantidade, marca, descricao);
        produto.setLoja(loja.getNome());
        ProdutoDAO.adicionarProduto(loja, produto);
    }

    /**
     * Lista todos os produtos salvos
     * @param loja A loja do produto
     * @return A lista de produtos vendidos pela loja
     */
    public static List<Produto> listarProdutos(UserLoja loja) {
        return ProdutoDAO.listarProdutos(loja);
    }

    /**
     * Remove um produto da base de dados
     * @param loja A loja "dona" do produto
     * @param nomeProduto O nome do produto a ser removido
     * @return true se o produto foi removido, false se o produto não existe no estoque da loja
     */
    public static boolean removerProduto(UserLoja loja, String nomeProduto) {
        return ProdutoDAO.removerProduto(loja, nomeProduto);
    }

    /**
     * Busca um produto pelo nome
     * @param loja O nome da loja na qual o produto será procurado
     * @param nomeProduto O nome do produto
     * @return O produto ou null se não foi encontrado
     */
    public static Produto buscarProduto(UserLoja loja, String nomeProduto) {
        return ProdutoDAO.buscarProduto(loja, nomeProduto);
    }

    /**
     * Registra a avaliação de um produto
     * @param usuario O usuário fazendo a avaliação do produto
     * @param nomeLoja A loja na qual o produto foi comprado
     * @param nomeProduto O nome do produto
     * @param nota A nota do produto
     * @param comentario Um comentário opcional sobre o produto
     * @return true se a avaliação foi adicionada, false caso tenha ocorrido erro
     */
    public static boolean avaliarProduto(User usuario, String nomeLoja, String nomeProduto, int nota, String comentario) {
        return ProdutoDAO.adicionarAvaliacao(usuario, nomeLoja, nomeProduto, nota, comentario);
    }

    // TODO: Remover, só está sendo usado pelos testes
    protected static void avaliarProduto(User usuario) {
        System.out.print("Informe o nome da loja: ");
        String nomeLoja = scanner.nextLine();

        System.out.print("Informe o nome do produto que deseja avaliar: ");
        String nomeProduto = scanner.nextLine();

        int nota;
        try {
            System.out.print("Nota (1 a 5): ");
            nota = Integer.parseInt(scanner.nextLine());
            if (nota < 1 || nota > 5) {
                System.out.println("Nota inválida. Deve estar entre 1 e 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Nota inválida.");
            return;
        }

        System.out.print("Comentário (opcional): ");
        String comentario = scanner.nextLine();

        boolean sucesso = ProdutoDAO.adicionarAvaliacao(usuario, nomeLoja, nomeProduto, nota, comentario);

        if (sucesso) {
            System.out.println("Avaliação adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação.");
        }
    }
}
