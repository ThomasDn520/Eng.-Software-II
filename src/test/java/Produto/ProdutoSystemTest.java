package Produto;

import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Database.DatabaseJSON;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoSystemTest {

    private UserLoja loja;

    @BeforeEach
    void setUp() {
        loja = new UserLoja(3, "Loja Teste", "teste@loja.com", "senha123", "1234567898746");

        JsonArray lojas = new JsonArray();
        JsonObject lojaJson = new JsonObject();
        lojaJson.addProperty("id", loja.getId());
        lojaJson.addProperty("nome", loja.getNome());
        lojaJson.add("produtos", new JsonArray());
        lojas.add(lojaJson);

        DatabaseJSON.salvarLojas(lojas);
    }

    @Test
    void testCadastrarProdutoDiretamente() {
        // Simula cadastro manual
        Produto produto = new Produto("Camiseta", 59.90, "Vestuário", 10, "Nike", "Camiseta confortável");
        produto.setLoja(loja.getNome());
        ProdutoDAO.adicionarProduto(loja, produto);

        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
        assertEquals(1, produtos.size());
        assertEquals("Camiseta", produtos.get(0).getNome());
        assertEquals(59.90, produtos.get(0).getValor());
    }

    @Test
    void testRemoverProduto() {
        Produto produto = new Produto("Tênis", 299.99, "Calçados", 5, "Adidas", "Tênis esportivo");
        produto.setLoja(loja.getNome());
        ProdutoDAO.adicionarProduto(loja, produto);

        boolean removido = ProdutoDAO.removerProduto(loja, "Tênis");

        assertTrue(removido, "Produto deveria ser removido com sucesso");

        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
        assertTrue(produtos.isEmpty(), "Lista de produtos deveria estar vazia após remoção");
    }

    @Test
    void testRemoverProdutoInexistente() {
        boolean removido = ProdutoDAO.removerProduto(loja, "Produto Que Não Existe");

        assertFalse(removido, "Não deveria remover nada");
    }

    @Test
    void testBuscarProdutoExistente() {
        Produto produto = new Produto("Relógio", 499.90, "Acessórios", 3, "Casio", "Relógio digital");
        produto.setLoja(loja.getNome());
        ProdutoDAO.adicionarProduto(loja, produto);

        Produto encontrado = ProdutoDAO.buscarProduto(loja, "Relógio");

        assertNotNull(encontrado);
        assertEquals("Relógio", encontrado.getNome());
        assertEquals(499.90, encontrado.getValor());
    }

    @Test
    void testBuscarProdutoInexistente() {
        Produto encontrado = ProdutoDAO.buscarProduto(loja, "Produto Fantasma");

        assertNull(encontrado);
    }

    @Test
    void testListarProdutos() {
        Produto produto1 = new Produto("Fone", 129.99, "Eletrônicos", 20, "Sony", "Fone de ouvido bluetooth");
        Produto produto2 = new Produto("Mouse", 89.90, "Informática", 15, "Logitech", "Mouse sem fio");

        produto1.setLoja(loja.getNome());
        produto2.setLoja(loja.getNome());

        ProdutoDAO.adicionarProduto(loja, produto1);
        ProdutoDAO.adicionarProduto(loja, produto2);

        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);

        assertEquals(2, produtos.size());
    }
}
