package Carrinho;

import Carrinho.CarrinhoCompras;
import Carrinho.ItemCarrinho;
import Produto.Produto;
import User.UserCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CarrinhoComprasTest {

    private CarrinhoCompras carrinho;
    private Produto produto1;
    private Produto produto2;
    private UserCliente cliente;

    @BeforeEach
    public void setUp() {
        cliente = new UserCliente(8, "João", "joao@email.com", "senha5874", "13524552651");
        carrinho = new CarrinhoCompras(cliente);

        produto1 = new Produto("Arroz", 10.0, "Arroz branco tipo 1", 100, "Alimentos", "Tio João");
        produto2 = new Produto("Feijão", 7.5, "Feijão carioca", 80, "Alimentos", "Camil");

    }

    @Test
    public void testAdicionarItemNovo() {
        ItemCarrinho item = new ItemCarrinho(produto1, 1);
        boolean adicionado = carrinho.adicionarItem(item);

        assertTrue(adicionado);
        assertEquals(1, carrinho.getItens().size());
        assertEquals("Arroz", carrinho.getItens().get(0).getNome().getNome());
    }

    @Test
    public void testAdicionarItemRepetidoAumentaQuantidade() {
        carrinho.adicionarItem(new ItemCarrinho(produto1, 1));
        carrinho.adicionarItem(new ItemCarrinho(produto1, 1));

        ItemCarrinho item = carrinho.getItem(produto1);

        assertNotNull(item);
        assertEquals(2, item.getQuantidade());
        assertEquals(1, carrinho.getItens().size());
    }

    @Test
    public void testTemItemInexistente() {
        assertFalse(carrinho.temItem(produto1));
    }

    @Test
    public void testGetItemExistente() {
        ItemCarrinho item = new ItemCarrinho(produto1, 3);
        carrinho.adicionarItem(item);

        ItemCarrinho encontrado = carrinho.getItem(produto1);
        assertNotNull(encontrado);
        assertEquals(3, encontrado.getQuantidade());
    }

    @Test
    public void testGetItemInexistente() {
        assertNull(carrinho.getItem(produto1));
    }

    @Test
    public void testGetCliente() {
        assertEquals(cliente, carrinho.getCliente());
    }
}


