package Carrinho;

import Produto.Produto;
import User.UserCliente;

import java.util.ArrayList;


public class CarrinhoCompras {
    private UserCliente cliente;
    private ArrayList<ItemCarrinho> itens;

    public CarrinhoCompras(UserCliente cliente) {
        this.cliente= cliente;
        this.itens = new ArrayList<>();
    }

    public UserCliente getCliente() { return cliente; }
    public ArrayList<ItemCarrinho> getItens() { return itens; }

    public ItemCarrinho getItem(Produto produto) {
        for (ItemCarrinho item : itens) {
            if (item.getNome().equals(produto)) {
                return item;
            }
        }
        return null;
    }

    public boolean adicionarItem(ItemCarrinho produto) {
        for (ItemCarrinho item : itens) {
            if (item.getNome().equals(produto.getNome())) {
                item.setQuantidade(item.getQuantidade() + 1);
                return true;
            }
        }
        itens.add(produto);
        return true;
    }

    public boolean temItem(Produto produto) {
        for (ItemCarrinho item : itens) {
            if (item.getNome().equals(produto.getNome())) {
                return true;
            }
        }
        return false;
    }

}
