package Produto;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import User.UserLoja;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Interface de operações com produtos
 */
public class ProdutoInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    public ProdutoInterface(InputStream entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
    }

    /**
     * Mostra o menu principal de produtos
     * @param loja A loja "dona" do produto
     */
    public void menuProdutos(UserLoja loja) {
        Menu menu = new Menu()
                .adicionarCabecalho("=== MENU PRODUTOS ===")
                .adicionarOpcao("Cadastrar Produto", () -> cadastrarProduto(loja))
                .adicionarOpcao("Listar Produtos", () -> listarProdutos(loja))
                .adicionarOpcao("Buscar Produto", () -> buscarProduto(loja))
                .setPromptSaida("Sair")
                .setPromptEntrada("Escolha uma opção (0-5): ");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Mostra o diálogo para cadastro de produto
     * @param loja A loja "dona" do produto
     */
    public void cadastrarProduto(UserLoja loja) {
        Formulario formulario = new Formulario()
            .adicionarCabecalho("===== Cadastro de Produto =====")
            .adicionarCabecalho("Para cancelar, deixe um campo em branco.")
            .perguntarTexto("nome", "Nome: ")
            .perguntarValor("valor", "Valor: ")
            .perguntarTexto("tipo", "Tipo: ")
            .perguntarQuantidade("quantidade", "Quantidade: ")
            .perguntarTexto("marca", "Marca: ")
            .perguntarTexto("descricao", "Descrição: ");

        // checar se o usuário cancelou
        if(!formulario.mostrar(this.entrada, this.saida)) {
            return;
        }

        String nome = formulario.getTexto("nome");
        double valor = formulario.getValor("valor");
        String tipo = formulario.getTexto("tipo");
        int quantidade = formulario.getInteiro("quantidade");
        String marca = formulario.getTexto("marca");
        String descricao = formulario.getTexto("descricao");

        ProdutoSystem.cadastrarProduto(loja, nome, valor, tipo, quantidade, marca, descricao);
        Info.mostrar(this.saida, "Produto cadastrado com sucesso!");
    }

    /**
     * Mostra a listagem de produtos e diálogo de seleção
     * @param loja A loja "dona" do produto
     */
    public void listarProdutos(UserLoja loja) {
        Menu menu = new Menu()
            .adicionarCabecalho("=== LISTA DE PRODUTOS ===");

        List<Produto> produtos = ProdutoSystem.listarProdutos(loja);
        if (produtos.isEmpty()) {
            menu.adicionarCabecalho("Nenhum produto cadastrado.");
            menu.setPromptEntrada("Escolha uma opção: ");
        } else {
            for (Produto p : produtos) {
                // TODO: Reformatar listagem
                String seletor = String.format("%s | R$ %.2f | %s | %s\n", p.getNome(), p.getValor(), p.getTipo(), p.getMarca());
                seletor += String.format("Descricao: %s\n", p.getDescricao());
                seletor += String.format("Quantidade: %d\n", p.getQuantidade());
                seletor += "---------------------";

                menu.adicionarOpcao(seletor, () -> menuProduto(loja, p));
            }
            menu.setPromptEntrada(String.format("Escolha um produto para mais opções (0-%d):", produtos.size()));
        }

        menu.setPromptSaida("Voltar");
        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Mostra o menu de gerenciamento de produto
     * @param loja Loja "dona" do produto
     * @param produto Produto a ser gerenciado
     */
    public void menuProduto(UserLoja loja, Produto produto) {
        Menu menu = new Menu()
            .adicionarCabecalho("=== Gerenciar Produto ===")
            .adicionarOpcao("Remover produto", () -> removerProduto(loja, produto))
            .adicionarOpcao("Editar produto", () -> editarProduto(loja, produto))
            .setPromptEntrada("Escolha uma opção (0-2): ")
            .setPromptSaida("Voltar");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Mostra o diálogo para remoção de produto
     * @param loja A loja "dona" do produto
     * @param produto O produto a ser removido
     */
    public void removerProduto(UserLoja loja, Produto produto) {
        if(ProdutoSystem.removerProduto(loja, produto.getNome()))
            Info.mostrar(this.saida, "Produto removido com sucesso!");
        else
            Info.mostrar(this.saida, "Erro: Produto não encontrado!");
    }

    /**
     * Mostra o diálogo para cadastro de produto
     * @param loja A loja "dona" do produto
     * @param produto O produto a ser editado
     */
    public void editarProduto(UserLoja loja, Produto produto) {
        //TODO: Implementar
        Info.mostrar(this.saida, "Funcionalidade estará disponível em breve");
    }

    /**
     * Mostra o diálogo de busca de produto
     * @param loja A loja "dona" do produto
     */
    public void buscarProduto(UserLoja loja) {
        Formulario formulario = new Formulario()
            .perguntarTexto("nome", "Informe o nome do produto para buscar: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return;

        String nome = formulario.getTexto("nome");

        Produto produto = ProdutoSystem.buscarProduto(loja, nome);
        if(produto == null) {
            Info.mostrar(this.saida, "Produto não encontrado.");
        } else {
            Info.mostrar(this.saida,
                "=== PRODUTO ENCONTRADO ===",
                produto.toString());
        }
    }
}
