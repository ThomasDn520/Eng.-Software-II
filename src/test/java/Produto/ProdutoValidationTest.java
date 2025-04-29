package Produto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoValidationTest {

    @Test
    void testValorNegativo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Produto Inválido", -50.0, "Eletrônico", 10, "Marca", "Descrição ok");
        });

        assertEquals("Valor do produto deve ser positivo", exception.getMessage());
    }

    @Test
    void testValorZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Produto Inválido", 0.0, "Eletrônico", 10, "Marca", "Descrição ok");
        });

        assertEquals("Valor do produto deve ser positivo", exception.getMessage());
    }

    @Test
    void testDescricaoMuitoLonga() {
        StringBuilder descricao = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            descricao.append("a");
        }

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Produto Inválido", 100.0, "Livro", 10, "Marca", descricao.toString());
        });

        assertEquals("Descrição muito longa", exception.getMessage());
    }

    @Test
    void testQuantidadeNegativa() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Produto Inválido", 100.0, "Roupa", -5, "Marca", "Descrição ok");
        });

        assertEquals("Quantidade em estoque não pode ser negativa", exception.getMessage());
    }

    @Test
    void testProdutoValidoDeveSerCriado() {
        Produto produto = new Produto("Livro de Java", 89.90, "Livro", 5, "Editora X", "Aprenda Java completo");

        assertNotNull(produto);
        assertEquals("Livro de Java", produto.getNome());
    }
}
