package Loja;

import User.UserLoja;
import Database.DatabaseJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LojaDAOTest {


    @BeforeEach
    void setup() {
        // Preparar um JSON inicial com uma loja sem avaliações
        JsonArray lojas = new JsonArray();
        JsonObject loja = new JsonObject();
        loja.addProperty("id", 1);
        loja.addProperty("nome", "Loja Teste");
        loja.addProperty("email", "loja@teste.com");
        loja.addProperty("senha", "1234");
        loja.addProperty("cnpj", "123456789");

        lojas.add(loja);
        DatabaseJSON.salvarLojas(lojas);
    }

    @Test
    @Order(1)
    void testCadastrarLoja() {
        int id = LojaDAO.cadastrarLoja("Loja Teste", "teste@email.com", "1234", "12345678000199");
        assertEquals(2, id);
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

    @Test
    @Order(9)
    void testAdicionarAvaliacaoLojaComSucesso() {
        boolean resultado = LojaDAO.adicionarAvaliacaoLoja(101, "Loja Teste", 5, "Muito bom!");
        assertTrue(resultado, "A avaliação deveria ser adicionada com sucesso");

        JsonArray lojas = DatabaseJSON.carregarLojas();
        JsonObject loja = lojas.get(0).getAsJsonObject();
        JsonArray avaliacoes = loja.getAsJsonArray("avaliacoes");

        assertNotNull(avaliacoes, "A lista de avaliações não deveria ser nula");
        assertEquals(1, avaliacoes.size(), "Deveria ter 1 avaliação");

        JsonObject avaliacao = avaliacoes.get(0).getAsJsonObject();
        assertEquals(101, avaliacao.get("idCliente").getAsInt());
        assertEquals(5, avaliacao.get("nota").getAsInt());
        assertEquals("Muito bom!", avaliacao.get("comentario").getAsString());
    }

    @Test
    @Order(10)
    void testAdicionarAvaliacaoLojaNaoEncontrada() {
        boolean resultado = LojaDAO.adicionarAvaliacaoLoja(102, "Loja Inexistente", 4, "Bom");
        assertFalse(resultado, "Não deveria adicionar avaliação para loja inexistente");
    }

    @Test
    @Order(11)
    void testObterNotaEConceitoLojaSemAvaliacoes() {
        String resultado = LojaDAO.obterNotaEConceitoLoja("Loja Teste");
        assertEquals("Esta loja ainda não possui avaliações.", resultado);
    }

    @Test
    @Order(12)
    void testObterNotaEConceitoLojaComAvaliacoes() {
        // Primeiro adicionamos avaliações
        LojaDAO.adicionarAvaliacaoLoja(101, "Loja Teste", 5, "Excelente");
        LojaDAO.adicionarAvaliacaoLoja(102, "Loja Teste", 4, "Bom");

        String resultado = LojaDAO.obterNotaEConceitoLoja("Loja Teste");
        assertTrue(resultado.contains("Nota média"), "Deveria retornar a nota média");
        assertTrue(resultado.contains("Excelente") || resultado.contains("Bom"),
                "Deveria classificar corretamente o conceito");
    }

    @Test
    @Order(13)
    void testObterNotaEConceitoLojaNaoEncontrada() {
        String resultado = LojaDAO.obterNotaEConceitoLoja("Loja Inexistente");
        assertEquals("Loja não encontrada.", resultado);
    }

    @Test
    @Order(14)
    void testListarTodas() {
        List<UserLoja> lojas = LojaDAO.listarTodas();
        assertNotNull(lojas, "A lista de lojas não deveria ser nula");
        assertEquals(1, lojas.size(), "Deveria conter 1 loja cadastrada");

        UserLoja loja = lojas.get(0);
        assertEquals(1, loja.getId());
        assertEquals("Loja Teste", loja.getNome());
        assertEquals("loja@teste.com", loja.getEmail());
        assertEquals("1234", loja.getSenha());
        assertEquals("123456789", loja.getCnpj());
    }
}
