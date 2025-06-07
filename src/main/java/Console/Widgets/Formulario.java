package Console.Widgets;

import Console.EmptyInputException;
import Console.EntradaUsuario;
import Console.InvalidInputException;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

/**
 * Classe responsável pela construção e display de formulários
 * Formulários obtém dados do console, validam e então convertem para os tipos de dados corretos
 * As respostas do usuário serão armazenadas e recuperadas por "chaves", de modo similar a um dicionário
 */
public class Formulario {
    private final List<String> cabecalhos;
    private final HashMap<String, EntradaUsuario> questoes;

    /**
     * Construtor
     */
    public Formulario() {
        this.cabecalhos = new ArrayList<>();
        this.questoes = new LinkedHashMap<>();
    }

    /**
     * Adiciona uma pergunta por nome próprio
     * <p><i>Nomes de pessoa devem conter somente letras separados por um único espaço</i></p>
     * <p><i>Os nomes obtidos serão capitalizados</i></p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarNome(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^[A-zÀ-ú]{2,}(?: [A-zÀ-ú]{2,})*$",
                (texto) -> {
                    // Separa o nome por espaços
                    String[] nomes = texto.trim().split(" ");

                    // Capitaliza cada palavra
                    StringBuilder resultado = new StringBuilder();
                    for(String nome: nomes) {
                        String primaLetra = nome.substring(0, 1).toUpperCase();
                        String resto = nome.substring(1).toLowerCase();
                        resultado.append(primaLetra).append(resto).append(" ");
                    }

                    // A resposta vai ter um espaço ao final, remove último espaço
                    return resultado.toString().trim();
                },
                "Não é um nome de pessoa válido.");
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por nome de loja
     * <p><i>Nomes de loja devem conter somente letras ou números separados por um único espaço</i></p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarLoja(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^[A-zÀ-ú0-9]{2,}(?: [A-zÀ-ú0-9]{2,})*$",
                (texto) -> texto, // faz nada
                "Não é um nome de loja válido.");
        this.questoes.put(chave, entrada);
        return this;
    }


    /**
     * Adiciona uma pergunta por número de CPF
     * <p>CPF pode estar com ou sem pontuação
     * no padrão <i>AAA.BBB.CCC-DD</i> ou no padrão <i>AAABBBCCCDD</i>.
     * A, B, C e D são números
     * </p>
     * <p>Os CPFs recebidos terão a pontuação removida.</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarCPF(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^(?:\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}|\\d{11})$",
                (texto) -> texto.replaceAll("[\\.\\-]", ""),
                "O dado informado não é um número de CPF válido.");
        this.questoes.put(chave, entrada);
        return this;
    }


    /**
     * Adiciona uma pergunta por número de CNPJ
     * <p>CNPJ pode estar com ou sem pontuação
     * no padrão <i>AA.BBB.CCC/000D-EE</i> ou no padrão <i>AABBBCCC000DEE</i>.
     * A, B, C e E são números e D é 1 ou 2
     * </p>
     * <p>Os CNPJs recebidos terão a pontuação removida.</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarCNPJ(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^(?:\\d{2}\\.\\d{3}\\.\\d{3}\\/000[12]-\\d{2}|\\d{8}000[12]\\d{2})$",
                (texto) -> texto.replaceAll("[\\.\\-\\/]", ""),
                "O dado informado não é um número de CNPJ válido.");
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por endereço de email
     * <p>Só serão permitidos emails no padrão <i>usuario@provedor.dominio</i>, no qual:
     * <ul>
     *     <li><i>usuario</i> contém ao menos três caracteres, sendo eles alfanuméricos, ponto, hífen ou underscore. Deve iniciar e terminar com caractere alfanumérico;</li>
     *     <li><i>provedor</i> contém ao menos três caracteres alfanuméricos;</li>
     *     <li><i>domínio</i> contém ao menos duas letras.</li>
     * </ul>
     * </p>
     * <p>Os emails recebidos serão convertidos para caixa baixa (lowercase)</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarEmail(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^\\w+[\\w\\.\\-_].*?\\w+@[\\w]{3,}\\.[a-zA-Z]{2,8}$",
                (texto) -> texto.toLowerCase(),
                "O dado informado não é um endereço de email válido.");
        this.questoes.put(chave, entrada);
        return this;
    }


    /**
     * Adiciona uma pergunta por senha
     * <p>Uma senha deve conter pelo menos oito caracteres</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarSenha(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(
                prompt,
                "^.{8,}$",
                (texto) -> texto,
                "A senha informada é muito curta. Deve conter pelo menos oito caracteres.");
        this.questoes.put(chave, entrada);
        return this;
    }


    /**
     * Adiciona uma pergunta por valor monetário
     * <p>Somente aceita valores com nenhuma ou duas casas decimais, separados por ponto ou vírgula
     * no padrão <i>A.BB</i>, <i>A,BB</i> ou somente <i>A</i>,
     * no qual A >= 0 e B é número entre 0 e 9
     * </p>
     * <p>Os valores recebidos serão convertidos para ponto flutuante (Double).</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarValor(String chave, String prompt) {
        EntradaUsuario<Double> entrada = EntradaUsuario.valor(prompt,
                "O dado informado não é um valor válido, positivo e com nenhuma ou duas casas decimais.");
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por quantidade de itens
     * <p>Somente serão aceitos valores inteiros maiores que zero.</p>
     * <p>Os valores recebidos serão convertidos para inteiro (Integer).</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarQuantidade(String chave, String prompt) {
        EntradaUsuario<Integer> entrada = EntradaUsuario.inteiro(prompt, 1, Integer.MAX_VALUE,
                "O dado informado não é uma quantidade válida.");
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por um índice seletor de opção
     * <p>Somente serão aceitos valores entre zero e um limite máximo (inclusivo).</p>
     * <p>Os valores recebidos serão convertidos para inteiro (Integer).</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @param max O valor máximo a ser aceito
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarOpcao(String chave, String prompt, int max) {
        EntradaUsuario<Integer> entrada = EntradaUsuario.inteiro(prompt, 0, max,
                String.format("O dado informado não é uma opção válida entre 0 e %d.", max));
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por uma nota de avaliação
     * <p>Somente serão aceitos valores entre um limite mínimo e um limite máximo (inclusivo).</p>
     * <p>Os valores recebidos serão convertidos para inteiro (Integer).</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @param min A nota mínima a ser aceita
     * @param max A nota máxima a ser aceita
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarNota(String chave, String prompt, int min, int max) {

        EntradaUsuario<Integer> entrada = EntradaUsuario.inteiro(prompt, min, max,
                String.format("O dado informado não é uma nota válida entre %d e %d.", min, max));
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por texto opcional
     * <p>Será aceito qualquer texto.</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarTextoOpcional(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(prompt);
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Adiciona uma pergunta por texto
     * <p>Não serão aceitos textos vazios ou só com espaços.</p>
     * @param chave A chave na qual a resposta será armazenada
     * @param prompt O texto que solicita a entrada do usuário
     * @return Instância de formulário com a pergunta adicionada
     */
    public Formulario perguntarTexto(String chave, String prompt) {
        EntradaUsuario<String> entrada = EntradaUsuario.texto(prompt, "[^\\s]+", (texto) -> texto, "Essa mensagem não deveria ser mostrada!");
        this.questoes.put(chave, entrada);
        return this;
    }

