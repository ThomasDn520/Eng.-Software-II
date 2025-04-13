package Loja;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import User.UserLoja;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LojaInterfaceTest {

    private LojaSystem lojaSystem;
    private LojaInterface lojaInterface;
    private Scanner scanner;
    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private static final String FILE_NAME_LOJA = "database_loja.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE_LOJA = "lojaDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        Scanner scanner = new Scanner(System.in);
        lojaSystem = new LojaSystem();
        lojaSystem = mock(LojaSystem.class);
        lojaInterface = new LojaInterface(scanner, lojaSystem);
        lojaInterface = Mockito.spy(new LojaInterface());

        // Redireciona a saída para capturar os prints
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        System.setIn(new ByteArrayInputStream("".getBytes()));
    }

    @AfterEach
    void tearDown() throws IOException {
        restaurarBackup();
        reset(lojaSystem);  // Reseta o mock para garantir que o estado do mock não seja compartilhado
        System.setOut(System.out);
        System.setIn(System.in);
    }

    private void criarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_LOJA);
        File backupFile = new File(BACKUP_FILE_LOJA);
        if (dbFile.exists()) {
            Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void restaurarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_LOJA);
        File backupFile = new File(BACKUP_FILE_LOJA);
        if (backupFile.exists()) {
            Files.copy(backupFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupFile.delete();
        }
    }

    @Test
    @Order(1)
    public void testEntradaInvalida() {
        LojaSystem lojaSystemReal = new LojaSystem();
        Scanner scanner = new Scanner(new ByteArrayInputStream("abc\n4\n".getBytes()));
        lojaInterface = new LojaInterface(scanner, lojaSystemReal);

        lojaInterface.menuLoja(new UserLoja(8, "Loja Teste", "email@teste.com", "123", "123"));

        String output = outputStream.toString();
        assertTrue(output.contains("Entrada inválida! Digite um número."));
        assertTrue(output.contains("Saindo..."));
    }

    @Test
    @Order(2)
    public void testOpcao1InformacoesLoja() {
        String input = "1\n4\n"; // Opção 1 + sair
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        lojaInterface.setScanner(new Scanner(System.in));

        lojaInterface.menuLoja(new UserLoja(4, "Loja Teste", "email@teste.com", "123", "123"));

        String output = outputStream.toString();
        assertTrue(output.contains("Função não implementada!"));
        assertTrue(output.contains("Saindo..."));
    }

    @Test
    @Order(3)
    public void testOpcao2AdicionarProduto() {
        String input = "2\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        lojaInterface.setScanner(new Scanner(System.in));

        lojaInterface.menuLoja(new UserLoja(5, "Loja Teste", "email@teste.com", "123", "123"));

        String output = outputStream.toString();
        assertTrue(output.contains("Função não implementada!"));
        assertTrue(output.contains("Saindo..."));
    }

    @Test
    @Order(4)
    public void testOpcao3AtualizarDados() {
        // Simulando a entrada: opção 3 (atualizar dados) e depois 4 (sair)
        String input = "3\n\n\n\n\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        Scanner scanner = new Scanner(System.in);

        // Criando uma instância real do LojaSystem
        LojaSystem lojaSystem = new LojaSystem();

        // Criando uma instância real do LojaInterface, com o LojaSystem real
        LojaInterface lojaInterface = new LojaInterface(scanner, lojaSystem);

        // Configurando a saída para capturar o que foi impresso
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);

        // Rodar o método que será testado
        lojaInterface.menuLoja(new UserLoja(6, "Loja Teste", "email@teste.com", "123", "123"));

        // Verificar se "Saindo..." foi impresso
        String output = outputStream.toString();
        assertTrue(output.contains("Saindo..."));
    }

    @Test
    @Order(5)
    public void testOpcao4Sair() {
        String input = "4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        lojaInterface.setScanner(new Scanner(System.in));

        lojaInterface.menuLoja(new UserLoja(7, "Loja Teste", "email@teste.com", "123", "123"));

        String output = outputStream.toString();
        assertTrue(output.contains("Saindo..."));
    }

    @Test
    @Order(6)
    void testLoginCadastroLoja() {
        // Definindo a entrada do Scanner
        String input = "1\nLoja_Teste\nloja@email.com\nsenha123\n12345678900001\n3\n4\n";
        Scanner scanner = new Scanner(input);
        LojaSystem lojaSystem = new LojaSystem();  // Suponha que LojaSystem seja um objeto real (não um mock)

        LojaInterface lojaInterface = new LojaInterface(scanner, lojaSystem);

        lojaInterface.loginCadastroLoja();
    }
}
