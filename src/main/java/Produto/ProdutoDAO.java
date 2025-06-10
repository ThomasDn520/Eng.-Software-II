package Produto;

import Database.DatabaseJSON;
import User.UserLoja;
import User.User;
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
                        return true;
                    }
                }
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

    public static Produto[] buscarTodosProdutos() {
        List<Produto> listaProdutos = new ArrayList<>();
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.has("produtos")) {
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

                    produto.setLoja(lojaJson.get("nome").getAsString());
                    if(produto.getQuantidade() > 0){
                        listaProdutos.add(produto);
                    }
                }
            }
        }

        return listaProdutos.toArray(new Produto[0]);
    }

    public static boolean atualizarEstoqueLojaCompra(String lojaNome, String nomeProduto, int quantidadeCompra) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement lojaElement : lojas) {
            JsonObject lojaJson = lojaElement.getAsJsonObject();
            String nomeLoja = lojaJson.get("nome").getAsString();

            if (nomeLoja.equalsIgnoreCase(lojaNome)) {
                JsonArray produtos = lojaJson.getAsJsonArray("produtos");

                for (JsonElement produtoElement : produtos) {
                    JsonObject produtoJson = produtoElement.getAsJsonObject();
                    String nome = produtoJson.get("nome").getAsString();
                    int quantidade = produtoJson.get("quantidade").getAsInt();

                    if (nome.equalsIgnoreCase(nomeProduto)) {
                        if (quantidade < quantidadeCompra) {
                            System.out.println("Estoque insuficiente para " + nomeProduto);
                            return false;
                        }

                        produtoJson.addProperty("quantidade", quantidade - quantidadeCompra);
                        DatabaseJSON.salvarLojas(lojas);
                        return true;
                    }
                }
            }
        }

        System.out.println("Produto não encontrado na loja.");
        return false;
    }



    public static boolean adicionarAvaliacao(User usuario, String nomeLoja, String nomeProduto, int nota, String comentario) {
        // 1. Validação da nota
        if (nota < 1 || nota > 5) {
            System.out.println("Nota inválida. Deve estar entre 1 e 5.");
            return false;
        }

        // 2. Impede que lojas avaliem produtos
        if (usuario instanceof UserLoja) {
            System.out.println("Lojas não podem avaliar produtos.");
            return false;
        }

        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement lojaElement : lojas) {
            JsonObject lojaJson = lojaElement.getAsJsonObject();
            String nomeDaLoja = lojaJson.get("nome").getAsString();

            if (nomeDaLoja.equalsIgnoreCase(nomeLoja)) {
                JsonArray produtos = lojaJson.getAsJsonArray("produtos");

                for (JsonElement produtoElement : produtos) {
                    JsonObject produtoJson = produtoElement.getAsJsonObject();

                    if (produtoJson.get("nome").getAsString().equalsIgnoreCase(nomeProduto)) {
                        // 3. Recupera ou inicializa o array de avaliações
                        JsonArray avaliacoes = produtoJson.has("avaliacoes")
                                ? produtoJson.getAsJsonArray("avaliacoes")
                                : new JsonArray();

                        // 4. Cria nova avaliação
                        JsonObject avaliacaoJson = new JsonObject();
                        avaliacaoJson.addProperty("usuario", usuario.getNome()); // opcional mas útil
                        avaliacaoJson.addProperty("nota", nota);
                        avaliacaoJson.addProperty("comentario", comentario == null ? "" : comentario);

                        // 5. Adiciona e salva
                        avaliacoes.add(avaliacaoJson);
                        produtoJson.add("avaliacoes", avaliacoes);
                        DatabaseJSON.salvarLojas(lojas);

                        System.out.println("Avaliação adicionada com sucesso!");
                        return true;
                    }
                }
            }
        }

        System.out.println("Produto ou loja não encontrados.");
        return false;
    }

    public static JsonArray buscarProdutosPorLoja(String nomeLoja) {
        JsonArray lojas = DatabaseJSON.carregarLojas();

        for (JsonElement element : lojas) {
            JsonObject lojaJson = element.getAsJsonObject();
            if (lojaJson.get("nome").getAsString().equalsIgnoreCase(nomeLoja) && lojaJson.has("produtos")) {
                return lojaJson.getAsJsonArray("produtos");
            }
        }
        return new JsonArray(); // Retorna vazio se não encontrar a loja
    }


    public static void setProdutos(Produto[] novosProdutos) {
        Object produtos = novosProdutos;
    }
}
