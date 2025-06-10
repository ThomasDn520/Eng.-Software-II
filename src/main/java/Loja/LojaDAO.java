package Loja;

import Database.DatabaseJSON;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;

public class LojaDAO {

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

    public static boolean existeLoja(String nomeLoja) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement elemento : lojas) {
            JsonObject loja = elemento.getAsJsonObject();
            if (loja.get("nome").getAsString().equalsIgnoreCase(nomeLoja)) {
                return true;
            }
        }
        return false;
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

    // Atualiza loja, verificando se email não está duplicado
    public static boolean atualizar(UserLoja lojaAtualizada) {
        JsonArray lojas = DatabaseJSON.carregarLojas();
        boolean encontrada = false;

        // Verifica se novo email já existe para outra loja
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

    // Método para adicionar avaliação na loja
    public static boolean adicionarAvaliacaoLoja(int idCliente, String nomeLoja, int nota, String comentario) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement elem : lojas) {
            JsonObject loja = elem.getAsJsonObject();
            if (loja.get("nome").getAsString().equalsIgnoreCase(nomeLoja)) {

                JsonArray avaliacoes;
                if (loja.has("avaliacoes") && loja.get("avaliacoes").isJsonArray()) {
                    avaliacoes = loja.getAsJsonArray("avaliacoes");
                } else {
                    avaliacoes = new JsonArray();
                    loja.add("avaliacoes", avaliacoes);
                }

                JsonObject novaAvaliacao = new JsonObject();
                novaAvaliacao.addProperty("idCliente", idCliente);
                novaAvaliacao.addProperty("nota", nota);
                novaAvaliacao.addProperty("comentario", comentario);

                avaliacoes.add(novaAvaliacao);

                // Salvar o JSON atualizado
                DatabaseJSON.salvarLojas(lojas);

                System.out.println("Avaliação adicionada com sucesso para a loja: " + nomeLoja);
                return true;
            }
        }

        System.out.println("Loja não encontrada para avaliação.");
        return false; // Loja não encontrada
    }

    // TODO: Remover, só está sendo usado por código morto e testes
    // Retorna nota média e conceito da loja
    public static String obterNotaEConceitoLoja(String nomeLoja) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement elemento : lojas) {
            JsonObject lojaJson = elemento.getAsJsonObject();
            if (lojaJson.get("nome").getAsString().equalsIgnoreCase(nomeLoja)) {

                if (!lojaJson.has("avaliacoes") || lojaJson.getAsJsonArray("avaliacoes").size() == 0) {
                    return "Esta loja ainda não possui avaliações.";
                }

                JsonArray avaliacoes = lojaJson.getAsJsonArray("avaliacoes");
                double soma = 0;
                int quantidade = avaliacoes.size();

                for (JsonElement elem : avaliacoes) {
                    JsonObject avaliacao = elem.getAsJsonObject();
                    soma += avaliacao.get("nota").getAsDouble();
                }

                double media = soma / quantidade;

                String conceito;
                if (media >= 4.5) {
                    conceito = "Excelente";
                } else if (media >= 3.5) {
                    conceito = "Bom";
                } else if (media >= 2.5) {
                    conceito = "Regular";
                } else if (media >= 1.5) {
                    conceito = "Ruim";
                } else {
                    conceito = "Péssimo";
                }

                return String.format("Nota média: %.2f (%s)", media, conceito);
            }
        }

        return "Loja não encontrada.";
    }

    /**
     * Lista as notas de uma loja
     * @param cnpj O cnpj da loja
     * @return A lista de notas ou null se a loja não foi encontrada
     */
    public static List<Double> listarNotas(String cnpj) {
        JsonObject lojaJson = null;

        for(JsonElement elem: DatabaseJSON.carregarLojas()) {
            JsonObject json = elem.getAsJsonObject();
            if(json.get("cnpj").getAsString().equals(cnpj)) {
                lojaJson = json;
                break;
            }
        }

        // loja não encontrada (isso não deveria acontecer)
        if(lojaJson == null)
            return null;

        List<Double> notas = new ArrayList<>();

        // loja não tem avaliações: retorne lista vazia
        if(!lojaJson.has("avaliacoes") || lojaJson.get("avaliacoes").getAsJsonArray().isEmpty())
            return notas;

        // adiciona notas à lista
        for(JsonElement elem: lojaJson.getAsJsonArray("avaliacoes")) {
            JsonObject avaliacaoJson = elem.getAsJsonObject();
            notas.add(avaliacaoJson.get("nota").getAsDouble());
        }

        return notas;
    }
}
