package Loja;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import Produto.ProdutoDAO;
import Database.DatabaseJSON;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static Loja.LojaDAO.obterNotaEConceitoLoja;

public class LojaInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    private Scanner scanner = new Scanner(System.in);
    LojaSystem lojaSystem;

    // Modificado para aceitar um Scanner externo
    public LojaInterface() {
        this(System.in, System.out, new Scanner(System.in), new LojaSystem());
    }

    public LojaInterface(InputStream entrada, PrintStream saida, Scanner scanner, LojaSystem lojaSystem) {
        this.entrada = entrada;
        this.saida = saida;
        this.scanner = scanner;
        this.lojaSystem = lojaSystem;
    }

    // Novo construtor para testes
    public LojaInterface(Scanner scanner, LojaSystem lojaSystem) {
        this(System.in, System.out, scanner, lojaSystem); // Usa mock no teste
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public void atualizarLoja(Scanner scanner, UserLoja loja) {
        LojaSystem.atualizarLoja(scanner, loja);
    }

    public void menuLoja(UserLoja loja) {
        Menu menu = new Menu()
            .adicionarCabecalho("===== Painel da Loja =====")
            .adicionarCabecalho("Bem vindo, " + loja.getNome() + "!")
            .adicionarOpcao("Informações Loja", () -> exibirNotaLoja(loja.getNome()))
            .adicionarOpcao("Gerenciar produtos", () -> LojaSystem.menuProdutos(loja))
            .adicionarOpcao("Atualizar dados", () -> LojaSystem.atualizarLoja(scanner, loja))
            .setPromptSaida("Logout")
            .setPromptEntrada("Escolha uma opção (0-3): ");

        menu.mostrar(this.entrada, this.saida);
    }

    public void loginCadastroLoja() {
        // TODO: Caso repetisse muitas opções inválidas, retorna para o menu principal
        Menu menu = new Menu()
            .adicionarCabecalho("==== LOGIN/CADASTRO Loja ====")
                .adicionarOpcao("Fazer Login", () -> loginLoja())
                .adicionarOpcao("Cadastrar Loja", () -> cadastroLoja())
                .setPromptSaida("Voltar ao Menu Principal")
                .setPromptEntrada("Escolha uma opção (0-2): ");

        menu.mostrar(this.entrada, this.saida);
    }

    private void cadastroLoja() {
        Formulario formulario = new Formulario()
            .adicionarCabecalho("Informe os dados da loja a ser cadastrada")
            .adicionarCabecalho("Para cancelar, pressione <Enter> sem informar dados.")
            .perguntarLoja("nome", "Nome: ")
            .perguntarEmail("email", "E-mail: ")
            .perguntarSenha("senha", "Senha: ")
            .perguntarCNPJ("cnpj", "CNPJ: ");

        if(!formulario.mostrar(this.entrada, this.saida))
            return;

        String nome = formulario.getTexto("nome");
        String email = formulario.getTexto("email");
        String senha = formulario.getTexto("senha");
        String cnpj = formulario.getTexto("cnpj");

        LojaSystem.criarLoja(nome, email, senha, cnpj);
    }

    public void loginLoja() {
        int tentativas = 0;

        while (tentativas < 5) {
            Formulario formulario = new Formulario()
                .adicionarCabecalho("Tentativa: " + tentativas)
                .perguntarEmail("email", "E-mail: ")
                .perguntarSenha("senha", "Senha: ");

            if(!formulario.mostrar(this.entrada, this.saida))
                return;

            String email = formulario.getTexto("email");
            String senha = formulario.getTexto("senha");

            UserLoja loja = lojaSystem.autenticarLoja(email, senha);

            if (loja != null) {
                Info.mostrar(this.saida, "Login bem-sucedido!"); // <-- Debug
                menuLoja(loja);
                return;
            } else {
                Info.mostrar(this.saida, "Email ou senha incorretos!");
                tentativas++;
            }
        }

        Info.mostrar(this.saida, "Número de tentativas excedido. Retornando ao menu inicial...");
    }

    public void exibirProdutosLojaComNota(String nomeLoja) {
        Info.mostrar(this.saida, "Loja: " + nomeLoja);

        // Exibe nota média + conceito da loja
        String notaLoja = obterNotaEConceitoLoja(nomeLoja);
        Info.mostrar(this.saida,
                notaLoja,
                "----------------------------");

        // Exibe produtos da loja
        JsonArray produtos = ProdutoDAO.buscarProdutosPorLoja(nomeLoja);
        if (produtos == null || produtos.size() == 0) {
            Info.mostrar(this.saida, "Nenhum produto encontrado.");
            return;
        }

        for (JsonElement elem : produtos) {
            JsonObject produto = elem.getAsJsonObject();
            Info.mostrar(this.saida,
                    "Produto: " + produto.get("nome").getAsString(),
                    "Preço: R$ " + produto.get("valor").getAsDouble(),
                    "----------------------------");
        }
    }

    public void exibirNotaLoja(String nomeLoja) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (int i = 0; i < lojas.size(); i++) {
            JsonObject loja = lojas.get(i).getAsJsonObject();
            if (loja.get("nome").getAsString().equalsIgnoreCase(nomeLoja)) {

                if (!loja.has("avaliacoes") || loja.get("avaliacoes").getAsJsonArray().size() == 0) {
                    Info.mostrar(this.saida,
                            "Loja: " + nomeLoja,
                            "Esta loja não possui avaliações ainda.");
                    return;
                }

                JsonArray avaliacoes = loja.getAsJsonArray("avaliacoes");
                double somaNotas = 0;
                for (int j = 0; j < avaliacoes.size(); j++) {
                    JsonObject avaliacao = avaliacoes.get(j).getAsJsonObject();
                    somaNotas += avaliacao.get("nota").getAsInt();
                }
                double media = somaNotas / avaliacoes.size();

                String conceito;
                if (media < 2.0) {
                    conceito = "Avaliação: Ruim";
                } else if (media < 3.5) {
                    conceito = "Avaliação: Médio";
                } else if (media < 4.5) {
                    conceito = "Avaliação: Bom";
                } else {
                    conceito = "Excelente";
                }

                String resultado = String.format("Loja: %s%nNota média: %.2f (%s)%n", nomeLoja, media, conceito);
                Info.mostrar(this.saida, resultado);
                return;
            }
        }

        Info.mostrar(this.saida, "Loja não encontrada.");
    }
}
