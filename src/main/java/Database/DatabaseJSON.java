package Database;

import User.*;

import java.io.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class DatabaseJSON {

    public static final String FILE_NAME_ADMIN = "database.json";       // Admins
    public static final String FILE_NAME_CLIENTE = "database_cliente.json"; // Clientes
    public static final String FILE_NAME_LOJA = "database_loja.json";   // Lojas
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    // Tipos para desserialização
    private static final Type ADMIN_LIST_TYPE = new TypeToken<List<UserAdmin>>(){}.getType();
    private static final Type CLIENTE_LIST_TYPE = new TypeToken<List<UserCliente>>(){}.getType();
    private static final Type LOJA_LIST_TYPE = new TypeToken<List<UserLoja>>(){}.getType();

    // Método para inicializar todos os JSONs
    public static void inicializarJSON() {
        criarArquivoSeNaoExistir(FILE_NAME_ADMIN, "admins");
        criarArquivoSeNaoExistir(FILE_NAME_CLIENTE, "clientes");
        criarArquivoSeNaoExistir(FILE_NAME_LOJA, "lojas");
    }

    private static void criarArquivoSeNaoExistir(String fileName, String arrayName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(fileName)) {
                JsonObject root = new JsonObject();
                root.add(arrayName, new JsonArray());
                gson.toJson(root, writer);
                System.out.println("Arquivo " + fileName + " criado com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao criar " + fileName + ": " + e.getMessage());
            }
        }
    }

    // Métodos para carregar dados

    public static JsonArray carregarAdmins() {
        return carregarArray(FILE_NAME_ADMIN, "admins");
    }

    public static JsonArray carregarClientes() {
        return carregarArray(FILE_NAME_CLIENTE, "clientes");
    }

    public static JsonArray carregarLojas() {
        return carregarArray(FILE_NAME_LOJA, "lojas");
    }

    private static JsonArray carregarArray(String fileName, String arrayName) {
        try (FileReader reader = new FileReader(fileName)) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            return root.getAsJsonArray(arrayName);
        } catch (IOException e) {
            System.out.println("Erro ao carregar " + fileName + ": " + e.getMessage());
            return new JsonArray(); // Retorna array vazio em caso de erro
        }
    }

    // Métodos para salvar dados

    public static void salvarAdmins(JsonArray admins) {
        salvarArray(FILE_NAME_ADMIN, "admins", admins);
    }

    public static void salvarClientes(JsonArray clientes) {
        salvarArray(FILE_NAME_CLIENTE, "clientes", clientes);
    }

    public static void salvarLojas(JsonArray lojas) {
        salvarArray(FILE_NAME_LOJA, "lojas", lojas);
    }

    private static void salvarArray(String fileName, String arrayName, JsonArray array) {
        try (FileWriter writer = new FileWriter(fileName)) {
            JsonObject root = new JsonObject();
            root.add(arrayName, array);
            gson.toJson(root, writer);
        } catch (IOException e) {
            System.out.println("Erro ao salvar " + fileName + ": " + e.getMessage());
        }
    }
}