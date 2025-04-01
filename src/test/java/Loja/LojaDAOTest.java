package Loja;

import User.UserLoja;
import Database.DatabaseJSON;
import com.google.gson.JsonArray;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LojaDAOTest {

    @BeforeEach
    void limparBanco() {
        DatabaseJSON.salvarLojas(new JsonArray()); // Reseta o banco antes de cada teste
    }

    @Test
    @Order(1)
    void testCadastrarLoja() {
        int id = LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        assertEquals(1, id);
    }

    @Test
    @Order(2)
    void testCadastrarLojaComCnpjDuplicado() {
        LojaDAO.cadastrarLoja("Loja A", "a@email.com", "1234", "12345678000199");
        int id = LojaDAO.cadastrarLoja("Loja B", "b@email.com", "1234", "12345678000199");
        assertEquals(-1, id);
    }

    @Test
    @Order(3)
    void testCadastrarLojaComEmailDuplicado() {
        LojaDAO.cadastrarLoja("Loja A", "a@email.com", "1234", "12345678000199");
        int id = LojaDAO.cadastrarLoja("Loja B", "a@email.com", "1234", "98765432000199");
        assertEquals(-2, id);
    }

    @Test
    @Order(4)
    void testValidarLoginCorreto() {
        LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        UserLoja loja = LojaDAO.validarLogin("teste@email.com", "1234");
        assertNotNull(loja);
    }

    @Test
    @Order(5)
    void testValidarLoginSenhaIncorreta() {
        LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        UserLoja loja = LojaDAO.validarLogin("teste@email.com", "senhaErrada");
        assertNull(loja);
    }

    @Test
    @Order(6)
    void testBuscarPorCnpj() {
        LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        UserLoja loja = LojaDAO.buscarPorCnpj("12345678000199");
        assertNotNull(loja);
        assertEquals("Loja Teste", loja.getNome());
    }

    @Test
    @Order(7)
    void testAtualizarLoja() {
        int id = LojaDAO.cadastrarLoja("Loja Antiga", "antiga@email.com", "1234", "12345678000199");
        UserLoja novaLoja = new UserLoja(id, "Loja Nova", "nova@email.com", "5678", "12345678000199");
        boolean atualizado = LojaDAO.atualizar(novaLoja);
        assertTrue(atualizado);
        UserLoja lojaAtualizada = LojaDAO.buscarPorCnpj("12345678000199");
        assertEquals("Loja Nova", lojaAtualizada.getNome());
    }

    @Test
    @Order(8)
    void testRemoverLoja() {
        LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        boolean removido = LojaDAO.removerLoja("12345678000199");
        assertTrue(removido);
        assertNull(LojaDAO.buscarPorCnpj("12345678000199"));
    }
}
