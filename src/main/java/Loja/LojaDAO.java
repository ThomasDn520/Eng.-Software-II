package Loja;

import Database.DatabaseJSON;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class LojaDAO {

    // === CRUD COMPLETO ===

    public static int cadastrarLoja(String nome, String email, String senha, String cnpj) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        // Verifica se CNPJ ou email já existem
        if (buscarPorCnpj(cnpj) != null) {
            System.out.println("Erro: CNPJ já cadastrado!");
            return -1;
        }
        if (buscarPorEmail(email) != null) {
            System.out.println("Erro: Email já cadastrado!");
            return -2;
        }

        // Gera novo ID
        int novoId = lojas.size() > 0 ?
                lojas.get(lojas.size()-1).getAsJsonObject().get("id").getAsInt() + 1 : 1;

        // Cria nova loja
        JsonObject novaLoja = new JsonObject();
        novaLoja.addProperty("id", novoId);
        novaLoja.addProperty("nome", nome);
        novaLoja.addProperty("email", email);
        novaLoja.addProperty("senha", senha);
        novaLoja.addProperty("cnpj", cnpj);

        // Adiciona e salva
        lojas.add(novaLoja);
        DatabaseJSON.salvarLojas(lojas);

        System.out.println("Loja cadastrada com sucesso! ID: " + novoId);
        return novoId;
    }

    public static UserLoja validarLogin(String email, String senha) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject loja = element.getAsJsonObject();
            if (loja.get("email").getAsString().equalsIgnoreCase(email) &&
                    loja.get("senha").getAsString().equals(senha)) {
                return new UserLoja(
                        loja.get("id").getAsInt(),
                        loja.get("nome").getAsString(),
                        email,
                        senha,
                        loja.get("cnpj").getAsString()
                );
            }
        }
        return null;
    }

    public static UserLoja buscarPorEmail(String email) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject loja = element.getAsJsonObject();
            if (loja.get("email").getAsString().equalsIgnoreCase(email)) {
                return new UserLoja(
                        loja.get("id").getAsInt(),
                        loja.get("nome").getAsString(),
                        email,
                        loja.get("senha").getAsString(),
                        loja.get("cnpj").getAsString()
                );
            }
        }
        return null; // Retorna null caso a loja não seja encontrada
    }


    public static UserLoja buscarPorCnpj(String cnpj) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject loja = element.getAsJsonObject();
            if (loja.get("cnpj").getAsString().equals(cnpj)) {
                return new UserLoja(
                        loja.get("id").getAsInt(),
                        loja.get("nome").getAsString(),
                        loja.get("email").getAsString(),
                        loja.get("senha").getAsString(),
                        cnpj
                );
            }
        }
        return null;
    }

    public static boolean removerLoja(String cnpj) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (int i = 0; i < lojas.size(); i++) {
            JsonObject loja = lojas.get(i).getAsJsonObject();
            if (loja.get("cnpj").getAsString().equals(cnpj)) {
                lojas.remove(i);
                DatabaseJSON.salvarLojas(lojas);
                System.out.println("Loja removida com sucesso!");
                return true;
            }
        }

        System.out.println("Loja com CNPJ " + cnpj + " não encontrada");
        return false;
    }

    public static List<UserLoja> listarTodas() {
        List<UserLoja> todasLojas = new ArrayList<>();
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject loja = element.getAsJsonObject();
            todasLojas.add(new UserLoja(
                    loja.get("id").getAsInt(),
                    loja.get("nome").getAsString(),
                    loja.get("email").getAsString(),
                    loja.get("senha").getAsString(),
                    loja.get("cnpj").getAsString()
            ));
        }

        return todasLojas;
    }

    // funcao de atualizar loja ainda nao esta funcionando completamente
    public static boolean atualizar(UserLoja lojaAtualizada) {
        JsonArray lojas = DatabaseJSON.carregarLojas();
        boolean encontrada = false;

        // Verifica se novo email já existe (para outra loja)
        for (JsonElement element : lojas) {
            JsonObject l = element.getAsJsonObject();
            if (l.get("id").getAsInt() != lojaAtualizada.getId() &&
                    l.get("email").getAsString().equalsIgnoreCase(lojaAtualizada.getEmail())) {
                System.out.println("Erro: Email já está em uso por outra loja");
                return false;
            }
        }

        // Atualiza a loja
        for (int i = 0; i < lojas.size(); i++) {
            JsonObject loja = lojas.get(i).getAsJsonObject();
            if (loja.get("id").getAsInt() == lojaAtualizada.getId()) {
                loja.addProperty("nome", lojaAtualizada.getNome());
                loja.addProperty("email", lojaAtualizada.getEmail());
                loja.addProperty("senha", lojaAtualizada.getSenha());
                loja.addProperty("cnpj", lojaAtualizada.getCnpj());
                encontrada = true;
                break;
            }
        }

        if (encontrada) {
            DatabaseJSON.salvarLojas(lojas);
            System.out.println("Loja atualizada com sucesso!");
            return true;
        }

        System.out.println("Erro: Loja não encontrada");
        return false;
    }
}