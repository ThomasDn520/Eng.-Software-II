package Loja;

import User.UserLoja;
import java.util.Scanner;

public class LojaInterface {

    protected Scanner scanner;
    protected LojaSystem lojaSystem;

    // Modificado para aceitar um Scanner externo
    public LojaInterface() {
        this.scanner = scanner;
        this.lojaSystem = new LojaSystem();
    }

    // Novo construtor para testes
    public LojaInterface(Scanner scanner, LojaSystem lojaSystem) {
        this.scanner = scanner;
        this.lojaSystem = lojaSystem; // Usa mock no teste
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void atualizarLoja(Scanner scanner, UserLoja loja) {
        LojaSystem.atualizarLoja(scanner, loja);
    }

    public void menuLoja(UserLoja loja) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n===== Painel da Loja =====");
            System.out.println("Bem vindo, " + loja.getNome() + "!");

            System.out.println("\n1. Informações Loja");
            System.out.println("2. Adicionar Produto");
            System.out.println("3. Atualizar dados");
            System.out.println("4. Sair do sistema");
            System.out.print("Escolha uma opção: ");

            if (scanner.hasNextInt()) {
                int opcao = scanner.nextInt();
                scanner.nextLine();

                switch (opcao) {
                    case 1:
                        System.out.println("Função não implementada!");
                        break;
                    case 2:
                        System.out.println("Função não implementada!");
                        break;
                    case 3:
                        LojaSystem.atualizarLoja(scanner, loja);
                        break;
                    case 4:
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
        int tentativas = 0;
        while (tentativas < 5) { // Limita as tentativas para evitar loop infinito
            System.out.println("\n==== LOGIN/CADASTRO Loja ====");
            System.out.println("1. Cadastrar Loja");
            System.out.println("2. Fazer Login");
            System.out.println("3. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção (1-3): ");

            if (scanner.hasNextInt()) {
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
                        tentativas++;
                        if (tentativas >= 5) {
                            System.out.println("Muitas tentativas inválidas. Retornando ao menu inicial.");
                            return; // Sai do loop após muitas tentativas erradas
                        }
                }
            } else {
                System.out.println("Entrada inválida! Digite um número.");
                scanner.next(); // Descarta a entrada errada
                tentativas++;
            }
        }
    }


    public void loginLoja() {
        int tentativas = 0;
        scanner.nextLine();
        while (tentativas < 5) {
            System.out.println("Tentativa: " + tentativas);

            System.out.print("E-mail: ");
            String email = scanner.nextLine();
            System.out.println("Email recebido: " + email); // <-- Debug

            System.out.print("Senha: ");
            String senha = scanner.nextLine();
            System.out.println("Senha recebida: " + senha); // <-- Debug



            UserLoja loja = lojaSystem.autenticarLoja(email, senha);

            if (loja != null) {
                System.out.println("Login bem-sucedido!"); // <-- Debug
                menuLoja(loja);
                return;
            } else {
                System.out.println("Email ou senha incorretos!");
                tentativas++;
            }
        }
        System.out.println("Número de tentativas excedido. Retornando ao menu inicial...");
    }


}
