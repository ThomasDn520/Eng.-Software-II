package Produto;

import Database.DatabaseJSON;
import User.User;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import User.UserCliente;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProdutoDAOTest {

    private static UserLoja loja;
    private static Produto produto;

    @BeforeEach
    public void setup() {
        // Criar loja e produto fictícios
        loja = new UserLoja(1000, "Loja Teste", "teste@loja.com", "senha", "78945413214542");
        loja.setId(1000);  // ID fixo para facilitar testes

        produto = new Produto("Produto Teste", 10.0, "Tipo A", 5, "Marca X", "Descrição teste");

        // Cria estrutura JSON da loja e salva no "banco"
        JsonObject lojaJson = new JsonObject();
        lojaJson.addProperty("id", loja.getId());
        lojaJson.addProperty("nome", loja.getNome());
        lojaJson.addProperty("email", loja.getEmail());
        lojaJson.add("produtos", new JsonArray());

        JsonArray lojas = new JsonArray();
        lojas.add(lojaJson);
        DatabaseJSON.salvarLojas(lojas);
    }

    @Test
    public void testAdicionarProduto() {
        boolean resultado = ProdutoDAO.adicionarProduto(loja, produto);
        assertTrue(resultado);

        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
        assertEquals(1, produtos.size());
        assertEquals("Produto Teste", produtos.get(0).getNome());
    }

    @Test
    public void testAdicionarProdutoDuplicado() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean resultado = ProdutoDAO.adicionarProduto(loja, produto); // repetido
        assertFalse(resultado);
    }

    @Test
    public void testRemoverProduto() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean removido = ProdutoDAO.removerProduto(loja, "Produto Teste");
        assertTrue(removido);

        List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
        assertEquals(0, produtos.size());
    }

    @Test
    public void testBuscarProdutoExistente() {
        ProdutoDAO.adicionarProduto(loja, produto);
        Produto buscado = ProdutoDAO.buscarProduto(loja, "Produto Teste");
        assertNotNull(buscado);
        assertEquals("Produto Teste", buscado.getNome());
    }

    @Test
    public void testBuscarProdutoInexistente() {
        Produto buscado = ProdutoDAO.buscarProduto(loja, "Inexistente");
        assertNull(buscado);
    }

    @Test
    public void testAtualizarEstoqueLojaCompra() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean resultado = ProdutoDAO.atualizarEstoqueLojaCompra("Loja Teste", "Produto Teste", 3);
        assertTrue(resultado);

        Produto atualizado = ProdutoDAO.buscarProduto(loja, "Produto Teste");
        assertEquals(2, atualizado.getQuantidade());
    }

    @Test
    public void testAtualizarEstoqueLojaCompraInsuficiente() {
        ProdutoDAO.adicionarProduto(loja, produto);
        boolean resultado = ProdutoDAO.atualizarEstoqueLojaCompra("Loja Teste", "Produto Teste", 10);
        assertFalse(resultado);
    }

    @Test
    public void testAdicionarAvaliacaoValida() {
        ProdutoDAO.adicionarProduto(loja, produto);

        User cliente = new UserCliente(10, "João", "joao@email.com", "123", "45127889562");
        boolean resultado = ProdutoDAO.adicionarAvaliacao(cliente, "Loja Teste", "Produto Teste", 5, "Ótimo!");
        assertTrue(resultado);
    }

    @Test
    public void testAdicionarAvaliacaoNotaInvalida() {
        ProdutoDAO.adicionarProduto(loja, produto);

        User cliente = new UserCliente(5, "Maria", "maria@email.com", "123", "12457889562");
        boolean resultado = ProdutoDAO.adicionarAvaliacao(cliente, "Loja Teste", "Produto Teste", 0, "Ruim!");
        assertFalse(resultado);
    }

    @Test
    public void testLojaNaoPodeAvaliarProduto() {
        ProdutoDAO.adicionarProduto(loja, produto);

        boolean resultado = ProdutoDAO.adicionarAvaliacao(loja, "Loja Teste", "Produto Teste", 4, "Gostei");
        assertFalse(resultado);
    }
}