    /**
     * Obtém a resposta para uma pergunta
     * @param chave A chave na qual a pergunta e resposta está armazenada
     * @return A resposta do usuário para a pergunta
     */
    public String getTexto(String chave) {
        return (String) this.questoes.get(chave).getResposta();
    }

    /**
     * Obtém a resposta para uma pergunta convertida para inteiro
     * @param chave A chave na qual a pergunta e resposta está armazenada
     * @return A resposta do usuário para a pergunta
     */
    public int getInteiro(String chave) {
        return (Integer) this.questoes.get(chave).getResposta();
    }

    /**
     * Obtém a resposta para uma pergunta convertida para valor em ponto flutuante
     * @param chave A chave na qual a pergunta e resposta está armazenada
     * @return A resposta do usuário para a pergunta
     */
    public double getValor(String chave) {
        return (Double) this.questoes.get(chave).getResposta();
    }

    /**
     * Adiciona uma linha de texto a ser mostrada antes das perguntas ao usuário
     * @param texto Texto que será mostrado
     */
    public Formulario adicionarCabecalho(String texto) {
        this.cabecalhos.add(texto);
        return this;
    }

    /**
     * Mostra esse formulário na seguinte ordem:
     * <ol>
     *     <li>Cabeçalhos;</li>
     *     <li>Perguntas e entradas do usuário.</li>
     * </ol>
     * Para cancelar a entrada de dados, basta responder a uma pergunta com uma string vazia
     * @param in Stream de entrada de dados
     * @param out Stream de saída de dados
     * @return true se o formulário foi completado, false se foi cancelado
     */
    public boolean mostrar(InputStream in, PrintStream out) {
        out.println();

        for(String texto: this.cabecalhos)
            out.println(texto);

        out.println();
        for(Map.Entry<String, EntradaUsuario> item: this.questoes.entrySet()) {
            EntradaUsuario questao = item.getValue();
            do {
                try {
                    questao.perguntar(in, out);
                } catch (InvalidInputException e) {
                    out.printf("%s Tente novamente\n", e.getMessage());
                } catch (EmptyInputException e) { // quer cancelar a entrada, digite nada
                    out.println("Cancelando operação...");
                    return false;
                }
            } while(questao.getResposta() == null);
        }

        out.println();
        return true;
    }
}
