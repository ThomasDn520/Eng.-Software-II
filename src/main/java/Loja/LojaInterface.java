package Loja;

import Console.Widgets.Formulario;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import Produto.ProdutoInterface;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import Produto.ProdutoDAO;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static Loja.LojaDAO.obterNotaEConceitoLoja;

public class LojaInterface {
    private final InputStream entrada;
    private final PrintStream saida;

    // Modificado para aceitar um Scanner externo
    public LojaInterface() {
        this(System.in, System.out);
    }

    public LojaInterface(InputStream entrada, PrintStream saida) {
        this.entrada = entrada;
        this.saida = saida;
    }

    // FIXME: Migrar para construtor acima
    // Novo construtor para testes
    public LojaInterface(Scanner scanner, LojaSystem lojaSystem) {
        this(System.in, System.out); // Usa mock no teste
    }

    public void atualizarLoja(UserLoja loja) {
        UserLoja lojaEdit = new UserLoja(
                loja.getId(),
                loja.getNome(),
                loja.getEmail(),
                loja.getSenha(),
                loja.getCnpj());

        // Nome
        Formulario formulario = new Formulario()
                .adicionarCabecalho("===== Atualização de Dados =====")
                .adicionarCabecalho("Deixe o campo em branco para manter os dados atuais.")
                .perguntarNome("nome", "Novo nome (" + loja.getNome() + "): ");
        if(!formulario.mostrar(this.entrada, this.saida))
            lojaEdit.setNome(formulario.getTexto("nome"));

        // E-mail
        formulario = new Formulario()
                .perguntarEmail("email", "Novo e-mail (" + loja.getEmail() + "): ");
        if(!formulario.mostrar(this.entrada, this.saida))
            lojaEdit.setEmail(formulario.getTexto("email"));

        // Senha
        formulario = new Formulario()
                .perguntarEmail("senha", "Nova senha: ");
        if(!formulario.mostrar(this.entrada, this.saida))
            lojaEdit.setSenha(formulario.getTexto("senha"));

        // CNPJ
        formulario = new Formulario()
                .perguntarEmail("cnpj", "Novo CNPJ (" + loja.getCnpj() + "): ");
        if(!formulario.mostrar(this.entrada, this.saida))
            lojaEdit.setCnpj(formulario.getTexto("cnpj"));

        if(LojaSystem.atualizarLoja(lojaEdit))
            Info.mostrar(this.saida, "Dados da loja atualizados com sucesso!");
        else
            Info.mostrar(this.saida, "Erro ao atualizar dados");
    }

    public void menuLoja(UserLoja loja) {
        Menu menu = new Menu()
            .adicionarCabecalho("===== Painel da Loja =====")
            .adicionarCabecalho("Bem vindo, " + loja.getNome() + "!")
            .adicionarOpcao("Informações Loja", () -> exibirNotaLoja(loja.getNome()))
            .adicionarOpcao("Gerenciar produtos", () -> menuProdutos(loja))
            .adicionarOpcao("Atualizar dados", () -> atualizarLoja(loja))
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

            UserLoja loja = LojaSystem.autenticarLoja(email, senha);

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

    // TODO: Remover se não estiver sendo usado
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

    /**
     * Mostra a nota média e conceito da loja
     * @param nomeLoja O nome da loja
     */
    public void exibirNotaLoja(String nomeLoja) {
        Double media = LojaSystem.buscarNotaMediaLoja(nomeLoja);

        // loja não existe
        if(media == null) {
            Info.mostrar(this.saida, "Loja não encontrada.");
            return;
        }

        // loja não tem avaliações
        if(media < 0) {
            Info.mostrar(this.saida,
                    "Loja: " + nomeLoja,
                    "Esta loja não possui avaliações ainda.");
            return;
        }

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

        Info.mostrar(this.saida,
                "Loja: " + nomeLoja,
                String.format("Nota média: %.2f", media),
                conceito);
    }

    /**
     * Mostra o menu de produtos
     * @param loja A loja que vende os produtos
     */
    public void menuProdutos(UserLoja loja) {
        new ProdutoInterface(this.entrada, this.saida).menuProdutos(loja);
    }
}
