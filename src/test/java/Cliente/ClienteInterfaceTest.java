package Cliente;

import User.UserCliente;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClienteInterfaceTest {

    private ClienteInterface clienteInterface;
    private UserCliente cliente;

    private static final String FILE_NAME_CLIENTE = "database_cliente.json";
    private static final String BACKUP_FILE_CLIENTE = "clienteDB_backup.json";

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        clienteInterface = new ClienteInterface();
        cliente = new UserCliente(4, "João", "joao@email.com", "senha123", "12345678900");
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
    void testMenuCliente_Sair() {
        String inputSimulado = "0\n"; // Opção para sair
        System.setIn(new ByteArrayInputStream(inputSimulado.getBytes()));
        clienteInterface.scanner = new Scanner(System.in);
        clienteInterface.menuCliente(cliente);
        assertTrue(true); // Teste passa se não travar
    }

    @Test
    void testLoginCadastroCliente() {
        String entradaSimulada = "3\n4\n"; // Voltar ao menu e sair
        System.setIn(new ByteArrayInputStream(entradaSimulada.getBytes()));
        clienteInterface.scanner = new Scanner(System.in);
        clienteInterface.loginCadastroCliente();
        assertTrue(true);
    }

    @Test
    void testLoginClienteComFalhaDeAutenticacao() {
        StringBuilder entrada = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            entrada.append("invalido").append(i).append("@teste.com\n");
            entrada.append("senhaErrada").append(i).append("\n");
        }
        System.setIn(new ByteArrayInputStream(entrada.toString().getBytes()));
        clienteInterface.scanner = new Scanner(System.in);
        clienteInterface.loginCliente();
        assertTrue(true);
    }

    @Test
    void testDetalheCompra_AvaliarProduto() {
        String input = "1\n5\nComentário do produto\n0\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testDetalheCompra_AvaliarLoja() {
        String input = "2\n4\nComentário da loja\n0\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testDetalheCompra_EntradaInvalida() {
        String input = "abc\n0\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testDetalheCompra_OpcaoInvalida() {
        String input = "9\n0\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testAvaliarLoja_Sucesso() {
        String input = "4\nComentário positivo da loja\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        clienteInterface.avaliarLoja(cliente, 0);
        assertTrue(true);
    }

    @Test
    void testAvaliarLoja_CompraInexistente() {
        String input = "\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        clienteInterface.avaliarLoja(cliente, 999); // Índice inválido
        assertTrue(true);
    }

    @Test
    void testAvaliarProduto_Sucesso() {
        String input = "5\nComentário do produto\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        clienteInterface.avaliarProduto(cliente, 0);
        assertTrue(true);
    }

    @Test
    void testAvaliarProduto_CompraInexistente() {
        String input = "\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        clienteInterface.avaliarProduto(cliente, 999); // Índice inválido
        assertTrue(true);
    }
}
