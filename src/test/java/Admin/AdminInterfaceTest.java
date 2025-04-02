package Admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class AdminInterfaceTest {
    private AdminInterface adminInterface;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private static final String FILE_NAME_ADMIN = "database.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE = "adminsDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        adminInterface = new AdminInterface();
        System.setOut(new PrintStream(outputStreamCaptor)); // Captura saída do console
    }

    @AfterEach
    void tearDown() throws IOException {
        restaurarBackup();
        System.setOut(originalOut); // Restaura saída original
    }

    private void criarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_ADMIN);
        File backupFile = new File(BACKUP_FILE);
        if (dbFile.exists()) {
            Files.copy(dbFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void restaurarBackup() throws IOException {
        File dbFile = new File(FILE_NAME_ADMIN);
        File backupFile = new File(BACKUP_FILE);
        if (backupFile.exists()) {
            Files.copy(backupFile.toPath(), dbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            backupFile.delete();
        }
    }

    @Test
    void testCriarAdmTeste() {
        adminInterface.criarAdmTeste();

        // Verifica se pelo menos 2 admins foram criados
        assertTrue(AdminDAO.listarTodos().size() >= 2, "Deve haver pelo menos 2 administradores no sistema");
    }

    @Test
    void testLerIdAdminEntradaValida() {
        // Simula entrada de "123" no Scanner
        ByteArrayInputStream input = new ByteArrayInputStream("123\n".getBytes());
        adminInterface.setScanner(new Scanner(input));

        int id = adminInterface.lerIdAdmin(new Scanner(input));
        assertEquals(123, id, "O ID lido deve ser 123.");
    }

    @Test
    void testLerIdAdminEntradaInvalida() {
        // Simula entrada inválida ("abc") seguida de uma válida ("456")
        ByteArrayInputStream input = new ByteArrayInputStream("abc\n456\n".getBytes());
        adminInterface.setScanner(new Scanner(input));

        int id = adminInterface.lerIdAdmin(new Scanner(input));
        assertEquals(456, id, "O ID lido deve ser 456 após a entrada inválida.");

        // Verifica se a mensagem de erro foi exibida
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Erro: Digite um número válido!"), "Deve exibir erro para entrada inválida.");
    }
}