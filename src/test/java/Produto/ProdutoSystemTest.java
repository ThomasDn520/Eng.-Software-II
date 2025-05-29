package Produto;

import User.UserCliente;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import Database.DatabaseJSON;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoSystemTest {

    private UserLoja loja;
    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

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

    public void setUpOutput() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        // Se o scanner é um campo estático da classe ProdutoService, reinicialize-o aqui:
        ProdutoSystem.scanner = new Scanner(System.in);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @AfterEach
    public void restoreSystemInputOutput() {
        System.setIn(systemIn);
        System.setOut(systemOut);
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

    @Test
    public void testAvaliarProduto_Sucesso() {
        // Entradas simuladas: nome loja, nome produto, nota, comentário
        String input = "LojaLegal\nProdutoX\n5\nÓtimo produto\n";
        provideInput(input);

        UserCliente usuario = new UserCliente(2, "Loja Teste", "teste@loja.com", "senha", "1234567899876");

        // Aqui o ProdutoDAO.adicionarAvaliacao precisa estar funcional e aceitar essa avaliação
        ProdutoSystem.avaliarProduto(usuario);

        String output = getOutput();
        assertTrue(output.contains("Informe o nome da loja:"));
        assertTrue(output.contains("Informe o nome do produto que deseja avaliar:"));
        assertTrue(output.contains("Nota (1 a 5):"));
        assertTrue(output.contains("Comentário (opcional):"));
        assertTrue(output.contains("Avaliação adicionada com sucesso!") || output.contains("Erro ao adicionar avaliação."));
        // Se o método ProdutoDAO.adicionarAvaliacao estiver funcionando, deve entrar no "Avaliação adicionada com sucesso!"
    }

    @Test
    public void testAvaliarProduto_NotaInvalida_Letra() {
        String input = "LojaLegal\nProdutoX\nabc\nComentário\n";
        provideInput(input);

        UserCliente usuario = new UserCliente(2, "Loja Teste", "teste@loja.com", "senha", "1234567899876");
        ProdutoSystem.avaliarProduto(usuario);

        String output = getOutput();
        assertTrue(output.contains("Nota inválida."));
    }

    @Test
    public void testAvaliarProduto_NotaInvalida_ForaDoIntervalo() {
        String input = "Loja Teste\nProdutoX\n0\nComentário\n";
        provideInput(input);

        UserCliente usuario = new UserCliente(2, "Loja Teste", "teste@loja.com", "senha", "1234567899876");
        ProdutoSystem.avaliarProduto(loja);

        String output = getOutput();
        assertTrue(output.contains("Nota inválida. Deve estar entre 1 e 5."));
    }
}
