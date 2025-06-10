package Cliente;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import User.UserCliente;
import Loja.LojaInterface;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ClienteInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    // FIXME: Remover
    protected Scanner scanner = new Scanner(System.in);
    protected ClienteSystem clienteSystem;

    /**
     * Interface do cliente com o sistema
     * Essa interface usará a saída e entrada padrão do console ({@code System.in} e {@code System.out})
     */
    public ClienteInterface() {
        this(System.in, System.out);
    }

    /**
     * Interface do cliente com o sistema
     * @param saida Stream de saída na qual as informações serão mostradas
     * @param entrada Stream de entrada pela qual o sistema recebe os dados
     */
    public ClienteInterface(InputStream entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
        this.clienteSystem = new ClienteSystem();
    }

    // TODO: Remover
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    // TODO: Remover
    public void setClienteSystem(ClienteSystem clienteSystem) {
        this.clienteSystem = clienteSystem;
    }

    public void menuCliente(UserCliente cliente) {
        Menu menu = new Menu()
            .adicionarCabecalho("===== Painel do Cliente =====")
            .adicionarCabecalho("Bem-vindo, " + cliente.getNome() + "!")
            .adicionarOpcao("Buscar itens", () -> buscarProdutoNome(cliente))
            .adicionarOpcao("Carrinho de compras", () -> menuCarrinho(cliente))
            .adicionarOpcao("Atualizar dados", () -> atualizarCliente(cliente))
            .adicionarOpcao("Histórico de compras", () -> historicoCliente(cliente))
            .adicionarOpcao("Ver nota da loja", () -> verNotaLoja())
            .setPromptSaida("Logout")
            .setPromptEntrada("Escolha uma opção (0-5): ");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Mostra a interface para verificar nota da loja
     */
    private void verNotaLoja() {
        Formulario formulario = new Formulario()
                .perguntarLoja("loja", "Digite o nome da loja para ver a nota: ");

        if(!formulario.mostrar(this.entrada, this.saida)) // se foi cancelado
            return;
        String nomeLoja = formulario.getTexto("loja");

        new LojaInterface(this.entrada, this.saida)
                .exibirNotaLoja(nomeLoja);
    }

    public void menuCarrinho(UserCliente cliente) {
        Menu menu = new Menu()
            .adicionarCabecalho("===== Painel Carrinho de Compras =====")
                .adicionarCabecalho("Carrinho de " + cliente.getNome() + "!")
                .adicionarOpcao("Exibir itens", () -> exibirItensCarrinho(cliente))
                .adicionarOpcao("Remover item", () -> removerItemCarrinho(cliente))
                .adicionarOpcao("Concluir compra", () -> efetuarCompra(cliente, scanner))
                .setPromptSaida("Voltar")
                .setPromptEntrada("Escolha uma opção (0-3): ");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Mostra o diálogo de atualização de dados do cliente
     * @param cliente O cliente logado
     */
    public void atualizarCliente(UserCliente cliente) {
        // faz uma cópia
        UserCliente clienteEditado = new UserCliente(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getCpf(),
                cliente.getPontos());

        // nome
        Formulario formulario = new Formulario()
            .adicionarCabecalho("===== Atualização de Dados =====\n")
            .adicionarCabecalho("Deixe em branco para manter os dados atuais.")
                .perguntarNome("nome", "Novo nome (" + clienteEditado.getNome() + "): ");

        if(formulario.mostrar(this.entrada, this.saida))
            clienteEditado.setNome(formulario.getTexto("nome"));

        // email
        formulario = new Formulario()
                .perguntarEmail("email", "Novo e-mail (" + clienteEditado.getEmail() + "): ");
        if(formulario.mostrar(this.entrada, this.saida))
            clienteEditado.setEmail(formulario.getTexto("email"));

        // senha
        formulario = new Formulario()
                .perguntarSenha("senha", "Nova senha: ");
        if(formulario.mostrar(this.entrada, this.saida))
            clienteEditado.setSenha(formulario.getTexto("senha"));


        // cpf
        formulario = new Formulario()
                .perguntarSenha("cpf","Novo CPF (" + clienteEditado.getCpf() + "): ");
        if(formulario.mostrar(this.entrada, this.saida))
            clienteEditado.setCpf(formulario.getTexto("cpf"));

        // atualiza e mostra resultado
        Info info = new Info();
        if (ClienteSystem.atualizarCliente(clienteEditado)) {
            info.adicionarTexto("Dados do cliente atualizados com sucesso!");
        } else {
            info.adicionarTexto("Erro ao atualizar dados. Talvez o e-mail já esteja em uso.");
        }

        info.mostrar(this.saida);
    }

    public boolean buscarProdutoNome(UserCliente cliente) {
        Formulario formulario = new Formulario()
                .perguntarTexto("produto", "Digite o nome do produto que deseja buscar: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return true;

        String nomeBusca = formulario.getTexto("produto").trim().toLowerCase();
        if(ClienteSystem.buscarProdutoPorNome(cliente, scanner, nomeBusca)){
            return true;
        }
        else {
            return false;
        }
    }

    // TODO: Migrar código de interface
    public static boolean efetuarCompra(UserCliente cliente, Scanner scanner){
        return ClienteSystem.efetuarCompra(cliente, scanner);
    }

    // TODO: Migrar código de interface
    public static boolean exibirItensCarrinho(UserCliente cliente) {
        if(ClienteSystem.exibirCarrinho(cliente)){
            return true;
        }
        return false;
    }

    /**
     * Remove um item do carrinho
     * @param cliente Cliente logado
     * @return True se houve sucesso, senão false
     */
    public boolean removerItemCarrinho(UserCliente cliente) {
        if(!ClienteSystem.exibirCarrinho(cliente)){
            return false;
        }

        Formulario formulario = new Formulario()
            .adicionarCabecalho("Qual produto voce deseja remover do carrinho?")
            .adicionarCabecalho("Para cancelar, pressione <Enter> sem informar dados.")
            .perguntarTexto("produto", "Digite um nome: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return true;

        String itemRemover = formulario.getTexto("produto").trim();

        if (ClienteSystem.removerProduto(cliente, itemRemover)) {
            Info.mostrar(this.saida, "Produto removido com sucesso.");
            return true;
        } else {
            Info.mostrar(this.saida, "Produto não encontrado no carrinho. Tente novamente.");
        }
        return false;
    }

    /**
     * Apresenta o histórico de compras do cliente
     * @param cliente
     * @return false se ocorreu algum erro, senão true
     */
    public boolean historicoCliente(UserCliente cliente) {
        // FIXME: Mudar para Menu ao invés de Formulario
        if(!ClienteSystem.exibirHistoricoCliente(cliente)) {
            return false;
        }

        Formulario formulario = new Formulario()
                .perguntarOpcao("compra",
                        "Escolha o Nº da compra para mais opções, ou 0 (zero) para voltar: ", 65535);

        if(!formulario.mostrar(this.entrada, this.saida)) // cancelado?
            return true;

        int opcao = formulario.getInteiro("compra");
        if(opcao != 0)
            detalheCompra(cliente, opcao-1);

        Info.mostrar(this.saida, "Voltando ao menu do cliente...");
        return true;
    }

    /**
     * Apresenta as opções de uma compra anterior
     * @param cliente O cliente logado
     * @param indiceHistorico O número da compra
     * @return false se ocorreu algum erro, senão true
     */
    public boolean detalheCompra(UserCliente cliente, int indiceHistorico) {
        Menu menu = new Menu()
            .adicionarOpcao("Avaliar produto", () -> avaliarProduto(cliente, indiceHistorico))
            .adicionarOpcao("Avaliar loja", () -> avaliarLoja(cliente, indiceHistorico))
            .setPromptSaida("Voltar")
            .setPromptEntrada("Escolha uma opção: ");

        menu.mostrar(this.entrada, this.saida);

        return true;
    }

    /**
     * Menu inicial da interface do cliente
     */
    public void loginCadastroCliente() {
        Menu menu = new Menu()
                .adicionarCabecalho("==== LOGIN/CADATRO CLIENTE ====")
                .adicionarOpcao("Fazer login", () -> loginCliente())
                .adicionarOpcao("Cadastrar Cliente", () -> cadastrarCliente())
                .setPromptSaida("Voltar ao Menu Principal")
                .setPromptEntrada("Escolha uma opção (0-2): ");

        menu.mostrar(this.entrada, this.saida);
    }

    /**
     * Efetua o login do cliente no sistema
     */
    public void loginCliente() {
        int tentativas = 0;
        while(tentativas < 5) {
            Formulario formulario = new Formulario()
                .adicionarCabecalho("Login do cliente")
                .adicionarCabecalho("Para cancelar, pressione <Enter> sem informar dados.")
                .perguntarEmail("email", "E-mail: ")
                .perguntarSenha("senha", "Senha: ");

            if(!formulario.mostrar(this.entrada, this.saida)) // cancelado?
                return;

            String email = formulario.getTexto("email");
            String senha = formulario.getTexto("senha");

            UserCliente cliente = ClienteSystem.autenticarCliente(email, senha);
            if (cliente != null) {
                menuCliente(cliente);
                return;
            } else {
                Info.mostrar(this.saida, "ID ou senha incorretos!");
                tentativas++;
            }

        }
        Info.mostrar(this.saida, "Número de tentativas excedido. Retornando ao menu inicial...");
    }

    /**
     * Cadastra um novo cliente no sistema
     */
    private void cadastrarCliente() {
        Formulario formulario = new Formulario()
            .adicionarCabecalho("Cadastro de cliente")
            .adicionarCabecalho("Para cancelar, pressione <Enter> sem informar dados.")
            .perguntarNome("nome", "Nome: ")
            .perguntarEmail("email", "E-mail: ")
            .perguntarSenha("senha", "Senha: ")
            .perguntarCPF("cpf", "CPF: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return;

        String nome = formulario.getTexto("nome");
        String email = formulario.getTexto("email");
        String senha = formulario.getTexto("senha");
        String cpf = formulario.getTexto("cpf");

        clienteSystem.criarCliente(nome, email, senha, cpf);
    }

    /**
     * Efetua a avaliação de loja em uma compra
     * @param cliente O cliente que efetuou a compra
     * @param indiceCompra O índice da compra
     */
    protected void avaliarLoja(UserCliente cliente, int indiceCompra) {
        String nomeLoja = ClienteSystem.buscarDetalheCompra("loja", indiceCompra, cliente);
        if(nomeLoja == null) {
            Info.mostrar(this.saida, "A compra Nº " + indiceCompra + " não existe.");
            return;
        }

        Formulario formulario = new Formulario()
            .perguntarNota("nota", "Digite uma nota para a loja (1 a 5): ", 1, 5)
            .perguntarTextoOpcional("comentario", "Digite um comentário (opcional): ");

        if(!formulario.mostrar(this.entrada, this.saida)) // cancelado?
            return;

        int nota = formulario.getInteiro("nota");
        String comentario = formulario.getTexto("comentario");

        // Agora chama o método com 4 argumentos
        boolean sucesso = ClienteSystem.avaliarLoja(cliente, nomeLoja, nota, comentario);
        if (sucesso) {
            Info.mostrar(this.saida, "Avaliação registrada com sucesso!");
        } else {
            Info.mostrar(this.saida, "Não foi possível avaliar a loja. Verifique o nome e tente novamente.");
        }
    }

    /**
     * Efetua a avaliação do produto em uma compra
     * @param cliente O cliente que efetuou a compra
     * @param indiceCompra O índice da compra
     */
    protected void avaliarProduto(UserCliente cliente, int indiceCompra) {
        String nomeLoja = ClienteSystem.buscarDetalheCompra("loja", indiceCompra, cliente);
        String nomeProduto = ClienteSystem.buscarDetalheCompra("produto", indiceCompra, cliente);
        if(nomeLoja == null || nomeProduto == null) {
            Info.mostrar(this.saida, "A compra Nº " + indiceCompra + " não existe.");
            return;
        }

        Formulario formulario = new Formulario()
            .perguntarNota("nota", "Digite uma nota para o produto (1 a 5): ", 1, 5)
            .perguntarTextoOpcional("comentario", "Digite um comentário (opcional): ");


        if(!formulario.mostrar(this.entrada, this.saida)) // cancelado?
            return;

        int nota = formulario.getInteiro("nota");
        String comentario = formulario.getTexto("comentario");

        // Agora chama o método com 4 argumentos
        boolean sucesso = ClienteSystem.avaliarProduto(cliente, nomeLoja, nomeProduto, nota, comentario);
        if (sucesso) {
            Info.mostrar(this.saida, "Avaliação registrada com sucesso!");
        } else {
            Info.mostrar(this.saida, "Não foi possível avaliar o produto. Verifique o nome e tente novamente.");
        }
    }
}

