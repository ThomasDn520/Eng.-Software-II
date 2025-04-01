package Produto;

import Database.DatabaseJSON;
import User.UserLoja;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // Método para adicionar um produto a uma loja específica
    public static boolean adicionarProduto(UserLoja loja, Produto produto) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.get("id").getAsInt() == loja.getId()) {

                // Obtém ou cria a lista de produtos dentro da loja
                JsonArray produtos;
                if (lojaJson.has("produtos")) {
                    produtos = lojaJson.getAsJsonArray("produtos");
                } else {
                    produtos = new JsonArray();
                }

                // Verifica se o produto já existe na loja
                for (JsonElement prodElement : produtos) {
                    if (prodElement.getAsJsonObject().get("nome").getAsString().equalsIgnoreCase(produto.getNome())) {
                        System.out.println("Erro: Produto já existe nesta loja!");
                        return false;
                    }
                }

                // Adiciona o novo produto
                JsonObject novoProduto = new JsonObject();
                novoProduto.addProperty("nome", produto.getNome());
                novoProduto.addProperty("valor", produto.getValor());
                novoProduto.addProperty("tipo", produto.getTipo());
                novoProduto.addProperty("quantidade", produto.getQuantidade());
                novoProduto.addProperty("marca", produto.getMarca());
                novoProduto.addProperty("descricao", produto.getDescricao());

                produtos.add(novoProduto);
                lojaJson.add("produtos", produtos);

                DatabaseJSON.salvarLojas(lojas);
                System.out.println("Produto cadastrado com sucesso!");
                return true;
            }
        }
        System.out.println("Erro: Loja não encontrada!");
        return false;
    }

    // Método para remover um produto de uma loja pelo nome
    public static boolean removerProduto(UserLoja loja, String nomeProduto) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.get("id").getAsInt() == loja.getId() && lojaJson.has("produtos")) {
                JsonArray produtos = lojaJson.getAsJsonArray("produtos");

                for (int i = 0; i < produtos.size(); i++) {
                    JsonObject produto = produtos.get(i).getAsJsonObject();
                    if (produto.get("nome").getAsString().equalsIgnoreCase(nomeProduto)) {
                        produtos.remove(i);
                        lojaJson.add("produtos", produtos);
                        DatabaseJSON.salvarLojas(lojas);
                        System.out.println("Produto removido com sucesso!");
                        return true;
                    }
                }
                System.out.println("Erro: Produto não encontrado!");
                return false;
            }
        }
        System.out.println("Erro: Loja não encontrada!");
        return false;
    }

    // Método para listar os produtos de uma loja
    public static List<Produto> listarProdutos(UserLoja loja) {
        List<Produto> listaProdutos = new ArrayList<>();
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.get("id").getAsInt() == loja.getId() && lojaJson.has("produtos")) {
                JsonArray produtos = lojaJson.getAsJsonArray("produtos");

                for (JsonElement prodElement : produtos) {
                    JsonObject produtoJson = prodElement.getAsJsonObject();
                    Produto produto = new Produto(
                            produtoJson.get("nome").getAsString(),
                            produtoJson.get("valor").getAsDouble(),
                            produtoJson.get("tipo").getAsString(),
                            produtoJson.get("quantidade").getAsInt(),
                            produtoJson.get("marca").getAsString(),
                            produtoJson.get("descricao").getAsString()
                    );
                    listaProdutos.add(produto);
                }
                break;
            }
        }
        return listaProdutos;
    }

    // Método para buscar um produto específico dentro de uma loja
    public static Produto buscarProduto(UserLoja loja, String nomeProduto) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.get("id").getAsInt() == loja.getId() && lojaJson.has("produtos")) {
                JsonArray produtos = lojaJson.getAsJsonArray("produtos");

                for (JsonElement prodElement : produtos) {
                    JsonObject produtoJson = prodElement.getAsJsonObject();
                    if (produtoJson.get("nome").getAsString().equalsIgnoreCase(nomeProduto)) {
                        return new Produto(
                                produtoJson.get("nome").getAsString(),
                                produtoJson.get("valor").getAsDouble(),
                                produtoJson.get("tipo").getAsString(),
                                produtoJson.get("quantidade").getAsInt(),
                                produtoJson.get("marca").getAsString(),
                                produtoJson.get("descricao").getAsString()
                        );
                    }
                }
            }
        }
        System.out.println("Produto não encontrado!");
        return null;
    }
}
