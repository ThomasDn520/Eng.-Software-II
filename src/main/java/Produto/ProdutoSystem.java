package Produto;

import User.User;
import User.UserLoja;

import java.util.List;
import java.util.Scanner;

public class ProdutoSystem {

        protected static Scanner scanner = new Scanner(System.in);

        public static void iniciar(UserLoja loja) {
            while (true) {
                System.out.println("\n=== MENU PRODUTOS ===");
                System.out.println("1. Cadastrar Produto");
                System.out.println("2. Listar Produtos");
                System.out.println("3. Editar Produto");
                System.out.println("4. Remover Produto");
                System.out.println("5. Buscar Produto");
                System.out.println("6. Avaliar Produto");
                System.out.println("0. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao;
                try {
                    opcao = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Opção inválida! Digite um número.");
                    continue;
                }

                switch (opcao) {
                    case 1 -> cadastrarProduto(loja);
                    case 2 -> listarProdutos(loja);
                    case 4 -> removerProduto(loja);
                    case 5 -> buscarProduto(loja);
                    case 6 -> avaliarProduto(loja);
                    case 0 -> {
                        System.out.println("Saindo...");
                        return;
                    }
                    default -> System.out.println("Opção inválida! Tente novamente.");
                }
            }
        }

        protected static void cadastrarProduto(UserLoja loja) {
            System.out.print("Nome: ");
            String nome = scanner.nextLine();

            System.out.print("Valor: ");
            double valor;
            try {
                valor = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido! Cadastro cancelado.");
                return;
            }

            System.out.print("Tipo: ");
            String tipo = scanner.nextLine();

            System.out.print("Quantidade: ");
            int quantidade;
            try {
                quantidade = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Quantidade inválida! Cadastro cancelado.");
                return;
            }

            System.out.print("Marca: ");
            String marca = scanner.nextLine();

            System.out.print("Descrição: ");
            String descricao = scanner.nextLine();

            Produto produto = new Produto(nome, valor, tipo, quantidade, marca, descricao);
            produto.setLoja(loja.getNome());
            ProdutoDAO.adicionarProduto(loja, produto);
            System.out.println("Produto cadastrado com sucesso!");
        }

        protected static void listarProdutos(UserLoja loja) {
            List<Produto> produtos = ProdutoDAO.listarProdutos(loja);
            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado.");
            } else {
                System.out.println("\n=== LISTA DE PRODUTOS ===");
                for (Produto p : produtos) {
                    System.out.println(p.getNome() + "| R$" + p.getValor() + ",00 | " + p.getTipo() + "| " + p.getMarca());
                    System.out.println("Descricao: " + p.getDescricao());
                    System.out.println("Quantidade: " + p.getQuantidade());
                    System.out.println("---------------------");
                }
            }
        }

        protected static void removerProduto(UserLoja loja) {
            listarProdutos(loja);
            System.out.print("Informe o nome do produto que deseja remover: ");
            String nome = scanner.nextLine();
            ProdutoDAO.removerProduto(loja, nome);
        }

        protected static void buscarProduto(UserLoja loja) {
            System.out.print("Informe o nome do produto para buscar: ");
            String nome = scanner.nextLine();
            Produto produto = ProdutoDAO.buscarProduto(loja, nome);
            if (produto != null) {
                System.out.println("\n=== PRODUTO ENCONTRADO ===");
                System.out.println(produto);
            } else {
                System.out.println("Produto não encontrado.");
            }
        }

    protected static void avaliarProduto(User usuario) {
        System.out.print("Informe o nome da loja: ");
        String nomeLoja = scanner.nextLine();

        System.out.print("Informe o nome do produto que deseja avaliar: ");
        String nomeProduto = scanner.nextLine();

        int nota;
        try {
            System.out.print("Nota (1 a 5): ");
            nota = Integer.parseInt(scanner.nextLine());
            if (nota < 1 || nota > 5) {
                System.out.println("Nota inválida. Deve estar entre 1 e 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Nota inválida.");
            return;
        }

        System.out.print("Comentário (opcional): ");
        String comentario = scanner.nextLine();

        boolean sucesso = ProdutoDAO.adicionarAvaliacao(usuario, nomeLoja, nomeProduto, nota, comentario);

        if (sucesso) {
            System.out.println("Avaliação adicionada com sucesso!");
        } else {
            System.out.println("Erro ao adicionar avaliação.");
        }
    }



}
