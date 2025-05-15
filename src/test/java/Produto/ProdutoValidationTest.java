package Produto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoValidationTest {

    @Test
    void testProdutoValidoDeveSerCriado() {
        Produto produto = new Produto("Livro de Java", 89.90, "Livro", 5, "Editora X", "Aprenda Java completo");

        assertNotNull(produto);
        assertEquals("Livro de Java", produto.getNome());
    }
}
