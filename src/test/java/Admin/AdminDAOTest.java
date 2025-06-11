package Admin;

import static Database.DatabaseJSON.carregarAdmins;
import static org.junit.jupiter.api.Assertions.*;

import User.UserAdmin;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import com.google.gson.*;

class AdminDAOTest {

    private static final String FILE_NAME_ADMIN = "database.json"; // Banco de dados JSON original
    private static final String BACKUP_FILE = "adminsDB_backup.json"; // Arquivo de backup temporário

    @BeforeEach
    void setUp() throws IOException {
        criarBackup();
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
    void testCadastrarAdminSucesso() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        int id = adminDAO.cadastrarAdmin("Novo Admin", "novo@admin.com", "senha123");
        assertTrue(id > 0, "O ID retornado deve ser maior que zero");

        JsonArray admins = carregarAdmins();
        assertEquals(1, admins.size(), "Deve haver um administrador no arquivo");

        JsonObject admin = admins.get(0).getAsJsonObject();
        assertEquals("Novo Admin", admin.get("nome").getAsString());
        assertEquals("novo@admin.com", admin.get("email").getAsString());
        assertEquals("senha123", admin.get("senha").getAsString());
    }

    @Test
    void testCadastrarAdminComEmailExistente() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        adminDAO.cadastrarAdmin("Admin Existente", "admin@exemplo.com", "senha123");
        int novoId = adminDAO.cadastrarAdmin("Outro Admin", "admin@exemplo.com", "senha456");
        assertEquals(-1, novoId, "Deveria retornar -1, já que o email já está cadastrado");
    }

    @Test
    void testValidarLoginComSucesso() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        int id = adminDAO.cadastrarAdmin("Admin Teste", "admin@teste.com", "senha123");
        assertTrue(adminDAO.validarLogin(id, "senha123"));
    }

    @Test
    void testValidarLoginComFalha() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        int id = adminDAO.cadastrarAdmin("Admin Teste", "admin@teste.com", "senha123");
        assertFalse(adminDAO.validarLogin(id, "senhaErrada"));
    }

    @Test
    void testBuscarPorId() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        int id = adminDAO.cadastrarAdmin("Admin Teste", "admin@teste.com", "senha123");
        UserAdmin admin = adminDAO.buscarPorId(id);
        assertNotNull(admin);
        assertEquals("Admin Teste", admin.getNome());
    }

    @Test
    void testBuscarPorIdAdminNaoExistente() {
        AdminDAO adminDAO = new AdminDAO();
        assertNull(adminDAO.buscarPorId(999));
    }

    @Test
    void testBuscarPorEmail() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        adminDAO.cadastrarAdmin("Admin Teste", "admin@teste.com", "senha123");
        assertNotNull(adminDAO.buscarPorEmail("admin@teste.com"));
    }

    @Test
    void testBuscarPorEmailAdminNaoExistente() {
        AdminDAO adminDAO = new AdminDAO();
        assertNull(adminDAO.buscarPorEmail("inexistente@teste.com"));
    }

    @Test
    void testListarTodos() throws IOException {
        AdminDAO adminDAO = new AdminDAO();
        adminDAO.cadastrarAdmin("Admin Existente2", "admin2@exemplo.com", "senha2123");
        adminDAO.cadastrarAdmin("Admin Existente3", "admin3@exemplo.com", "senha3123");
        List<UserAdmin> listaAdmins = adminDAO.listarTodos();
        assertEquals(2, listaAdmins.size());
    }
}
