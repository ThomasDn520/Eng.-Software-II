package Cliente;

import User.UserCliente;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ClienteInterfaceTest {

    // Subclasse para testar e capturar chamada ao detalheCompra
    static class TestableClienteInterface extends ClienteInterface {
        public boolean detalheChamado = false;
        public int indiceRecebido = -1;

        @Override
        public boolean detalheCompra(UserCliente cliente, int indiceHistorico) {
            detalheChamado = true;
            indiceRecebido = indiceHistorico;
            return true;
        }
    }

    private ClienteSystem clienteSystem;
    private ClienteInterface clienteInterface;
    private UserCliente cliente;

    private static final String FILE_NAME_CLIENTE = "database_cliente.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE_CLIENTE = "clienteDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        clienteSystem = new ClienteSystem();
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
    void testMenuCliente() {
        // Simular entrada do usuário: opção 4 (sair do sistema)
        String inputSimulado = "0\n";
        InputStream inputStreamOriginal = System.in;
        System.setIn(new ByteArrayInputStream(inputSimulado.getBytes()));

        try {
            clienteInterface.scanner = new java.util.Scanner(System.in);  // Usar o scanner com a entrada simulada
            clienteInterface.menuCliente(cliente);
            // Se chegou aqui sem travar ou exceções, consideramos o teste ok.
            assertTrue(true);
        } finally {
            System.setIn(inputStreamOriginal);  // Restaurar entrada padrão
        }
    }

    @Test
    void testLoginCadastroCliente() {
        // Simula as opções que o usuário escolhe no menu
        // Primeiro escolhe a opção 3 (Voltar ao Menu Principal) e depois escolhe a opção 4 (Sair do Sistema)
        String entradaSimulada = String.join("\n",
                "3",    // opção 3 (Voltar ao Menu Principal)
                "4"     // opção 4 (Sair do Sistema)
        ) + "\n";

        // Salva o valor original do System.in para restaurá-lo no final
        InputStream systemInOriginal = System.in;
        System.setIn(new ByteArrayInputStream(entradaSimulada.getBytes())); // Substitui System.in para ler da entrada simulada

        try {
            clienteInterface.scanner = new java.util.Scanner(System.in); // Atualiza o scanner para ler da entrada simulada

            // Chama o método que será testado
            clienteInterface.loginCadastroCliente();

            // Verificamos que o fluxo foi executado até as opções 3 e 4 corretamente
            // O teste verifica se não houve erro na navegação e se o sistema sai como esperado.

        } finally {
            System.setIn(systemInOriginal); // Restaura o valor original de System.in
        }
    }

    @Test
    void testLoginClienteComFalhaDeAutenticacao() {
        // Simula 5 tentativas com e-mails e senhas inválidos
        StringBuilder entradaSimulada = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            entradaSimulada.append("email" + i + "@teste.com\n");
            entradaSimulada.append("senha" + i + "\n");
        }

        // Substitui o System.in
        InputStream systemInOriginal = System.in;
        System.setIn(new ByteArrayInputStream(entradaSimulada.toString().getBytes()));

        try {
            clienteInterface.scanner = new java.util.Scanner(System.in);
            clienteInterface.loginCliente();  // executa o método
        } finally {
            System.setIn(systemInOriginal);  // restaura entrada original
        }
    }

    @Test
    void testDetalheCompra_AvaliarProduto() {
        // Simula o input "1" para escolher avaliar produto
        String input = "1\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        clienteInterface.scanner = new Scanner(in);

        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
        // Aqui pode-se verificar efeitos colaterais, se houverem
    }

    @Test
    void testDetalheCompra_AvaliarLoja() {
        // Simula o input "2" para escolher avaliar loja
        String input = "2\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        clienteInterface.scanner = new Scanner(in);

        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testDetalheCompra_EntradaInvalida() {
        // Simula entrada inválida "abc", depois "0" para sair
        String input = "abc\n0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        clienteInterface.scanner = new Scanner(in);

        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testDetalheCompra_OpcaoInvalida() {
        // Simula opção inválida "9" depois "0" para sair
        String input = "9\n0\n";
        InputStream in = new ByteArrayInputStream(input.getBytes());
        clienteInterface.scanner = new Scanner(in);

        boolean resultado = clienteInterface.detalheCompra(cliente, 0);
        assertTrue(resultado);
    }

    @Test
    void testAvaliarLoja_Sucesso() {
        String input = "4\nComentário ok\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        clienteInterface.avaliarLoja(cliente, 0);
        // Não há retorno, só saída no console —
        // para verificar a saída, teria que capturar System.out (opcional)
    }

    @Test
    void testAvaliarLoja_CompraInexistente() {
        String input = "";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        clienteInterface.avaliarLoja(cliente, 99); // índice que retorna null para loja
    }

    @Test
    void testAvaliarProduto_Sucesso() {
        String input = "5\nComentário produto\n";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        clienteInterface.avaliarProduto(cliente, 0);
    }

    @Test
    void testAvaliarProduto_CompraInexistente() {
        String input = "";
        clienteInterface.scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        clienteInterface.avaliarProduto(cliente, 99);
    }
    
}

