package Admin;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import Loja.LojaSystem;
import User.UserAdmin;
import Loja.LojaInterface;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

// TODO
//  1. [OK] Adicionar System.in e System.out nos argumentos e na instância (facilita os testes!)
//  2. [OK] Fazer métodos .mostrar() usar a stream de entrada (System.in) da instância
//  2. [OK] Fazer métodos .mostrar() usar a stream de saída (System.out) da instância
//  3. Não deve existir nenhum Scanner, System.out.print no código
//  4. Se alguma coisa usar System.in, System.out ou Scanner, então deveria ficar aqui!!!
//  5. Não deve haver nenhum System.print... ou Scanner.next... em nenhuma classe, exceto as do módulo <Console>
/**
 * Classe interface de administrador
 */
public class AdminInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    private Scanner scanner = new Scanner(System.in);
    private AdminSystem adminSystem;

    /**
     * Interface de usuário com as funções administrativas do sistema
     * Essa interface usará a saída e entrada padrão do console ({@code System.in} e {@code System.out})
     */
    public AdminInterface() {
        this(System.in, System.out);
    }

    /**
     * Interface de usuário com as funções administrativas do sistema
     * @param saida Stream de saída na qual as informações serão mostradas
     * @param entrada Stream de entrada pela qual o sistema recebe os dados
     */
    public AdminInterface(InputStream entrada, PrintStream saida) {
        this.saida = saida;
        this.entrada = entrada;
        this.adminSystem = new AdminSystem();
    }

    // TODO: Renomear para criarAdmPadrao
    //  talvez renomear os admins para alguma coisa mais única, como "master", "admin-mestre" ou "root"
    public void criarAdmTeste() {
        adminSystem.criarAdminDiretamente("Admin1", "admin1@email.com", "12345678");
        adminSystem.criarAdminDiretamente("Admin2", "admin2@email.com", "12345678");
    }

    // TODO: Remover argumento ou adicionar níveis de privilégio
    /**
     * Mostra o menu principal do administrador para interação
     * @param admin O administrador logado
     */
    public void menuAdmin(UserAdmin admin) {
        Menu menu = new Menu()
                .setPromptEntrada("===== Painel do Administrador =====")
                .adicionarOpcao("Criar novo admin", () -> AdminSystem.criarAdmin(scanner))
                .adicionarOpcao("Listar admins", () -> adminSystem.listarAdmins())
                .adicionarOpcao("Listar clientes", () -> adminSystem.listarClientes())
                .adicionarOpcao("Deletar clientes", () -> adminSystem.removerCliente(scanner))
                .adicionarOpcao("Listar lojas", () -> adminSystem.listarLojas())
                .adicionarOpcao("Deletar lojas", () -> adminSystem.removerLoja(scanner))
                .adicionarOpcao("Ver nota da loja", () -> {
                    Formulario f = new Formulario()
                        .perguntarLoja("loja", "Digite o nome da loja para ver a nota: ");
                    if(!f.mostrar(System.in, System.out))
                        return;
                    String nomeLoja = f.getTexto("loja");
                    // FIXME: Melhorar linha abaixo
                    new LojaInterface(this.entrada, this.saida, this.scanner, new LojaSystem())
                            .exibirNotaLoja(nomeLoja);
                })
                .setPromptSaida("Logout")
                .setPromptEntrada("Escolha uma opção (0-7): ");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Apresenta a página para login do administrador
     * Serão permitidas no máximo cinco tentativas, então será redirecionado ao menu principal
     */
    public void loginAdmin() {
        int tentativas = 0;
        final int MAX_TENTATIVAS = 5;

        while (tentativas < MAX_TENTATIVAS) {
            Formulario formulario = new Formulario()
                    .adicionarCabecalho("--- LOGIN ADMIN ---")
                    .adicionarCabecalho("Informe suas credenciais de administrador.")
                    .adicionarCabecalho("Para cancelar e retornar ao menu anterior, pressione <Enter> sem informar nenhum dado.")
                    .perguntarOpcao("id", "Digite seu ID: ", 999)
                    .perguntarSenha("senha", "Digite sua senha: ");

            // se .mostrar retornar false, o usuário cancelou
            if(!formulario.mostrar(this.entrada, this.saida)) // formulario foi cancelado?
                return;

            int id = formulario.getInteiro("id");
            String senha = formulario.getTexto("senha");

            // Validação
            UserAdmin admin = AdminDAO.autenticar(id, senha);

            if (admin != null) {
                Info.mostrar(this.saida, "Bem-vindo, " + admin.getNome() + "!");
                menuAdmin(admin);
                return;
            } else {
                tentativas++;
                Info.mostrar(this.saida,
                        "Credenciais inválidas!",
                        "Tentativas restantes: " + (MAX_TENTATIVAS - tentativas));
            }
        }

        if(tentativas == MAX_TENTATIVAS)
            Info.mostrar(this.saida,
                    "Número máximo de tentativas excedido.",
                    "Acesso bloqueado temporariamente.");
    }

    // FIXME: Remover. Só está sendo usado em testes
    public int lerIdAdmin(Scanner scanner) {
        while (true) {
            try {
                System.out.print("ID do Admin: ");
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Erro: Digite um número válido!");
            }
        }
    }

    // FIXME: Remover. Não é necessário
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
