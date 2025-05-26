package Cliente;
import User.UserCliente;
import Database.DatabaseJSON;
import User.UserLoja;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Produto.ProdutoDAO;

import Carrinho.*;
import Produto.Produto;


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

    public static boolean adicionarProdutoAoCarrinho(UserCliente cliente, Produto produto, int quantidade) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                // Obtém ou cria o carrinho do cliente
                JsonObject carrinhoJson;
                if (clienteJson.has("carrinho")) {
                    carrinhoJson = clienteJson.getAsJsonObject("carrinho");
                } else {
                    carrinhoJson = new JsonObject();
                }

                // Cria o objeto Carrinho
                CarrinhoCompras carrinho = new CarrinhoCompras(cliente);

                // Adiciona os itens existentes ao carrinho
                if (carrinhoJson.has("itens")) {
                    JsonArray itens = carrinhoJson.getAsJsonArray("itens");
                    for (JsonElement itemElement : itens) {
                        JsonObject itemJson = itemElement.getAsJsonObject();
                        Produto p = new Produto(
                                itemJson.get("nome").getAsString(),
                                itemJson.get("valor").getAsDouble(),
                                itemJson.get("tipo").getAsString(),
                                itemJson.get("quantidade").getAsInt(),
                                itemJson.get("marca").getAsString(),
                                itemJson.get("descricao").getAsString()
                        );
                        int qtd = itemJson.get("quantidade").getAsInt();
                        carrinho.adicionarItem(new ItemCarrinho(p, qtd)); // Usando p aqui
                    }
                }

                // Verifica se o produto já está no carrinho
                if (carrinho.temItem(produto)) {
                    // Se o produto já estiver no carrinho, incrementa a quantidade
                    ItemCarrinho itemExistente = carrinho.getItem(produto);
                    itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
                } else {
                    // Caso contrário, adiciona o produto
                    carrinho.adicionarItem(new ItemCarrinho(produto, quantidade));
                }

                // Salva o carrinho no cliente
                JsonArray itensJson = new JsonArray();

                for (ItemCarrinho item : carrinho.getItens()) {
                    JsonObject itemJson = new JsonObject();

                    Produto pd = item.getNome(); // acessa o produto que está dentro do ItemCarrinho

                    // Aqui, deve usar o produto do item, que já contém os dados corretos
                    itemJson.addProperty("nome", pd.getNome());
                    itemJson.addProperty("valor", pd.getValor());
                    itemJson.addProperty("tipo", pd.getTipo());
                    itemJson.addProperty("marca", pd.getMarca());
                    itemJson.addProperty("descricao", pd.getDescricao());
                    itemJson.addProperty("quantidade", item.getQuantidade());
                    itemJson.addProperty("loja", pd.getLoja());

                    itensJson.add(itemJson);
                }

                carrinhoJson.add("itens", itensJson);
                clienteJson.add("carrinho", carrinhoJson);
                DatabaseJSON.salvarClientes(clientes);
                System.out.println("Produto adicionado ao carrinho com sucesso!");
                return true;
            }
        }

        System.out.println("Erro: Cliente não encontrado!");
        return false;
    }


    public static boolean exibirItensCarrinho(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                if (clienteJson.has("carrinho")) {
                    JsonObject carrinhoJson = clienteJson.getAsJsonObject("carrinho");

                    if (carrinhoJson.has("itens")) {
                        JsonArray itens = carrinhoJson.getAsJsonArray("itens");

                        if (itens.size() == 0) {
                            System.out.println("Carrinho vazio.");
                            return false;
                        }

                        System.out.println("\nItens no carrinho:");

                        for (JsonElement itemElement : itens) {
                            JsonObject itemJson = itemElement.getAsJsonObject();

                            String nome = itemJson.get("nome").getAsString();
                            double valor = itemJson.get("valor").getAsDouble();
                            String tipo = itemJson.get("tipo").getAsString();
                            String marca = itemJson.get("marca").getAsString();
                            String descricao = itemJson.get("descricao").getAsString();
                            int quantidade = itemJson.get("quantidade").getAsInt();

                            System.out.println(nome + " (" + tipo + ")");
                            System.out.println("  Marca: " + marca);
                            System.out.println("  Descrição: " + descricao);
                            System.out.println("  Valor: R$" + valor);
                            System.out.println("  Quantidade: " + quantidade);
                            System.out.println("--------------------------");
                        }

                        System.out.println("\nValor total do Carrinho: " + valorCarrinho(itens));

                        return true;

                    } else {
                        System.out.println("Carrinho vazio.");
                        return false;
                    }

                } else {
                    System.out.println("Carrinho não encontrado");
                    return false;
                }
            }
        }

        System.out.println("Cliente não encontrado.");
        return false;
    }

    public static Double valorCarrinho(JsonArray itens) {
        double valorTotal = 0;

        for (JsonElement itemElement : itens) {
            JsonObject itemJson = itemElement.getAsJsonObject();

            double valorUnitario = itemJson.get("valor").getAsDouble();
            int quantidade = itemJson.get("quantidade").getAsInt();

            valorTotal += valorUnitario * quantidade;
        }

        return valorTotal;
    }

    public static boolean removerProdutoDoCarrinho(UserCliente cliente, String nomeProduto) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                if (!clienteJson.has("carrinho")) {
                    System.out.println("Carrinho não encontrado.");
                    return false;
                }

                JsonObject carrinhoJson = clienteJson.getAsJsonObject("carrinho");

                if (!carrinhoJson.has("itens")) {
                    System.out.println("Carrinho vazio.");
                    return false;
                }

                JsonArray itens = carrinhoJson.getAsJsonArray("itens");
                JsonArray novosItens = new JsonArray();
                boolean encontrado = false;

                for (JsonElement itemElement : itens) {
                    JsonObject itemJson = itemElement.getAsJsonObject();
                    String nome = itemJson.get("nome").getAsString();

                    if (nome.equalsIgnoreCase(nomeProduto)) {
                        encontrado = true;
                        int quantidade = itemJson.get("quantidade").getAsInt();

                        if (quantidade > 1) {
                            // Remove apenas uma unidade
                            itemJson.addProperty("quantidade", quantidade - 1);
                            novosItens.add(itemJson);
                        }
                        // Se quantidade == 1, não adiciona novamente (remove o item por completo)
                    } else {
                        novosItens.add(itemJson);
                    }
                }

                if (encontrado) {
                    carrinhoJson.add("itens", novosItens);
                    clienteJson.add("carrinho", carrinhoJson);
                    DatabaseJSON.salvarClientes(clientes);
                    System.out.println("Produto removido do carrinho.");
                    return true;
                } else {
                    System.out.println("Produto não encontrado no carrinho.");
                    return false;
                }
            }
        }

        System.out.println("Cliente não encontrado.");
        return false;
    }

    public static boolean exibirHistoricoCompras(UserCliente cliente) {

        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                JsonArray historicoCompras;
                if (clienteJson.has("historicoCompras")) {
                    historicoCompras = clienteJson.getAsJsonArray("historicoCompras");
                } else {
                    historicoCompras = new JsonArray();
                    clienteJson.add("historicoCompras", historicoCompras);
                }

                if (historicoCompras.size() == 0) {
                    System.out.println("Nenhuma compra realizada ainda.");
                } else {
                    System.out.println("\nHistórico de Compras de " + cliente.getNome() + ":");
                    // for (JsonElement itemElement : historicoCompras) {
                    for(int i=0; i<historicoCompras.size(); i++) {
                        JsonElement itemElement = historicoCompras.get(i);
                        JsonObject item = itemElement.getAsJsonObject();
                        String nomeProduto = item.get("produto").getAsString();
                        String nomeLoja = item.get("loja").getAsString();
                        int quantidade = item.get("quantidade").getAsInt();
                        double valor = item.get("valor").getAsDouble();


                        System.out.println("<Compra Nº" + (i+1) + ">");
                        System.out.println("Produto: " + nomeProduto);
                        System.out.println("Loja: " + nomeLoja);
                        System.out.println("Quantidade: " + quantidade);
                        System.out.println("Valor: R$ " + valor);
                        System.out.println("-----------------------------");
                    }
                }

                DatabaseJSON.salvarClientes(clientes);
                return true;
            }
        }

        System.out.println("Cliente não encontrado.");
        return false;
    }

    public static boolean adicionarHistoricoCompra(UserCliente cliente, String nomeProduto, int quantidadeCompra, String nomeLoja, double valor) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement clienteElement : clientes) {
            JsonObject clienteJson = clienteElement.getAsJsonObject();

            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                JsonArray historicoCompras = clienteJson.has("historicoCompras")
                        ? clienteJson.getAsJsonArray("historicoCompras")
                        : new JsonArray();

                JsonObject historicoItem = new JsonObject();
                historicoItem.addProperty("produto", nomeProduto);
                historicoItem.addProperty("quantidade", quantidadeCompra);
                historicoItem.addProperty("loja", nomeLoja);
                historicoItem.addProperty("valor", valor);

                historicoCompras.add(historicoItem);

                clienteJson.add("historicoCompras", historicoCompras);
                DatabaseJSON.salvarClientes(clientes);
                return true;
            }
        }

        return false;
    }


    public static boolean efetuarCompra(UserCliente cliente, Scanner scanner) {
        exibirItensCarrinho(cliente);
        JsonArray itens = arrayItens(cliente);

        if (itens == null || itens.size() <= 0) {
            System.out.println("Carrinho vazio. Não é possível realizar a compra.");
            return false;
        }

        double valorCompra = valorCarrinho(itens);

        System.out.println("\nO valor total do carrinho é: " + valorCompra);
        System.out.print("Deseja confirmar a compra? (s/n): ");
        String confirmacao = scanner.nextLine().trim().toLowerCase();
        if (!confirmacao.equals("s")) {
            System.out.println("Compra cancelada.");
            return false;
        }

        for (JsonElement itemElement : itens) {
            JsonObject itemJson = itemElement.getAsJsonObject();

            if (!itemJson.has("nome") || itemJson.get("nome").isJsonNull()) {
                System.out.println("Erro: Nome do produto não encontrado.");
                return false;
            }
            String nomeProduto = itemJson.get("nome").getAsString();

            if (!itemJson.has("quantidade") || itemJson.get("quantidade").isJsonNull()) {
                System.out.println("Erro: Quantidade do produto não encontrada.");
                return false;
            }
            int quantidadeCompra = itemJson.get("quantidade").getAsInt();

            if (!itemJson.has("loja") || itemJson.get("loja").isJsonNull()) {
                System.out.println("Erro: Nome da loja não encontrado.");
                return false;
            }
            String lojaNome = itemJson.get("loja").getAsString();

            if (!itemJson.has("valor") || itemJson.get("valor").isJsonNull()) {
                System.out.println("Erro: Valor do produto não encontrado.");
                return false;
            }
            double valorProduto = itemJson.get("valor").getAsDouble();

            // Atualiza o estoque da loja
            if (!ProdutoDAO.atualizarEstoqueLojaCompra(lojaNome, nomeProduto, quantidadeCompra)) {
                System.out.println("Erro ao atualizar o estoque da loja.");
                return false;
            }

            // Adiciona o item ao histórico de compras do cliente
            if (!adicionarHistoricoCompra(cliente, nomeProduto, quantidadeCompra, lojaNome, valorProduto)) {
                System.out.println("Erro ao adicionar item ao histórico de compras.");
                return false;
            }
        }

        limparCarrinho(cliente);

        return true;
    }

    public static JsonArray arrayItens(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                if (clienteJson.has("carrinho")) {
                    JsonObject carrinhoJson = clienteJson.getAsJsonObject("carrinho");

                    if (carrinhoJson.has("itens")) {
                        JsonArray itens = carrinhoJson.getAsJsonArray("itens");

                        return itens;
                    }
                }
            }
        }

        return null;
    }

    public static boolean limparCarrinho(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();

            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                // Verifica se o carrinho existe
                if (clienteJson.has("carrinho")) {
                    JsonObject carrinhoJson = clienteJson.getAsJsonObject("carrinho");

                    // Remove os itens
                    carrinhoJson.add("itens", new JsonArray());

                    // Atualiza o carrinho no cliente
                    clienteJson.add("carrinho", carrinhoJson);

                    // Salva de volta no arquivo JSON
                    DatabaseJSON.salvarClientes(clientes);

                    System.out.println("Carrinho limpo com sucesso.");
                    return true;
                } else {
                    System.out.println("O cliente não possui carrinho.");
                    return false;
                }
            }
        }

        System.out.println("Cliente não encontrado.");
        return false;
    }

    public static boolean limparHistoricoCompras(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement clienteElement : clientes) {
            JsonObject clienteJson = clienteElement.getAsJsonObject();

            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                clienteJson.add("historicoCompras", new JsonArray()); // Limpa o histórico
                DatabaseJSON.salvarClientes(clientes);
                System.out.println("Histórico de compras limpo com sucesso.");
                return true;
            }
        }

        System.out.println("Cliente não encontrado.");
        return false;
    }

    /**
     * Busca por informação da compra anterior
     * @param detalhe O tipo de informação
     * @param indice O índice da compra
     * @param cliente O cliente que efetuou a compra
     * @return A informação requerida ou null se não foi encontrada
     */
    public static String buscarDetalheCompra(String detalhe, int indice, UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for(JsonElement element: clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if(clienteJson.get("id").getAsInt() == cliente.getId()) {
                JsonArray compras = clienteJson.getAsJsonArray("historicoCompras");
                JsonObject item = compras.get(indice).getAsJsonObject();
                return item.get(detalhe).getAsString();
            }
        }
        return null;
    }
}

