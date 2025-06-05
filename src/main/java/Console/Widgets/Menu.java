package Console.Widgets;

import Console.EmptyInputException;
import Console.EntradaUsuario;
import Console.InvalidInputException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.IntPredicate;

/**
 * Classe responsável por construção e display de menus de opções
 * O menu apresenta opções, o usuário escolhe uma e a ação atrelada a essa opção será executada
 */
public class Menu {
    private final static int OPCAO_INVALIDA = -1;
    private final static int SAIR = 0;

    /**
     * Um item do menu atrelado a uma ação
     */
    private static class MenuItem {
        private final String texto;
        private final IntPredicate acao;

        /**
         * Construtor
         * @param texto O texto do item do menu
         * @param acao Função que deve retornar se houve sucesso ou falha na execução
         */
        private MenuItem(String texto, IntPredicate acao) {
            this.texto = texto;
            this.acao = acao;
        }
    }

    private final List<MenuItem> opcoes;
    private final List<String> cabecalhos;
    private final List<String> rodapes;
    private String promptSaida;
    private String promptEntrada;
    private Function<Integer, String> geradorIndices;

    /**
     * Construtor
     * @param in Stream de entrada
     * @param out Stream de saída
     */
    private Menu(InputStream in, PrintStream out) {
        this.opcoes = new ArrayList<>();
        this.cabecalhos = new ArrayList<>();
        this.rodapes = new ArrayList<>();
        this.promptSaida = "Sair";
        this.promptEntrada = "Digite uma opcao";
        this.geradorIndices = (i) -> String.format("%d. ", i);
    }

    /**
     * Construtor padrão
     * Usa entrada e saída padrão do sistema
     */
    public Menu() {
        this(System.in, System.out);
    }

    /**
     * Converte esse menu construído em texto
     * @return A representação desse menu
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");

        // linhas de cabecalho
        for(String linha: this.cabecalhos) {
            sb.append(linha).append("\n");
        }
        // opcoes
        for(int i = 0; i< opcoes.size(); i++) {
            sb.append(geradorIndices.apply(i+1))
                    .append(opcoes.get(i).texto)
                    .append("\n");
        }
        sb.append(String.format("0. %s\n", this.promptSaida));
        // linhas de rodape
        for(String linha: this.rodapes) {
            sb.append(linha).append("\n");
        }
        return sb.toString();
    }

    /**
     * Adiciona uma opção sem argumentos a esse menu
     * @param texto Texto da opção
     * @param acao Ação a ser executada ao escolher essa opção. Deve retornar se houve sucesso ou falha
     * @return Esse menu com a entrada adicionada
     */
    public Menu adicionarOpcao(String texto, BooleanSupplier acao) {
        this.opcoes.add(new MenuItem(texto, (ignored) -> acao.getAsBoolean()));
        return this;
    }

    /**
     * Adiciona uma opção sem argumentos que nunca falha a esse menu
     * @param texto Texto da opção
     * @param acao Ação a ser executada. É assumido que a função sempre conclui com êxito
     * @return Esse menu com a entrada adicionada
     */
    public Menu adicionarOpcao(String texto, Runnable acao) {
        this.opcoes.add(new MenuItem(texto, (ignored) -> {
            acao.run();
            return true;
        }));
        return this;
    }

    /**
     * Adiciona uma opção a esse menu
     * @param texto Texto da opção
     * @param acao Ação a ser executada ao escolher essa opção, que recebe o índice da opção selecionada e retorna se houve sucesso ou falha
     * @return Esse menu com a entrada adicionada
     */
    public Menu adicionarOpcao(String texto, IntPredicate acao) {
        this.opcoes.add(new MenuItem(texto, acao));
        return this;
    }

    /**
     * Configura a opção de saída desse menu
     * @param texto Texto da opção
     * @return Esse menu com a entrada adicionada
     */
    public Menu setPromptSaida(String texto) {
        this.promptSaida = texto;
        return this;
    }

    /**
     * Configura o gerador de índices desse menu
     * O primeiro argumento da expressão de formatação será substituído pelo índice da opção
     * @param exp Expressão de formatação
     * @return Esse menu com a entrada adicionada
     */
    public Menu setGeradorIndices(String exp) {
        this.geradorIndices = (i) -> String.format(exp, i);
        return this;
    }

    /**
     * Configura o texto de entrada desse menu
     * <p>O texto de entrada será mostrado depois dos rodapés, antes da entrada do usuário
     * @param texto Prompt de entrada
     * @return Esse menu com a entrada adicionada
     */
    public Menu setPromptEntrada(String texto) {
        this.promptEntrada = texto;
        return this;
    }

    /**
     * Adiciona um cabeçalho a esse menu
     * <p>Cabeçalhos serão mostrados antes das opções
     * @param texto Linha de cabeçalho
     * @return Esse menu com a entrada adicionada
     */
    public Menu adicionarCabecalho(String texto) {
        this.cabecalhos.add(texto);
        return this;
    }

    /**
     * Adiciona um rodapé a esse menu
     * <p>Rodapés serão mostrados depois das opções e antes do prompt
     * @param texto Texto da opção
     * @return Esse menu com a entrada adicionada
     */
    public Menu adicionarRodape(String texto) {
        this.rodapes.add(texto);
        return this;
    }

    /**
     * Mostra esse menu usando a entrada e saída padrão do console
     * <p>O menu será mostrado repetidamente até a opção "0" ser selecionada
     */
    public void mostrar() {
        this.mostrar(System.in, System.out);
    }

    /**
     * Mostra esse menu
     * <p>O menu será mostrado repetidamente até a opção "0" ser selecionada
     */
    public void mostrar(InputStream in, PrintStream out) {
        int opcao = OPCAO_INVALIDA;
        EntradaUsuario<Integer> seletor = EntradaUsuario.inteiro(this.promptEntrada, 0, opcoes.size(), "Entrada inválida!");

        boolean resultadoOk;
        do {
            out.print(this.toString());
            try {
                opcao = seletor.perguntar(in, out);
                if(opcao == SAIR || opcao == OPCAO_INVALIDA)
                    continue;
                resultadoOk = this.opcoes.get(opcao-1).acao.test(opcao-1);
                if(!resultadoOk)
                    out.println("Ocorreu falha na operação.");
            } catch (InvalidInputException | EmptyInputException e) {
                out.println("Entrada inválida! Tente novamente");
                opcao = OPCAO_INVALIDA;
            }
        } while(opcao != 0);
    }
}
