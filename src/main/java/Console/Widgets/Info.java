package Console.Widgets;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por mostrar informação na saída padrão
 */
public class Info {
    private final List<String> linhas;

    /**
     * Construtor
     */
    public Info() {
        this.linhas = new ArrayList<>();
    }

    /**
     * Adiciona uma linha de texto
     * @param linha Texto a ser mostrado
     * @return Esse objeto com a linha adicionada
     */
    public Info adicionarTexto(String linha) {
        this.linhas.add(linha);
        return this;
    }

    /**
     * Mostra todas as linhas
     * @param out Stream de saída
     */
    public void mostrar(PrintStream out) {
        for(String linha: this.linhas) {
            out.println(linha);
        }
    }

    public static void mostrar(PrintStream out, String... linhas) {
        Info info = new Info();
        for(String linha: linhas) {
            info.adicionarTexto(linha);
        }
        info.mostrar(out);
    }
}
