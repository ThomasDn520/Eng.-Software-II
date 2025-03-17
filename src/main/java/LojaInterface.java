import java.util.Scanner;

public class LojaInterface {

    private Scanner scanner = new Scanner(System.in);
    private LojaSystem LojaSystem;

    public LojaInterface() {

        this.LojaSystem = new LojaSystem();
    }


    public void menuLoja(UserLoja loja){
        boolean continuar = true;
        while (continuar){
            System.out.println("\n===== Painel da Loja =====");
            System.out.println("Bem vindo, " + loja.getNome() + "!");

            System.out.println("\n1.Informações Loja");
            System.out.println("2. Adicionar Produto");
            System.out.println("3. Atualizar dados");
            System.out.println("4. Sair do sistema");
            System.out.println("5.Remover Loja");
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

                    case 5:
                        System.out.print("Digite o CNPJ da loja a ser removida: ");
                        String cnpj = scanner.nextLine().trim();
                        LojaDAO.removerLoja(cnpj);
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