package Cliente;
import User.UserCliente;
import Database.DatabaseJSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public static int cadastrarCliente(String nome, String email, String senha, String cpf) {

        JsonArray clientes = DatabaseJSON.carregarClientes();

        if (buscarPorCpf(cpf) != null) {
            System.out.println("Erro: CPF já cadastrado!");
            return -2;
        }

        int novoId = 1;
        if (clientes.size() > 0) {
            JsonObject ultimoCliente = clientes.get(clientes.size() - 1).getAsJsonObject();
            novoId = ultimoCliente.get("id").getAsInt() + 1;
        }

        JsonObject novoCliente = new JsonObject();
        novoCliente.addProperty("id", novoId);
        novoCliente.addProperty("nome", nome);
        novoCliente.addProperty("email", email);
        novoCliente.addProperty("senha", senha);
        novoCliente.addProperty("cpf", cpf);

        clientes.add(novoCliente);
        DatabaseJSON.salvarClientes(clientes);

        return novoId;
    }

    public static UserCliente buscarPorCpf(String cpf) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject cliente = element.getAsJsonObject();
            if (cliente.get("cpf").getAsString().equals(cpf)) {
                return new UserCliente(
                        cliente.get("id").getAsInt(),
                        cliente.get("nome").getAsString(),
                        cliente.get("email").getAsString(),
                        cliente.get("senha").getAsString(),
                        cliente.get("cpf").getAsString()
                );
            }
        }
        return null;
    }

    public static UserCliente buscarPorEmail(String email) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject cliente = element.getAsJsonObject();
            if (cliente.get("email").getAsString().equalsIgnoreCase(email)) {
                return new UserCliente(
                        cliente.get("id").getAsInt(),
                        cliente.get("nome").getAsString(),
                        cliente.get("email").getAsString(),
                        cliente.get("senha").getAsString(),
                        cliente.get("cpf").getAsString()
                );
            }
        }
        return null;
    }


    public static boolean validarLogin(String email, String senha) {

        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject cliente = element.getAsJsonObject();

            if (cliente.get("email").getAsString().equals(email) &&
                    cliente.get("senha").getAsString().equals(senha)) {
                return true;
            }
        }
        return false;
    }


    public static boolean atualizar(UserCliente cliente) {
        // Carrega todos os clientes
        JsonArray clientes = DatabaseJSON.carregarClientes();
        boolean encontrado = false;

        // Percorre a lista procurando pelo cliente com o ID correspondente
        for (int i = 0; i < clientes.size(); i++) {
            JsonObject clienteJson = clientes.get(i).getAsJsonObject();

            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                // Atualiza os dados do cliente
                clienteJson.addProperty("nome", cliente.getNome());
                clienteJson.addProperty("email", cliente.getEmail());
                clienteJson.addProperty("senha", cliente.getSenha());
                clienteJson.addProperty("cpf", cliente.getCpf());
                encontrado = true;
                break;
            }
        }

        if (encontrado) {
            // Salva as alterações no arquivo
            DatabaseJSON.salvarClientes(clientes);
            return true;
        }

        return false;
    }

    public static boolean remover(String email) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Erro: Email não pode ser vazio");
            return false;
        }

        // Carrega todos os clientes
        JsonArray clientes = DatabaseJSON.carregarClientes();

        // Procura o primeiro cliente com o email correspondente
        for (int i = 0; i < clientes.size(); i++) {
            JsonObject cliente = clientes.get(i).getAsJsonObject();
            if (cliente.get("email").getAsString().equalsIgnoreCase(email)) {
                // Remove o cliente encontrado
                JsonObject clienteRemovido = clientes.remove(i).getAsJsonObject();

                // Salva as alterações
                DatabaseJSON.salvarClientes(clientes);

                System.out.println("Cliente removido com sucesso:");
                System.out.println("ID: " + clienteRemovido.get("id"));
                System.out.println("Nome: " + clienteRemovido.get("nome"));
                return true;
            }
        }

        System.out.println("Nenhum cliente encontrado com o email: " + email);
        return false;
    }

    public static List<UserCliente> listarTodos() {
        List<UserCliente> listaClientes = new ArrayList<>();

        try {
            JsonArray clientesJson = DatabaseJSON.carregarClientes();

            if (clientesJson != null) {
                for (JsonElement element : clientesJson) {
                    try {
                        JsonObject clienteJson = element.getAsJsonObject();

                        UserCliente cliente = new UserCliente(
                                clienteJson.get("id").getAsInt(),
                                clienteJson.get("nome").getAsString(),
                                clienteJson.get("email").getAsString(),
                                clienteJson.get("senha").getAsString(),
                                clienteJson.get("cpf").getAsString()
                        );

                        listaClientes.add(cliente);
                    } catch (Exception e) {
                        System.err.println("Erro ao processar cliente: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar lista de clientes: " + e.getMessage());
        }

        return listaClientes;
    }

}

