package Loja;

import User.UserLoja;
import Produto.*;

import java.util.Scanner;

public class LojaInterface {

    private Scanner scanner = new Scanner(System.in);
    private LojaSystem LojaSystem;
    private ProdutoSystem produtoSystem;

    public LojaInterface() {

        this.LojaSystem = new LojaSystem();
        this.produtoSystem = new ProdutoSystem();
    }


    public void menuLoja(UserLoja loja){
        boolean continuar = true;
        while (continuar){
            System.out.println("\n===== Painel da Loja =====");
            System.out.println("Bem vindo, " + loja.getNome() + "!");

            System.out.println("\n1. Informações Loja");
            System.out.println("2. Gerenciar Produtos");
            System.out.println("3. Sair do sistema");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        infosLoja(loja);
                        break;
                    case 2:
                        produtoSystem.iniciar(loja);

                        break;
                    case 7:
                        LojaSystem.atualizarLoja(scanner, loja);
                        break;
                    case 8:
                        continuar = false;
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida, tente novamente.");
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next();
            }
        }
    }


    public void loginCadastroLoja() {
        while (true) {
            System.out.println("\n==== LOGIN/CADATRO Loja ====");
            System.out.println("1. Cadastrar Loja");
            System.out.println("2. Fazer Login");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção (1-4): ");

            Scanner scanner = new Scanner(System.in);
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();
                    System.out.print("CNPJ: ");
                    String cnpj = scanner.nextLine();

                    LojaSystem.criarLoja(nome, email, senha, cnpj);
                    break;
                case 2:
                    loginLoja();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Opção inválida, tente novamente.");
            }
        }


    }

    public void infosLoja(UserLoja loja){
        System.out.println("Nome: " + loja.getNome());
        System.out.println("E-mail" + loja.getEmail());
        System.out.println("CNPJ: " + loja.getCnpj());
    }

    public void loginLoja() {
        int tentativas = 0;
        while(tentativas < 5) {
            System.out.print("E-mail: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            UserLoja loja = LojaSystem.autenticarLoja(email, senha);
            if (loja != null) {
                menuLoja(loja);
                return;
            } else {
                System.out.println("Email ou senha incorretos!");
                tentativas++;
            }

        } System.out.println("Número de tentativas excedido. Retornando ao menu inicial...");

    }







}