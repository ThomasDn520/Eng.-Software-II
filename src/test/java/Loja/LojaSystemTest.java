package Loja;

import Cliente.ClienteSystem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import User.UserLoja;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class LojaSystemTest {
    private ClienteSystem clienteSystem;

    private static final String FILE_NAME_LOJA = "database_loja.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE_LOJA = "lojaDB_backup.json"; // Arquivo de backup temporário

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
    void testCriarLoja() {
        String nome = "Nova Loja";
        String email = "novaloja" + System.currentTimeMillis() + "@exemplo.com";
        String senha = "senha123";
        String cnpj = "123456789000" + (int)(Math.random() * 100); // Garante CNPJ único

        // Executa o método
        LojaSystem.criarLoja(nome, email, senha, cnpj);

        // Se não lançar exceção, consideramos OK (validação mínima)
        // Opcional: verificar se loja foi persistida
        UserLoja loja = Loja.LojaDAO.buscarPorEmail(email);
        assertNotNull(loja);
        assertEquals(nome, loja.getNome());
    }

    @Test
    void testAutenticarLoja() {
        String email = "lojaautenticar@teste.com";
        String senha = "senha456";

        // Garante que a loja existe
        LojaSystem.criarLoja("Loja Autenticar", email, senha, "98765432100098");

        LojaSystem lojaSystem = new LojaSystem();
        UserLoja loja = lojaSystem.autenticarLoja(email, senha);

        assertNotNull(loja);
        assertEquals(email, loja.getEmail());
    }


    @Test
    void testAtualizarLojaComDadosNovos() {
        // Simula entrada de novos dados: nome, email, senha, CNPJ
        String input = "Nova Loja\nnova@loja.com\nnovasenha\n11222333444455\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        // Cria uma loja de exemplo
        UserLoja loja = new UserLoja(1, "Loja Antiga", "antiga@loja.com", "senhavelha", "99887766554433");

        // Chama o método real
        LojaSystem.atualizarLoja(scanner, loja);

        // Verifica se os dados foram atualizados no objeto (não no banco)
        assertEquals("Nova Loja", loja.getNome());
        assertEquals("nova@loja.com", loja.getEmail());
        assertEquals("novasenha", loja.getSenha());
        assertEquals("11222333444455", loja.getCnpj());
    }
}

