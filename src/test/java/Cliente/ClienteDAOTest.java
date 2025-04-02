package Cliente;

import User.UserCliente;
import Database.DatabaseJSON;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClienteDAOTest {

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
}

