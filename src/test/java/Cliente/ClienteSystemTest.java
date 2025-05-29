package Cliente;

import User.UserCliente;
import Produto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteSystemTest {
    private ClienteSystem clienteSystem;

    private static final String FILE_NAME_CLIENTE = "database_cliente.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE_CLIENTE = "clienteDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        clienteSystem = new ClienteSystem();
    }

    @AfterEach
    void tearDown() throws IOException {
        restaurarBackup();
    }

    private void criarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_CLIENTE);
        File backupFile = new File(BACKUP_FILE_CLIENTE);
        if (dbFile.exists()) {
            Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void restaurarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_CLIENTE);
        File backupFile = new File(BACKUP_FILE_CLIENTE);
        if (backupFile.exists()) {
            Files.copy(backupFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupFile.delete();
        }
    }

    @Test
    void testCriarCliente() {
        // Criando um cliente
        ClienteSystem.criarCliente("João Silva", "joao@email.com", "senha123", "123.456.789-00");

        // Verificando se o cliente foi cadastrado corretamente
        UserCliente cliente = ClienteDAO.buscarPorEmail("joao@email.com");
        assertNotNull(cliente, "O cliente deveria ter sido cadastrado.");
        assertEquals("João Silva", cliente.getNome());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("123.456.789-00", cliente.getCpf());
    }

    @Test
    void testAutenticarCliente() {
        // Criar cliente para testar autenticação
        ClienteSystem.criarCliente("Maria Souza", "maria@email.com", "senhaSegura", "987.654.321-00");

        // Testando login com credenciais corretas
        UserCliente cliente = ClienteSystem.autenticarCliente("maria@email.com", "senhaSegura");
        assertNotNull(cliente, "O cliente deveria ser autenticado com sucesso.");
        assertEquals("Maria Souza", cliente.getNome());

        // Testando login com senha errada
        UserCliente clienteInvalido = ClienteSystem.autenticarCliente("maria@email.com", "senhaErrada");
        assertNull(clienteInvalido, "O cliente não deveria ser autenticado com senha errada.");
    }

    @Test
    void testAtualizarClienteMantemDadosSeNaoFornecidoNovo() {
        // Simular entrada do usuário (pressionando "Enter" para manter os dados antigos)
        String input = "\n\n\n\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Criar cliente com dados iniciais
        UserCliente cliente = new UserCliente(1, "João", "joao@email.com", "senha123", "123.456.789-00");

        ClienteSystem.atualizarCliente(new Scanner(System.in), cliente);

        // Verificar se os dados foram mantidos
        assertEquals("João", cliente.getNome());
        assertEquals("joao@email.com", cliente.getEmail());
        assertEquals("senha123", cliente.getSenha());
        assertEquals("123.456.789-00", cliente.getCpf());
    }

    @Test
    void testAtualizarClienteComNovosDados() {
        // Simular entrada do usuário com novos dados
        String input = "Carlos\ncarlos@email.com\nnovaSenha123\n987.654.321-00\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Criar cliente com dados iniciais
        UserCliente cliente = new UserCliente(2, "Maria", "maria@email.com", "senhaAntiga", "111.222.333-44");

        ClienteSystem.atualizarCliente(new Scanner(System.in), cliente);

        // Verificar se os dados foram atualizados corretamente
        assertEquals("Carlos", cliente.getNome());
        assertEquals("carlos@email.com", cliente.getEmail());
        assertEquals("novaSenha123", cliente.getSenha());
        assertEquals("987.654.321-00", cliente.getCpf());
    }

    @Test
    void testRemoverProduto_ComProdutoNoCarrinho() {
        ClienteDAO.cadastrarCliente("Pedro", "pedro@email.com", "senhaXYZ", "99988877766");
        UserCliente cliente = new UserCliente(1, "Pedro", "pedro@email.com", "senhaXYZ", "99988877766");
        cliente.setId(1);
        Produto produto = new Produto("Produto Teste", 23.50, "Tipo", 10, "Marca", "Descrição");

        ClienteDAO.adicionarProdutoAoCarrinho(cliente, produto, 1);

        boolean resultado = ClienteDAO.removerProdutoDoCarrinho(cliente, "Produto Teste");
        assertTrue(resultado);
    }

    @Test
    void testRemoverProduto_CarrinhoVazio() {
        UserCliente cliente = new UserCliente(5, "usuarioTeste", "Test@gmail.com", "11234", "24332454651");

        boolean resultado = ClienteDAO.removerProdutoDoCarrinho(cliente, "Produto Inexistente");
        assertFalse(resultado);
    }

    @Test
    void testBuscarProdutoPorNome_ProdutoNaoEncontrado() {
        Produto[] produtos = {
                new Produto("Produto Teste", 30.5, "Eletrônico", 2, "Marca", "Descrição"),
        };
        ProdutoDAO.setProdutos(produtos); // precisa simular isso

        String entrada = "Produto Inexistente\nLoja Inexistente\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(entrada.getBytes()));

        UserCliente cliente = new UserCliente(5, "usuarioTeste", "Test@gmail.com", "11234", "24332454651");

        boolean resultado = ClienteSystem.buscarProdutoPorNome(cliente, scanner, "inexistente");

        assertFalse(resultado);
    }
}

