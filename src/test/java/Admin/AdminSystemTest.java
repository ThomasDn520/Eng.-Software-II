package Admin;

import Cliente.ClienteDAO;
import Loja.LojaDAO;
import User.UserAdmin;
import User.UserCliente;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static Database.DatabaseJSON.carregarAdmins;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

class AdminSystemTest {
    private AdminSystem adminSystem;

    private static final String FILE_NAME_ADMIN = "database.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE = "adminsDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
        adminSystem = new AdminSystem();
    }

    @AfterEach
    void tearDown() throws IOException {
        restaurarBackup();
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
    void testCriarAdmin() {
        Scanner scanner = new Scanner("Admin Teste\nadmin@email.com\n1234\n");
        adminSystem.criarAdmin(scanner);
        List<UserAdmin> admins = AdminSystem.listarAdmins();
        assertFalse(admins.isEmpty());
    }

    @Test
    void testCriarAdminDiretamente() {
        adminSystem.criarAdminDiretamente("admin3", "admin3@gmail.com", "1234");
        List<UserAdmin> admins = AdminSystem.listarAdmins();
        assertFalse(admins.isEmpty());
    }

    @Test
    void testAutenticarAdmin() {
        Scanner scanner = new Scanner("admin5\nadmin5@gmail.com\n1234\n");
        JsonArray adminsBefore = carregarAdmins();
        adminSystem.criarAdmin(scanner);
        JsonArray adminsAfter = carregarAdmins();
        JsonObject admin = adminsAfter.get(adminsAfter.size() - 1).getAsJsonObject();
        Scanner scanner2 = new Scanner(admin.get("id").getAsInt() + "\n" + "1234\n");
        UserAdmin adminAutenticado = adminSystem.autenticarAdmin(scanner2);
        assertNotNull(adminAutenticado);
    }

    @Test
    void testListarAdmins() {
        List<UserAdmin> admins = AdminSystem.listarAdmins();
        assertNotNull(admins);
    }

    @Test
    void testListarClientes() {
        List<UserCliente> clientes = adminSystem.listarClientes();
        assertNotNull(clientes);
    }

    @Test
    void testListarLojas() {
        List<UserLoja> lojas = adminSystem.listarLojas();
        assertNotNull(lojas);
    }

    @Test
    void testRemoverCliente() {
        // Criar um cliente de teste
        String emailTeste = "cliente_teste@email.com";
        ClienteDAO.cadastrarCliente("Cliente Teste", emailTeste, "123.456.789-00", "senha123");

        // Verificar se o cliente foi cadastrado
        List<UserCliente> clientesAntes = ClienteDAO.listarTodos();
        boolean clienteExisteAntes = clientesAntes.stream().anyMatch(c -> c.getEmail().equals(emailTeste));
        assertTrue(clienteExisteAntes, "O cliente deveria existir antes da remoção.");

        // Remover o cliente
        boolean removido = ClienteDAO.remover(emailTeste);
        assertTrue(removido, "A remoção do cliente deveria retornar true.");

        // Verificar se o cliente não está mais na lista
        List<UserCliente> clientesDepois = ClienteDAO.listarTodos();
        boolean clienteExisteDepois = clientesDepois.stream().anyMatch(c -> c.getEmail().equals(emailTeste));
        assertFalse(clienteExisteDepois, "O cliente não deveria mais existir após a remoção.");
    }

    @Test
    void testRemoverLoja() {
        // Criar uma loja de teste
        String cnpjTeste = "12.345.678/0001-99";
        LojaDAO.cadastrarLoja("Loja Teste", "loja@email.com", "senha123", cnpjTeste);

        // Verificar se a loja foi cadastrada
        List<UserLoja> lojasAntes = LojaDAO.listarTodas();
        boolean lojaExisteAntes = lojasAntes.stream().anyMatch(l -> l.getCnpj().equals(cnpjTeste));
        assertTrue(lojaExisteAntes, "A loja deveria existir antes da remoção.");

        // Remover a loja
        boolean removida = LojaDAO.removerLoja(cnpjTeste);
        assertTrue(removida, "A remoção da loja deveria retornar true.");

        // Verificar se a loja não está mais na lista
        List<UserLoja> lojasDepois = LojaDAO.listarTodas();
        boolean lojaExisteDepois = lojasDepois.stream().anyMatch(l -> l.getCnpj().equals(cnpjTeste));
        assertFalse(lojaExisteDepois, "A loja não deveria mais existir após a remoção.");
    }
}

