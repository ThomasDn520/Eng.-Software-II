package Cliente;

import Produto.*;
import User.UserCliente;
import Database.DatabaseJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteDAOTest {

    private UserCliente cliente;

    private final PrintStream outputOriginal = System.out;
    private final ByteArrayOutputStream outputCapturado = new ByteArrayOutputStream();
    private static final String FILE_NAME_CLIENTE = "database_cliente.json";
    private static final String BACKUP_FILE_CLIENTE = "clienteDB_backup.json";

    @BeforeAll
    void criarBackup() throws IOException {
        Files.copy(new java.io.File(FILE_NAME_CLIENTE).toPath(),
                new java.io.File(BACKUP_FILE_CLIENTE).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterAll
    void restaurarBackup() throws IOException {
        Files.copy(new java.io.File(BACKUP_FILE_CLIENTE).toPath(),
                new java.io.File(FILE_NAME_CLIENTE).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        new java.io.File(BACKUP_FILE_CLIENTE).delete();
    }

    @BeforeEach
    void limparBanco() {
        DatabaseJSON.salvarClientes(new JsonArray());
        System.setOut(new PrintStream(outputCapturado));
    }

    @BeforeEach
    void setUp() {
        cliente = new UserCliente(1, "cliente_teste", "teste@gmail.com", "1234", "67890123456");
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());
        clienteJson.addProperty("email", cliente.getEmail());
        clienteJson.addProperty("senha", cliente.getSenha());
        clienteJson.addProperty("cpf", cliente.getCpf());


        JsonObject carrinhoJson = new JsonObject();
        JsonArray itens = new JsonArray();

        JsonObject item1 = new JsonObject();
        item1.addProperty("nome", "Camiseta");
        item1.addProperty("valor", 50.0);
        item1.addProperty("tipo", "Vestuário");
        item1.addProperty("quantidade", 2);
        item1.addProperty("marca", "Marca X");
        item1.addProperty("descricao", "Camiseta de algodão");
        item1.addProperty("loja", "Loja 1");

        JsonObject item2 = new JsonObject();
        item2.addProperty("nome", "Tênis");
        item2.addProperty("valor", 200.0);
        item2.addProperty("tipo", "Calçado");
        item2.addProperty("quantidade", 1);
        item2.addProperty("marca", "Marca Y");
        item2.addProperty("descricao", "Tênis esportivo");
        item2.addProperty("loja", "Loja 2");

        itens.add(item1);
        itens.add(item2);

        carrinhoJson.add("itens", itens);
        clienteJson.add("carrinho", carrinhoJson);
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);
    }

    @AfterEach
    void restaurarEstadoOriginalEOutput() {
        System.setOut(outputOriginal);
    }

    @Test
    void testCadastrarCliente() {
        int id = ClienteDAO.cadastrarCliente("João", "joao@email.com", "senha123", "12345678900");
        assertTrue(id > 0, "O ID do cliente deve ser maior que zero");
        assertNotNull(ClienteDAO.buscarPorCpf("12345678900"), "O cliente deve estar cadastrado");
    }

    @Test
    void testCadastrarClienteCpfDuplicado() {
        ClienteDAO.cadastrarCliente("Maria", "maria@email.com", "senha456", "11122233344");
        int resultado = ClienteDAO.cadastrarCliente("Carlos", "carlos@email.com", "senha789", "11122233344");
        assertEquals(-2, resultado, "Deve retornar erro ao cadastrar CPF duplicado");
    }

    @Test
    void testBuscarPorEmail() {
        ClienteDAO.cadastrarCliente("Ana", "ana@email.com", "senhaABC", "55566677788");
        UserCliente cliente = ClienteDAO.buscarPorEmail("ana@email.com");
        assertNotNull(cliente, "Cliente deve ser encontrado pelo e-mail");
        assertEquals("Ana", cliente.getNome(), "O nome deve ser Ana");
    }

    @Test
    void testBuscarPorCpf() {
        ClienteDAO.cadastrarCliente("Ana", "ana@email.com", "senhaABC", "55566077788");
        UserCliente cliente = ClienteDAO.buscarPorCpf("55566077788");
        assertNotNull(cliente, "Cliente deve ser encontrado pelo CPF");
        assertEquals("Ana", cliente.getNome(), "O nome deve ser Ana");
    }

    @Test
    void testValidarLogin() {
        ClienteDAO.cadastrarCliente("Pedro", "pedro@email.com", "senhaXYZ", "99988877766");
        assertTrue(ClienteDAO.validarLogin("pedro@email.com", "senhaXYZ"), "Login deve ser válido");
        assertFalse(ClienteDAO.validarLogin("pedro@email.com", "senhaErrada"), "Senha incorreta deve falhar");
    }

    @Test
    void testAtualizarCliente() {
        int id = ClienteDAO.cadastrarCliente("Lucas", "lucas@email.com", "1234", "33322211100");
        UserCliente cliente = new UserCliente(id, "Lucas Silva", "lucas@email.com", "novaSenha", "33322211100");
        assertTrue(ClienteDAO.atualizar(cliente), "Atualização deve ser bem-sucedida");
        UserCliente atualizado = ClienteDAO.buscarPorCpf("33322211100");
        assertEquals("Lucas Silva", atualizado.getNome(), "O nome deve ser atualizado");
    }

    @Test
    void testRemoverCliente() {
        ClienteDAO.cadastrarCliente("Clara", "clara@email.com", "1234", "77788899900");
        assertTrue(ClienteDAO.remover("clara@email.com"), "Remoção deve ser bem-sucedida");
        assertNull(ClienteDAO.buscarPorEmail("clara@email.com"), "Cliente não deve mais existir");
    }

    @Test
    void testListarTodos() {
        ClienteDAO.cadastrarCliente("Carlos", "carlos@email.com", "abc", "11122233399");
        ClienteDAO.cadastrarCliente("Mariana", "mariana@email.com", "xyz", "22233344488");
        List<UserCliente> clientes = ClienteDAO.listarTodos();
        assertEquals(2, clientes.size(), "Devem existir 2 clientes cadastrados");
    }

    @Test
    void deveAdicionarProdutoNovoAoCarrinho() {
        // Cria cliente e adiciona ao "banco"
        JsonArray clientes = new JsonArray();
        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", 1);
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        // Cria objeto de domínio
        UserCliente cliente = new UserCliente(1, "Ana", "ana@email.com", "senhaABC", "55566077788");
        Produto produto = new Produto("Café", 15.0, "Bebida", 1, "Melitta", "Café em pó");

        boolean resultado = ClienteDAO.adicionarProdutoAoCarrinho(cliente, produto, 2);
        assertTrue(resultado);

        // Verifica se foi salvo corretamente
        JsonArray atualizado = DatabaseJSON.carregarClientes();
        JsonObject carrinho = atualizado.get(0).getAsJsonObject().getAsJsonObject("carrinho");
        JsonArray itens = carrinho.getAsJsonArray("itens");

        assertEquals(1, itens.size());
        JsonObject item = itens.get(0).getAsJsonObject();
        assertEquals("Café", item.get("nome").getAsString());
        assertEquals(2, item.get("quantidade").getAsInt());
    }

    @Test
    void deveRetornarFalseSeClienteNaoExiste() {
        // Garante que não há clientes
        DatabaseJSON.salvarClientes(new JsonArray());

        UserCliente cliente = new UserCliente(999, "Desconhecido", "desco@gmail.com", "1234", "12315649878");
        Produto produto = new Produto("Leite", 7.0, "Bebida", 1, "Parmalat", "Leite integral");

        boolean resultado = ClienteDAO.adicionarProdutoAoCarrinho(cliente, produto, 1);
        assertFalse(resultado);
    }

    @Test
    void deveExibirItensDoCarrinhoComSucesso() {
        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", 1);

        JsonObject item = new JsonObject();
        item.addProperty("nome", "Café");
        item.addProperty("valor", 15.0);
        item.addProperty("tipo", "Bebida");
        item.addProperty("quantidade", 2);
        item.addProperty("marca", "Melitta");
        item.addProperty("descricao", "Café em pó");
        item.addProperty("loja", "LojaX");

        JsonArray itens = new JsonArray();
        itens.add(item);

        JsonObject carrinho = new JsonObject();
        carrinho.add("itens", itens);

        clienteJson.add("carrinho", carrinho);

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        UserCliente cliente = new UserCliente(1, "João", "joao@gmail.com", "1233", "56789012340");
        boolean resultado = ClienteDAO.exibirItensCarrinho(cliente);

        assertTrue(resultado);

        String saida = outputCapturado.toString();
        assertTrue(saida.contains("Café (Bebida)"));
        assertTrue(saida.contains("Marca: Melitta"));
        assertTrue(saida.contains("Valor total do Carrinho: 30.0")); // 15 * 2
    }

    @Test
    void deveInformarCarrinhoVazioQuandoNaoHaItens() {
        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", 2);

        JsonObject carrinho = new JsonObject();
        carrinho.add("itens", new JsonArray()); // vazio

        clienteJson.add("carrinho", carrinho);

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        UserCliente cliente = new UserCliente(2, "Maria", "maria@gmail.com", "1234", "45678901230");
        boolean resultado = ClienteDAO.exibirItensCarrinho(cliente);

        assertFalse(resultado);
        String saida = outputCapturado.toString();
        assertTrue(saida.contains("Carrinho vazio."));
    }

    @Test
    void deveInformarCarrinhoNaoEncontrado() {
        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", 3);
        // Não adiciona "carrinho"

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        UserCliente cliente = new UserCliente(3, "Pedro", "pedro@gmail.com", "1123", "34567890121");
        boolean resultado = ClienteDAO.exibirItensCarrinho(cliente);

        assertFalse(resultado);
        String saida = outputCapturado.toString();
        assertTrue(saida.contains("Carrinho não encontrado"));
    }

    @Test
    void deveInformarClienteNaoEncontrado() {
        // Nenhum cliente cadastrado
        DatabaseJSON.salvarClientes(new JsonArray());

        UserCliente cliente = new UserCliente(999, "Fantasma", "fant@gmail.com", "1234", "23456789567");
        boolean resultado = ClienteDAO.exibirItensCarrinho(cliente);

        assertFalse(resultado);
        String saida = outputCapturado.toString();
        assertTrue(saida.contains("Cliente não encontrado."));
    }

    @Test
    void deveCalcularComValoresDecimaisPrecisos() {
        JsonArray itens = new JsonArray();

        JsonObject item = new JsonObject();
        item.addProperty("valor", 9.99);
        item.addProperty("quantidade", 3); // 9.99 * 3 = 29.97

        itens.add(item);

        double resultado = ClienteDAO.valorCarrinho(itens);

        assertEquals(29.97, resultado, 0.001);
    }

    @Test
    void deveRetornarZeroParaCarrinhoVazio() {
        JsonArray itens = new JsonArray();

        double resultado = ClienteDAO.valorCarrinho(itens);

        assertEquals(0.0, resultado);
    }

    @Test
    void deveCalcularValorCorretamenteComItem() {
        JsonArray itens = new JsonArray();

        JsonObject item = new JsonObject();
        item.addProperty("valor", 10.0);
        item.addProperty("quantidade", 3); // 10 * 3 = 30

        itens.add(item);

        double resultado = ClienteDAO.valorCarrinho(itens);

        assertEquals(30.0, resultado);
    }

    @Test
    void deveRemoverUmaUnidadeDeProdutoComMaisDeUmaQuantidade() {
        // Arrange
        cliente.setId(1); // ID consistente com o JSON
        JsonObject item = new JsonObject();
        item.addProperty("nome", "Arroz");
        item.addProperty("quantidade", 3);

        JsonArray itens = new JsonArray();
        itens.add(item);

        JsonObject carrinho = new JsonObject();
        carrinho.add("itens", itens);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("carrinho", carrinho);

        JsonArray clientesJson = new JsonArray();
        clientesJson.add(clienteJson);

        DatabaseJSON.salvarClientes(clientesJson);

        // Act
        boolean resultado = ClienteDAO.removerProdutoDoCarrinho(cliente, "Arroz");

        // Assert
        JsonArray resultadoClientes = DatabaseJSON.carregarClientes();
        JsonObject clienteAtualizado = resultadoClientes.get(0).getAsJsonObject();
        JsonArray itensAtualizados = clienteAtualizado
                .getAsJsonObject("carrinho")
                .getAsJsonArray("itens");

        JsonObject itemAtualizado = itensAtualizados.get(0).getAsJsonObject();
        int novaQuantidade = itemAtualizado.get("quantidade").getAsInt();

        assertTrue(resultado);
        assertEquals(2, novaQuantidade);
    }

    @Test
    void deveRemoverProdutoDoCarrinhoSeQuantidadeForIgualAUm() {
        // Arrange
        cliente.setId(2); // ID diferente para evitar conflito

        JsonObject item = new JsonObject();
        item.addProperty("nome", "Feijão");
        item.addProperty("quantidade", 1);

        JsonArray itens = new JsonArray();
        itens.add(item);

        JsonObject carrinho = new JsonObject();
        carrinho.add("itens", itens);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("carrinho", carrinho);

        JsonArray clientesJson = new JsonArray();
        clientesJson.add(clienteJson);

        DatabaseJSON.salvarClientes(clientesJson);

        // Act
        boolean resultado = ClienteDAO.removerProdutoDoCarrinho(cliente, "Feijão");

        // Assert
        JsonArray resultadoClientes = DatabaseJSON.carregarClientes();
        JsonObject clienteAtualizado = resultadoClientes.get(0).getAsJsonObject();
        JsonArray itensAtualizados = clienteAtualizado
                .getAsJsonObject("carrinho")
                .getAsJsonArray("itens");

        assertTrue(resultado);
        assertEquals(0, itensAtualizados.size()); // item foi removido
    }


    @Test
    void deveRetornarFalseSeProdutoNaoEstiverNoCarrinho() {
        boolean resultado = ClienteDAO.removerProdutoDoCarrinho(cliente, "Relógio");

        assertFalse(resultado);
    }

    @Test
    void deveExibirMensagemQuandoHistoricoEstiverVazio() {
        // Arrange
        cliente.setId(1);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("historicoCompras", new JsonArray());

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        // Captura saída do console
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        boolean resultado = ClienteDAO.exibirHistoricoCompras(cliente);

        // Assert
        String saida = outContent.toString().trim();
        assertTrue(resultado);
        assertTrue(saida.contains("Nenhuma compra realizada ainda."));

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void deveExibirHistoricoDeComprasQuandoExistente() {
        // Arrange
        UserCliente cliente = new UserCliente(2, "Cliente Com Compras", "comprador@email.com", "senha", "12343432531");
        cliente.setId(2);

        JsonObject compra = new JsonObject();
        compra.addProperty("produto", "Notebook");
        compra.addProperty("loja", "Tech Store");
        compra.addProperty("quantidade", 1);
        compra.addProperty("valor", 3500.00);

        JsonArray historico = new JsonArray();
        historico.add(compra);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("historicoCompras", historico);

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        // Captura saída do console
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Act
        boolean resultado = ClienteDAO.exibirHistoricoCompras(cliente);

        // Assert
        String saida = outContent.toString().trim();
        assertTrue(resultado);
        assertTrue(saida.contains("Histórico de Compras de Cliente Com Compras"));
        assertTrue(saida.contains("Produto: Notebook"));
        assertTrue(saida.contains("Loja: Tech Store"));
        assertTrue(saida.contains("Quantidade: 1"));
        assertTrue(saida.contains("Valor: R$ 3500.0"));

        // Reset System.out
        System.setOut(System.out);
    }

    @Test
    void deveAdicionarCompraAoHistoricoExistente() {
        // Arrange
        cliente.setId(1);

        JsonObject compraAnterior = new JsonObject();
        compraAnterior.addProperty("produto", "Teclado");
        compraAnterior.addProperty("quantidade", 1);
        compraAnterior.addProperty("loja", "Loja A");
        compraAnterior.addProperty("valor", 100.00);

        JsonArray historico = new JsonArray();
        historico.add(compraAnterior);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("historicoCompras", historico);

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        // Act
        boolean resultado = ClienteDAO.adicionarHistoricoCompra(cliente, "Mouse", 2, "Loja B", 150.00);

        // Assert
        assertTrue(resultado);

        JsonArray atualizado = DatabaseJSON.carregarClientes()
                .get(0).getAsJsonObject()
                .getAsJsonArray("historicoCompras");

        assertEquals(2, atualizado.size());

        JsonObject ultimaCompra = atualizado.get(1).getAsJsonObject();
        assertEquals("Mouse", ultimaCompra.get("produto").getAsString());
        assertEquals(2, ultimaCompra.get("quantidade").getAsInt());
        assertEquals("Loja B", ultimaCompra.get("loja").getAsString());
        assertEquals(150.00, ultimaCompra.get("valor").getAsDouble());
    }

    @Test
    void deveCriarHistoricoSeNaoExistir() {
        // Arrange
        cliente.setId(2);

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId()); // Sem "historicoCompras"

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        // Act
        boolean resultado = ClienteDAO.adicionarHistoricoCompra(cliente, "Notebook", 1, "Tech Store", 3500.00);

        // Assert
        assertTrue(resultado);

        JsonArray atualizado = DatabaseJSON.carregarClientes()
                .get(0).getAsJsonObject()
                .getAsJsonArray("historicoCompras");

        assertEquals(1, atualizado.size());

        JsonObject compra = atualizado.get(0).getAsJsonObject();
        assertEquals("Notebook", compra.get("produto").getAsString());
        assertEquals(1, compra.get("quantidade").getAsInt());
        assertEquals("Tech Store", compra.get("loja").getAsString());
        assertEquals(3500.00, compra.get("valor").getAsDouble());
    }

    @Test
    void deveFalharComCarrinhoVazio() {
        cliente.setId(2);

        JsonObject carrinho = new JsonObject();
        carrinho.add("itens", new JsonArray()); // Carrinho vazio

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.add("carrinho", carrinho);

        JsonArray clientes = new JsonArray();
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        Scanner scanner = new Scanner("s\n");

        boolean resultado = ClienteDAO.efetuarCompra(cliente, scanner);

        assertFalse(resultado);
    }

    @Test
    public void testClienteNaoExisteRetornaNull() {
        // Base está vazia, cliente não existe
        JsonArray itens = ClienteDAO.arrayItens(cliente);
        assertNull(itens);
    }

    @Test
    public void testClienteSemCarrinhoRetornaNull() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());

        // Sem carrinho
        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        JsonArray itens = ClienteDAO.arrayItens(cliente);
        assertNull(itens);
    }

    @Test
    public void testClienteComCarrinhoSemItensRetornaNull() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());

        JsonObject carrinho = new JsonObject();
        // carrinho sem "itens"
        clienteJson.add("carrinho", carrinho);

        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        JsonArray itens = ClienteDAO.arrayItens(cliente);
        assertNull(itens);
    }

    @Test
    public void testClienteNaoExisteRetornaFalse() {
        boolean resultado = ClienteDAO.limparCarrinho(cliente);
        assertFalse(resultado);
    }

    @Test
    public void testClienteSemCarrinhoRetornaFalse() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());
        // Sem carrinho

        clientes.add(clienteJson);
        DatabaseJSON.salvarClientes(clientes);

        boolean resultado = ClienteDAO.limparCarrinho(cliente);
        assertFalse(resultado);
    }

    @Test
    public void testClienteComCarrinhoLimpaItensERetornaTrue() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());

        JsonObject carrinho = new JsonObject();
        JsonArray itens = new JsonArray();

        JsonObject item1 = new JsonObject();
        item1.addProperty("nome", "Produto1");
        item1.addProperty("quantidade", 2);
        carrinho.add("itens", itens);
        itens.add(item1);

        clienteJson.add("carrinho", carrinho);
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        boolean resultado = ClienteDAO.limparCarrinho(cliente);
        assertTrue(resultado);

        // Verifica se os itens foram removidos
        JsonArray clientesAtualizados = DatabaseJSON.carregarClientes();
        JsonObject clienteAtualizado = null;
        for (JsonElement e : clientesAtualizados) {
            JsonObject c = e.getAsJsonObject();
            if (c.get("id").getAsInt() == cliente.getId()) {
                clienteAtualizado = c;
                break;
            }
        }
        assertNotNull(clienteAtualizado);
        JsonObject carrinhoAtualizado = clienteAtualizado.getAsJsonObject("carrinho");
        JsonArray itensAtualizados = carrinhoAtualizado.getAsJsonArray("itens");
        assertNotNull(itensAtualizados);
        assertEquals(0, itensAtualizados.size());
    }

    @Test
    public void testBuscarDetalheCompraValidoRetornaValor() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());

        JsonArray historicoCompras = new JsonArray();

        JsonObject compra1 = new JsonObject();
        compra1.addProperty("produto", "ProdutoA");
        compra1.addProperty("quantidade", 3);
        compra1.addProperty("loja", "LojaX");
        compra1.addProperty("valor", 150.0);

        historicoCompras.add(compra1);

        clienteJson.add("historicoCompras", historicoCompras);
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        String produto = ClienteDAO.buscarDetalheCompra("produto", 0, cliente);
        assertEquals("ProdutoA", produto);

        String loja = ClienteDAO.buscarDetalheCompra("loja", 0, cliente);
        assertEquals("LojaX", loja);
    }

    @Test
    public void testIndiceInvalidoLancaException() {
        JsonArray clientes = new JsonArray();

        JsonObject clienteJson = new JsonObject();
        clienteJson.addProperty("id", cliente.getId());
        clienteJson.addProperty("nome", cliente.getNome());

        JsonArray historicoCompras = new JsonArray();

        JsonObject compra1 = new JsonObject();
        compra1.addProperty("produto", "ProdutoA");
        historicoCompras.add(compra1);

        clienteJson.add("historicoCompras", historicoCompras);
        clientes.add(clienteJson);

        DatabaseJSON.salvarClientes(clientes);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            ClienteDAO.buscarDetalheCompra("produto", 5, cliente);
        });
    }
}

