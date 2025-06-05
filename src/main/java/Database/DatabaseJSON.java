package Database;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Classe de database
 * Armazenamento em JSON no arquivo "database.json" por padrão
 */
public class DatabaseJSON {
    private static final Logger logger = Logger.getLogger("JSON");
    private static String FILE_NAME = "database.json";

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Inicializa arquivo JSON, populando com as entradas necessárias
     */
    public static void inicializarJSON() {
        criarArquivoSeNaoExistir("admins");
        criarArquivoSeNaoExistir("clientes");
        criarArquivoSeNaoExistir("lojas");
        criarArquivoSeNaoExistir("produtos");
    }

    /**
     * Altera o arquivo padrão para salvamento
     * @param file O novo arquivo json
     */
    public static void setFile(String file) {
        FILE_NAME = file;
        inicializarJSON();
    }

    /**
     * Cria arquivo JSON e a chave se necessário
     * @param arrayName Nome da chave do JSON
     */
    private static void criarArquivoSeNaoExistir(String arrayName) {
        JsonArray arr = carregarArray(arrayName);

        if(arr == null) // se a chave não existe, array é nulo
            arr = new JsonArray(); // salva array vazio

        salvarArray(arrayName, arr);
    }

    // Métodos para carregar dados

    /**
     * Carrega os admins a partir do banco de dados
     * @return Um array com objetos JSON
     */
    public static JsonArray carregarAdmins() {
        return carregarArray("admins");
    }

    /**
     * Carrega os clientes a partir do banco de dados
     * @return Um array com objetos JSON
     */
    public static JsonArray carregarClientes() {
        return carregarArray("clientes");
    }

    /**
     * Carrega as lojas a partir do banco de dados
     * @return Um array com objetos JSON
     */
    public static JsonArray carregarLojas() {
        return carregarArray("lojas");
    }

    /**
     * Carrega os produtos a partir do banco de dados
     * @return Um array com objetos JSON
     */
    public static JsonArray carregarProdutos() {
        return carregarArray("produtos");
    }


    /**
     * Carrega as entradas de uma chave contida no banco de dados
     * @param arrayName O nome da chave JSON
     * @return Um array com objetos JSON
     */
    private static JsonArray carregarArray(String arrayName) {
        try (FileReader reader = new FileReader(FILE_NAME)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            return root.getAsJsonArray(arrayName);
        } catch (IOException e) {
            logger.warning("Erro ao carregar " + FILE_NAME + ": " + e.getMessage());
            return new JsonArray();
        }
    }

    // Métodos para salvar dados
    /**
     * Salva admins no banco de dados
     * @param admins Um array com objetos JSON
     */
    public static void salvarAdmins(JsonArray admins) {
        salvarArray("admins", admins);
    }

    /**
     * Salva clintes no banco de dados
     * @param clientes Um array com objetos JSON
     */
    public static void salvarClientes(JsonArray clientes) {
        salvarArray("clientes", clientes);
    }

    /**
     * Salva lojas no banco de dados
     * @param lojas Um array com objetos JSON
     */
    public static void salvarLojas(JsonArray lojas) {
        salvarArray("lojas", lojas);
    }

    /**
     * Salva produtos no banco de dados
     * @param produtos Um array com objetos JSON
     */
    public static void salvarProdutos(JsonArray produtos) {
        salvarArray("produtos", produtos);
    }

    /**
     * Salva um array de objetos no banco de dados
     * @param arrayName A chave do objeto JSON
     * @param array O array a ser salvo
     */
    private static void salvarArray(String arrayName, JsonArray array) {
        // ler json existente
        JsonObject root;
        try (FileReader reader = new FileReader(FILE_NAME)) {
            root = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            // não conseguiu ler o arquivo, talvez não exista ou não tenha permissão suficiente
            logger.warning("Erro ao ler " + FILE_NAME + ": " + e.getMessage());
            root = new JsonObject();
        }

        // adiciona novo array no json existente e salva
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            root.add(arrayName, array);
            gson.toJson(root, writer);
        } catch (IOException e) {
            logger.warning("Erro ao salvar " + FILE_NAME + ": " + e.getMessage());
        }
    }
}
