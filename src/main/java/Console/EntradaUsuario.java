package Console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Classe responsável por obter a entrada do usuário
 * @param <R> tipo da resposta
 */
public class EntradaUsuario<R> {
    private R resposta;
    private final boolean opcional;
    private final String prompt;
    private final Function<String, R> conversor;
    private final Predicate<String> validador;
    private final String msgErro;

    /**
     * Construtor
     * @param opcional Se true, o texto irá aceitar string vazia, desde que obedeça às outras regras de validação
     * @param prompt Pergunta que será feita ao usuário antes da entrada de dados
     * @param validador Função que valida a entrada do usuário
     */
    public EntradaUsuario(boolean opcional, String prompt, Function<String, R> conversor, Predicate<String> validador, String msgErro) {
        this.resposta = null;
        this.opcional = opcional;
        this.prompt = prompt;
        this.conversor = conversor;
        this.validador = validador;
        this.msgErro = msgErro;
    }

    /**
     * Obtém a entrada do usuário
     * @param in Stream de entrada
     * @param out Stream de saída
     * @return A entrada do usuário validada e convertida
     */
    public R
    perguntar(InputStream in, PrintStream out) {
        if(in == null || out == null)
            throw new NullPointerException("Entrada e saída não podem ser nulas!");
        Scanner scanner = new Scanner(in);

        out.print(this.prompt);
        String entrada = scanner.nextLine();
        if(entrada.isEmpty() && !this.opcional)
            throw new EmptyInputException("Entrada vazia!");
        if(!this.validador.test(entrada))
            throw new InvalidInputException(this.msgErro);

        this.resposta = this.conversor.apply(entrada);
        return this.resposta;
    }

    /**
     * Obtém a última resposta validada e convertida
     * @return A resposta da última entrada
     */
    public R getResposta() {
        return this.resposta;
    }

    /**
     * Constroi um objeto que obtém um texto qualquer, incluindo texto vazio.
     * @param prompt A questão a ser apresentada antes da entrada do usuário
     * @return Um objeto que obtém e valida entrada do usuário
     */
    public static EntradaUsuario<String> texto(String prompt) {
        return new EntradaUsuario<>(true, prompt, (valor) -> valor, (ignored) -> true, "Isso não deveria ter acontecido.");
    }

    /**
     * Constroi um objeto que obtém um texto obedecendo a um padrão
     *
     * @param prompt A questão a ser apresentada antes da entrada do usuário
     * @param regex A expressão regular que determina o padrão que o texto deve seguir
     * @param conversor Função que converte o texto para o padrão desejado
     * @param msgErro A mensagem de erro a ser mostrada caso a validação/conversão falhe
     * @return Um objeto que obtém e valida entrada do usuário
     */
    public static EntradaUsuario<String> texto(String prompt, String regex, Function<String, String> conversor, String msgErro) {
        return new EntradaUsuario<>(
                false,
                prompt,
                (entrada) -> conversor.apply(entrada),
                (entrada) -> Pattern.matches(regex, entrada),
                msgErro);
    }

    /**
     * Constroi um objeto que obtém um número inteiro dentro de um intervalo (inclusivo)
     *
     * @param prompt A questão a ser apresentada antes da entrada do usuário
     * @param min O menor valor permitido
     * @param max O maior valor permitido
     * @param msgErro A mensagem de erro a ser mostrada caso a validação/conversão falhe
     * @return Um objeto que obtém e valida entrada do usuário
     */
    public static EntradaUsuario<Integer> inteiro(String prompt, int min, int max, String msgErro) {
        return new EntradaUsuario<>(
                false,
                prompt,
                (entrada) -> Integer.valueOf(entrada),
                (entrada) -> {
                    try {
                        int value = Integer.parseInt(entrada);
                        return (value >= min && value <= max);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                msgErro);
    }

    /**
     * Constroi um objeto que obtém um número de ponto flutuante dentro de um intervalo (inclusivo)
     *
     * @param prompt A questão a ser apresentada antes da entrada do usuário
     * @param min O menor valor permitido
     * @param max O maior valor permitido
     * @param msgErro A mensagem de erro a ser mostrada caso a validação/conversão falhe
     * @return Um objeto que obtém e valida entrada do usuário
     */
    public static EntradaUsuario<Double> fracionario(String prompt, double min, double max, String msgErro) {
        return new EntradaUsuario<>(
                false,
                prompt,
                (entrada) -> Double.valueOf(entrada),
                (entrada) -> {
                    try {
                        double value = Double.parseDouble(entrada);
                        return (value >= min && value <= max);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                msgErro);
    }

    /**
     * Constroi um objeto que obtém um valor monetário de duas ou nenhuma casa decimal e maior que zero
     *
     * @param prompt A questão a ser apresentada antes da entrada do usuário
     * @param msgErro A mensagem de erro a ser mostrada caso a validação/conversão falhe
     * @return Um objeto que obtém e valida entrada do usuário
     */
    public static EntradaUsuario<Double> valor(String prompt, String msgErro) {
        return new EntradaUsuario<>(
                false,
                prompt,
                (entrada) -> Double.valueOf(entrada.replace(',', '.')),
                (entrada) -> {
                    if(!Pattern.matches("^[0-9]+(?:[\\.,][0-9]{2})?$", entrada))
                        return false;
                    try {
                        double value = Double.parseDouble(entrada);
                        return (value >= 0);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                },
                msgErro);
    }
}
