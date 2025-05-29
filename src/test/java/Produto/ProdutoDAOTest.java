package Produto;

import User.UserCliente;
import User.UserLoja;
import com.google.gson.*;

import Database.DatabaseJSON;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDAOTest {

    private UserCliente usuarioCliente;
    private UserLoja usuarioLoja;
    private UserLoja loja;
    private Produto produto, produto1, produto2, produto3, produto4;

    @BeforeEach
    void setup() {
    loja = new UserLoja(2, "Loja Teste", "teste@loja.com", "senha", "1234567899876");

    // Primeiro, inicializa o banco
    JsonArray lojas = new JsonArray();
    JsonObject lojaJson = new JsonObject();
    lojaJson.addProperty("id", loja.getId());
    lojaJson.addProperty("nome", loja.getNome());
    lojaJson.add("produtos", new JsonArray());
    lojas.add(lojaJson);

    DatabaseJSON.salvarLojas(lojas);

    // Depois, adiciona produtos
    produto = new Produto("Camiseta", 59.90, "Vestuário", 10, "Nike", "Camiseta esportiva");
    produto1 = new Produto("Camiseta Azul", 50.0, "Roupa", 10, "Marca X", "Camiseta de algodão");
    produto2 = new Produto("Camiseta Branca", 55.0, "Roupa", 15, "Marca Y", "Camiseta básica");
    produto3 = new Produto("Notebook Dell", 3500.0, "Eletrônico", 5, "Dell", "Notebook profissional");
    produto4 = new Produto("Livro Java", 89.0, "Livro", 20, "Editora Z", "Livro de programação Java");

    produto.setLoja(loja.getNome());
    produto1.setLoja(loja.getNome());
    produto2.setLoja(loja.getNome());
    produto3.setLoja(loja.getNome());
    produto4.setLoja(loja.getNome());

    ProdutoDAO.adicionarProduto(loja, produto1);
    ProdutoDAO.adicionarProduto(loja, produto2);
    ProdutoDAO.adicionarProduto(loja, produto3);
    ProdutoDAO.adicionarProduto(loja, produto4);

        usuarioCliente = new UserCliente(1, "clienteTeste", "cliente@gmail.com", "1234", "09876543123");
        usuarioLoja = new UserLoja(2,"lojaTeste", "loja@gmail.com", "senha", "123456789023");

        // Criar estrutura básica de lojas e produtos no DatabaseJSON
        JsonArray lojas2 = new JsonArray();

        JsonObject loja = new JsonObject();
        loja.addProperty("nome", "LojaLegal");

        JsonArray produtos = new JsonArray();
        JsonObject produto = new JsonObject();
        produto.addProperty("nome", "ProdutoX");
        produtos.add(produto);

        loja.add("produtos", produtos);
        lojas2.add(loja);

        DatabaseJSON.salvarLojas(lojas2);
}


    @Test
    void testAdicionarProduto() {
        boolean sucesso = ProdutoDAO.adicionarProduto(loja, produto);
        assertTrue(sucesso, "Produto deveria ser adicionado com sucesso");
    }

    @Test
    void testAdicionarProdutoDuplicado() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean sucesso = ProdutoDAO.adicionarProduto(loja, produto);
        assertFalse(sucesso, "Produto duplicado não deveria ser adicionado");
    }

    @Test
    void testBuscarProduto() {
        ProdutoDAO.adicionarProduto(loja, produto);
        Produto buscado = ProdutoDAO.buscarProduto(loja, "Camiseta");
        assertNotNull(buscado);
        assertEquals("Camiseta", buscado.getNome());
    }

    @Test
    void testListarProdutos() {
        ProdutoDAO.adicionarProduto(loja, produto);
        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
        assertEquals(5, produtos.size());
        assertEquals("Camiseta Azul", produtos.get(0).getNome());
    }

    @Test
    void testRemoverProduto() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean removido = ProdutoDAO.removerProduto(loja, "Camiseta");
        assertTrue(removido, "Produto deveria ser removido");
    }

    @Test
    void testAtualizarEstoqueLojaCompra_Sucesso() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean atualizado = ProdutoDAO.atualizarEstoqueLojaCompra(loja.getNome(), "Camiseta", 5);
        assertTrue(atualizado, "Atualização de estoque deveria ocorrer");
    }

    @Test
    void testAtualizarEstoqueLojaCompra_EstoqueInsuficiente() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean atualizado = ProdutoDAO.atualizarEstoqueLojaCompra(loja.getNome(), "Camiseta", 15);
        assertFalse(atualizado, "Não deve atualizar se não houver estoque suficiente");
    }

    @Test
    void testBuscarProdutoPorNomeExato() {
        Produto resultado = ProdutoDAO.buscarProduto(loja, "Camiseta Azul");

        assertNotNull(resultado);
        assertEquals("Camiseta Azul", resultado.getNome());
    }

    @Test
    void testBuscarProdutoSemResultados() {
        Produto resultado = ProdutoDAO.buscarProduto(loja, "Celular");

        assertNull(resultado);
    }

    @Test
    public void testAdicionarAvaliacao_Valida() {
        boolean resultado = ProdutoDAO.adicionarAvaliacao(usuarioCliente, "LojaLegal", "ProdutoX", 4, "Ótimo produto");
        assertTrue(resultado);

        // Verifica se a avaliação foi realmente adicionada no JSON
        JsonArray lojas = DatabaseJSON.carregarLojas();
        JsonObject loja = lojas.get(0).getAsJsonObject();
        JsonArray produtos = loja.getAsJsonArray("produtos");
        JsonObject produto = produtos.get(0).getAsJsonObject();
        JsonArray avaliacoes = produto.getAsJsonArray("avaliacoes");

        assertNotNull(avaliacoes);
        assertEquals(1, avaliacoes.size());

        JsonObject avaliacao = avaliacoes.get(0).getAsJsonObject();
        assertEquals("clienteTeste", avaliacao.get("usuario").getAsString());
        assertEquals(4, avaliacao.get("nota").getAsInt());
        assertEquals("Ótimo produto", avaliacao.get("comentario").getAsString());
    }

    @Test
    public void testAdicionarAvaliacao_NotaInvalida() {
        boolean resultado = ProdutoDAO.adicionarAvaliacao(usuarioCliente, "LojaLegal", "ProdutoX", 0, "Comentário");
        assertFalse(resultado);
    }

    @Test
    public void testAdicionarAvaliacao_UsuarioLoja() {
        boolean resultado = ProdutoDAO.adicionarAvaliacao(usuarioLoja, "LojaLegal", "ProdutoX", 3, "Comentário");
        assertFalse(resultado);
    }
}
