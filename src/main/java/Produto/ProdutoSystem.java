package Produto;

import java.util.List;
import java.util.Scanner;

public class ProdutoSystem {
// ALERLTA,TEM FUNÇÕES AQUI QUE PRECISAM DA IMPLEMENTAÇÃO NO PRODUTO NAO, TODAS QUE TIVEREM "Produto.DAO.*nome dela
    Scanner scanner = new Scanner(System.in);
    private void cadastrarProduto() {

        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Valor: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Tipo: ");
        String tipo = scanner.nextLine();
        System.out.print("Quantidade: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Marca: ");
        String marca = scanner.nextLine();
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        Produto produto = new Produto(nome, valor, tipo, quantidade, marca, descricao);
        ProdutoDAO.adicionarProduto(produto);
        System.out.println("Produto cadastrado com sucesso!");
    }

    private void listarProdutos() {
        List<Produto> produtos = ProdutoDAO.listarProdutos();
        if (produtos.isEmpty()) {
           // System.out.println("Nenhum produto cadastrado.");
        } else {
            for (Produto p : produtos) {
                //System.out.println(p);
           }
       }
   }

    private void editarProduto() {
        listarProdutos();
        System.out.print("Informe o nome do produto que deseja editar: ");
        String nome = scanner.nextLine();
        ProdutoDAO.editarProduto(nome, scanner);
    }

    private void removerProduto() {
        listarProdutos();
        System.out.print("Informe o nome do produto que deseja remover: ");
        String nome = scanner.nextLine();
        ProdutoDAO.removerProduto(nome);
    }

    private void buscarProduto() {
        System.out.print("Informe o nome do produto para buscar: ");
        String nome = scanner.nextLine();
        Produto produto = ProdutoDAO.buscarProduto(nome);
        if (produto != null) {
            System.out.println(produto);
        } else {
            System.out.println("Produto não encontrado.");
        }
    }


}
