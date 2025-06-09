package Admin;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import Loja.LojaSystem;
import User.UserAdmin;
import Loja.LojaInterface;
import User.UserCliente;
import User.UserLoja;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

// TODO
//  1. [OK] Adicionar System.in e System.out nos argumentos e na instância (facilita os testes!)
//  2. [OK] Fazer métodos .mostrar() usar a stream de entrada (System.in) da instância
//  2. [OK] Fazer métodos .mostrar() usar a stream de saída (System.out) da instância
//  3. Não deve existir nenhum Scanner, System.out.print no código
//  4. [OK] Se alguma coisa usar System.in, System.out ou Scanner, então deveria ficar aqui!!!
//  5. Não deve haver nenhum System.print... ou Scanner.next... em nenhuma classe, exceto as do módulo <Console>
/**
 * Classe interface de administrador
 */
public class AdminInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    private Scanner scanner = new Scanner(System.in);

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
    }

    // TODO: Renomear para criarAdmPadrao
    //  talvez renomear os admins para alguma coisa mais única, como "master", "admin-mestre" ou "root"
    public void criarAdmTeste() {
        AdminSystem.criarAdminDiretamente("Admin1", "admin1@email.com", "12345678");
        AdminSystem.criarAdminDiretamente("Admin2", "admin2@email.com", "12345678");
    }

    // TODO: Remover argumento ou adicionar níveis de privilégio
    /**
     * Mostra o menu principal do administrador para interação
     * @param admin O administrador logado
     */
    public void menuAdmin(UserAdmin admin) {
        Menu menu = new Menu()
            .setPromptEntrada("===== Painel do Administrador =====")
            .adicionarOpcao("Criar novo admin", () -> criarAdmin())
            .adicionarOpcao("Listar admins", () -> listarAdmins())
            .adicionarOpcao("Listar clientes", () -> listarClientes())
            // TODO: Use listarClientes pra remover um cliente
            .adicionarOpcao("Deletar clientes", () -> removerCliente())
            // TODO: Ver notas e remover a partir de listarLojas
            // TODO: Remova <removerLoja> e <ver nota da loja> daqui
            .adicionarOpcao("Listar lojas", () -> listarLojas())
            .adicionarOpcao("Deletar lojas", () -> removerLoja())
            .adicionarOpcao("Ver nota da loja", () -> {
                    Formulario f = new Formulario()
                        .perguntarLoja("loja", "Digite o nome da loja para ver a nota: ");
                    if(!f.mostrar(System.in, System.out))
                        return;
                    String nomeLoja = f.getTexto("loja");
                    // TODO: Remover scanner dessa classe quando remover o scanner da LojaInterface
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

            // se formulario.mostrar retornar false, o usuário cancelou
            if(!formulario.mostrar(this.entrada, this.saida)) // formulario foi cancelado?
                return;

            int id = formulario.getInteiro("id");
            String senha = formulario.getTexto("senha");

            // Validação
            UserAdmin admin = AdminSystem.autenticarAdmin(id, senha);

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

    /**
     * Apresenta interface para criação de admin
     */
    public void criarAdmin() {
        Formulario formulario = new Formulario()
            .adicionarCabecalho("\n--- Cadastro de Administrador ---")
            .adicionarCabecalho("Para cancelar e retornar ao menu anterior, pressione <Enter> sem informar nenhum dado.")
            .perguntarNome("nome", "Nome: ")
            .perguntarEmail("email", "E-mail: ")
            .perguntarSenha("senha", "Senha: ");

        if(!formulario.mostrar(this.entrada, this.saida)) // foi cancelado
            return;

        String nome = formulario.getTexto("nome");
        String email = formulario.getTexto("email");
        String senha = formulario.getTexto("senha");

        int id = AdminSystem.criarAdminDiretamente(nome, email, senha); // Versão unificada

        if (id > 0) {
            Info.mostrar(this.saida, "Admin cadastrado com sucesso! ID: " + id);
        } else {
            Info.mostrar(this.saida, "Falha ao cadastrar admin.");
        }
    }

    /**
     * Lista todos os admins
     */
    public void listarAdmins() {
        List<UserAdmin> admins = AdminSystem.listarAdmins();
        Info info = new Info()
            .adicionarTexto("\n==== Admins Registrados ====\n");

        if (admins.isEmpty()) {
            info.adicionarTexto("Nenhum admin cadastrado.");
        } else {
            for (UserAdmin admin : admins) {
                info.adicionarTexto("ID: " + admin.getId() +
                        " | Nome: " + admin.getNome() +
                        " | Email: " + admin.getEmail());
            }
        }

        info.mostrar(this.saida);
    }

    /**
     * Exibe a interface para listagem de clientes
     */
    public void listarClientes() {
        // TODO: Se não tiver nenhum cliente cadastrado, mostre que não há nenhum cliente
        List<UserCliente> clientes = AdminSystem.listarClientes();
        Info info = new Info()
            .adicionarTexto("\n==== Clientes Registrados ====\n");


        for (UserCliente cliente : clientes) {
            info.adicionarTexto("ID: " + cliente.getId() + " | Nome: " + cliente.getNome() + " | Email: " + cliente.getEmail()+ " | cpf: " + cliente.getCpf());
        }

        info.mostrar(this.saida);
    }

    /**
     * Exibe a interface para remoção de cliente
     */
    public void removerCliente() {
        Formulario formulario = new Formulario()
            .adicionarCabecalho("Para cancelar e retornar ao menu anterior, pressione <Enter> sem informar nenhum dado.")
            .perguntarEmail("email", "Digite o e-mail do cliente que deseja remover: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return;

        String email = formulario.getTexto("email");

        Info info = new Info();

        if (AdminSystem.removerCliente(email)) {
            info.adicionarTexto("Cliente removido com sucesso!");
        } else {
            info.adicionarTexto("Falha ao remover o cliente. Verifique se o e-mail está correto.");
        }

        info.mostrar(this.saida);
    }

    /**
     * Exibe a interface para listagem de lojas
     */
    public void listarLojas() {
        // TODO: Se não tiver nenhuma loja cadastrada, mostre que não há nenhuma loja
        List<UserLoja> lojas = AdminSystem.listarLojas();
        Info info = new Info()
                .adicionarTexto("\n==== Lojas Registradas ====\n");

        for (UserLoja loja: lojas) {
            info.adicionarTexto("ID: " + loja.getId() + " | Nome: " + loja.getNome() + " | Email: " + loja.getEmail()+ " | cnpj: " + loja.getCnpj());
        }

        info.mostrar(this.saida);
    }

    /**
     * Exibe a interface para remoção de loja
     */
    public void removerLoja(){
        Formulario formulario = new Formulario()
                .adicionarCabecalho("Para cancelar e retornar ao menu anterior, pressione <Enter> sem informar nenhum dado.")
                .perguntarCNPJ("cnpj", "Digite o CNPJ da loja a ser removida: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return;

        String cnpj = formulario.getTexto("cnpj");

        Info info = new Info();

        if (AdminSystem.removerLoja(cnpj)) {
            info.adicionarTexto("Loja removida com sucesso!");
        } else {
            info.adicionarTexto("Falha ao remover a loja. Verifique se o e-mail está correto.");
        }
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
