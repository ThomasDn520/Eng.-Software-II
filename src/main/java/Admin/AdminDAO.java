package Admin;

import Database.DatabaseJSON;
import User.UserAdmin;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public static boolean validarLogin(int id, String senha) {
        UserAdmin admin = buscarPorId(id);
        return admin != null && admin.validarSenha(senha);
    }

    public static int cadastrarAdmin(String nome, String email, String senha) {
        // Carrega os dados atuais
        JsonArray admins = DatabaseJSON.carregarAdmins();

        // Verifica se o email já existe
        if (buscarPorEmail(email) != null) {
            System.out.println("Erro: Email já cadastrado!");
            return -1;
        }

        // Gera o novo ID (último ID + 1)
        int novoId = 1;
        if (admins.size() > 0) {
            JsonObject ultimoAdmin = admins.get(admins.size()-1).getAsJsonObject();
            novoId = ultimoAdmin.get("id").getAsInt() + 1;
        }

        // Cria o novo admin
        JsonObject novoAdmin = new JsonObject();
        novoAdmin.addProperty("id", novoId);
        novoAdmin.addProperty("nome", nome);
        novoAdmin.addProperty("email", email);
        novoAdmin.addProperty("senha", senha);

        // Adiciona e salva
        admins.add(novoAdmin);
        DatabaseJSON.salvarAdmins(admins);

        return novoId;
    }

    public static List<UserAdmin> listarTodos() {
        List<UserAdmin> listaAdmins = new ArrayList<>();
        JsonArray admins = DatabaseJSON.carregarAdmins();

        for (JsonElement element : admins) {
            JsonObject admin = element.getAsJsonObject();
            listaAdmins.add(new UserAdmin(
                    admin.get("id").getAsInt(),
                    admin.get("nome").getAsString(),
                    admin.get("email").getAsString(),
                    admin.get("senha").getAsString()
            ));
        }
        return listaAdmins;
    }

    public static UserAdmin buscarPorId(int id) {
        JsonArray admins = DatabaseJSON.carregarAdmins();

        for (JsonElement element : admins) {
            JsonObject admin = element.getAsJsonObject();
            if (admin.get("id").getAsInt() == id) {
                return new UserAdmin(
                        id,
                        admin.get("nome").getAsString(),
                        admin.get("email").getAsString(),
                        admin.get("senha").getAsString()
                );
            }
        }
        return null;
    }

    public static UserAdmin buscarPorEmail(String email) {
        JsonArray admins = DatabaseJSON.carregarAdmins();

        for (JsonElement element : admins) {
            JsonObject admin = element.getAsJsonObject();
            if (admin.get("email").getAsString().equalsIgnoreCase(email)) {
                return new UserAdmin(
                        admin.get("id").getAsInt(),
                        admin.get("nome").getAsString(),
                        admin.get("email").getAsString(),
                        admin.get("senha").getAsString()
                );
            }
        }
        return null;
    }

    public static UserAdmin autenticar(int id, String senha) {
        UserAdmin admin = buscarPorId(id);
        return (admin != null && admin.validarSenha(senha)) ? admin : null;
    }
}