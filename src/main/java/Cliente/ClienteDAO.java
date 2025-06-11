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
        novoCliente.addProperty("pontos", 0);

        clientes.add(novoCliente);
        DatabaseJSON.salvarClientes(clientes);

        return novoId;
    }

    private static int calcularPontos(double valorCompra) {
        return (int) (valorCompra / 50);
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

                // Verificação do carrinho
                if (!clienteJson.has("carrinho")) {
                    System.out.println("\n⚠️ Carrinho não encontrado");
                    return false;
                }

                JsonObject carrinhoJson = clienteJson.getAsJsonObject("carrinho");

                // Verificação dos itens
                if (!carrinhoJson.has("itens") || carrinhoJson.getAsJsonArray("itens").size() == 0) {
                    System.out.println("\n🛒 Seu carrinho está vazio");
                    return false;
                }

                JsonArray itens = carrinhoJson.getAsJsonArray("itens");

                // Cabeçalho
                System.out.println("\n════════ SEU CARRINHO ════════");
                System.out.println("ITEM\t\tQTD\tVALOR UNIT.\tSUBTOTAL");
                System.out.println("════════════════════════════════════");

                // Listagem de itens
                double valorTotal = 0;
                for (JsonElement itemElement : itens) {
                    JsonObject itemJson = itemElement.getAsJsonObject();

                    String nome = itemJson.get("nome").getAsString();
                    double valor = itemJson.get("valor").getAsDouble();
                    int quantidade = itemJson.get("quantidade").getAsInt();
                    double subtotal = valor * quantidade;
                    valorTotal += subtotal;

                    // Formatação para exibição alinhada
                    String nomeFormatado = nome.length() > 15 ? nome.substring(0, 12) + "..." : nome;
                    System.out.printf("%-15s\t%d\tR$%-9.2f\tR$%.2f%n",
                            nomeFormatado, quantidade, valor, subtotal);
                }

                // Resumo
                System.out.println("════════════════════════════════════");
                System.out.printf("VALOR TOTAL: R$%.2f%n", valorTotal);

                // Informações sobre pontos
                int pontos = consultarPontos(cliente);
                System.out.println("\n════════ SEUS PONTOS ════════");
                System.out.println("PONTOS ATUAIS: " + pontos);

                if (pontos >= 10) {
                    double desconto = valorTotal * 0.10;
                    System.out.printf("DESCONTO DISPONÍVEL: R$%.2f (10%%)%n", desconto);
                    System.out.printf("VALOR COM DESCONTO: R$%.2f%n", valorTotal - desconto);
                    System.out.println("(Serão debitados 10 pontos ao confirmar a compra)");
                } else {
                    int pontosFaltantes = 10 - pontos;
                    System.out.println("Você precisa de mais " + pontosFaltantes +
                            " pontos para obter 10% de desconto");
                }

                return true;
            }
        }

        System.out.println("\n⛔ Cliente não encontrado");
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
        try {
            JsonArray clientes = DatabaseJSON.carregarClientes();

            for (JsonElement element : clientes) {
                JsonObject clienteJson = element.getAsJsonObject();
                if (clienteJson.get("id").getAsInt() == cliente.getId()) {

                    // Carrega histórico de compras ou cria vazio se não existir
                    JsonArray historicoCompras = clienteJson.has("historicoCompras")
                            ? clienteJson.getAsJsonArray("historicoCompras")
                            : new JsonArray();

                    // Carrega histórico de pontos ou cria vazio se não existir
                    JsonArray historicoPontos = clienteJson.has("historicoPontos")
                            ? clienteJson.getAsJsonArray("historicoPontos")
                            : new JsonArray();

                    if (historicoCompras.size() == 0) {
                        System.out.println("\n📭 Nenhuma compra realizada");
                        return true;
                    }

                    // Calcula pontos totais de cada categoria
                    int pontosTotais = consultarPontos(cliente);
                    int pontosCompras = calcularPontosTotaisCompras(historicoCompras);
                    int pontosAvaliacoes = calcularPontosTotaisAvaliacoes(historicoPontos);

                    // Verificação de consistência (para debug)
                    if (pontosTotais != (pontosCompras + pontosAvaliacoes)) {
                        System.err.println("⚠️ Aviso: Inconsistência na contagem de pontos!");
                        System.err.println("Total: " + pontosTotais + " | Compras: " + pontosCompras
                                + " | Avaliações: " + pontosAvaliacoes);
                    }

                    // Exibe cabeçalho com informações de pontos
                    System.out.println("\n════════ HISTÓRICO DE COMPRAS ════════");
                    System.out.println("PONTOS TOTAIS: " + pontosTotais);
                    System.out.println("  - Pontos por compras: " + pontosCompras);
                    System.out.println("  - Pontos por avaliações: " + pontosAvaliacoes + "\n");

                    // Cabeçalho da tabela de compras
                    System.out.println("COMPRA\tPRODUTO\t\tLOJA\tVALOR\tQTD\tPONTOS");
                    System.out.println("══════════════════════════════════════════");

                    // Lista todas as compras
                    for (int i = 0; i < historicoCompras.size(); i++) {
                        JsonObject compra = historicoCompras.get(i).getAsJsonObject();
                        double valorCompra = compra.get("valor").getAsDouble() * compra.get("quantidade").getAsInt();
                        int pontosCompra = calcularPontos(valorCompra);

                        System.out.printf(
                                "#%-6d%-12s\t%-8s\tR$%-7.2f%d\t+%d pts%n",
                                i+1,
                                compra.get("produto").getAsString(),
                                compra.get("loja").getAsString(),
                                compra.get("valor").getAsDouble(),
                                compra.get("quantidade").getAsInt(),
                                pontosCompra
                        );
                    }

                    // Adiciona legenda se houver inconsistência
                    if (pontosTotais != (pontosCompras + pontosAvaliacoes)) {
                        System.out.println("\n⚠️ Nota: A diferença nos pontos pode ser devido a");
                        System.out.println("promoções ou outras atividades não listadas");
                    }

                    return true;
                }
            }
            System.out.println("\n⛔ Cliente não encontrado");
            return false;
        } catch (Exception e) {
            System.err.println("Erro ao exibir histórico: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static int calcularPontosTotaisAvaliacoes(JsonArray historicoPontos) {
        int total = 0;
        if (historicoPontos != null) {
            for (JsonElement element : historicoPontos) {
                JsonObject ponto = element.getAsJsonObject();
                if (ponto.has("tipo") && ponto.get("tipo").getAsString().equals("avaliacao")) {
                    total += ponto.get("pontos").getAsInt();
                }
            }
        }
        return total;
    }

    private static int calcularPontosTotaisCompras(JsonArray historicoCompras) {
        int pontosTotais = 0;

        for (JsonElement element : historicoCompras) {
            JsonObject compra = element.getAsJsonObject();
            double valor = compra.get("valor").getAsDouble() * compra.get("quantidade").getAsInt();
            pontosTotais += calcularPontos(valor);
        }

        return pontosTotais;
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

                // Calcula e armazena os pontos ganhos nesta compra
                int pontosGanhos = calcularPontos(valor * quantidadeCompra);
                historicoItem.addProperty("pontosGanhos", pontosGanhos);

                historicoCompras.add(historicoItem);

                clienteJson.add("historicoCompras", historicoCompras);
                DatabaseJSON.salvarClientes(clientes);
                return true;
            }
        }

        return false;
    }

    public static boolean adicionarPontos(UserCliente cliente, int pontos) {
        if (pontos <= 0) {
            System.out.println("Nenhum ponto a ser adicionado.");
            return false;
        }

        JsonArray clientes = DatabaseJSON.carregarClientes();
        boolean clienteEncontrado = false;

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                // Verifica se já existe campo de pontos
                int pontosAtuais = 0;
                if (clienteJson.has("pontos")) {
                    try {
                        pontosAtuais = clienteJson.get("pontos").getAsInt();
                    } catch (Exception e) {
                        System.err.println("Erro ao ler pontos existentes, iniciando de 0");
                        pontosAtuais = 0;
                    }
                }

                // Atualiza os pontos
                int novoTotal = pontosAtuais + pontos;
                clienteJson.addProperty("pontos", novoTotal);
                clienteEncontrado = true;
                break;
            }
        }

        if (clienteEncontrado) {
            // Atualiza tanto no JSON quanto no objeto cliente
            DatabaseJSON.salvarClientes(clientes);
            cliente.setPontos(cliente.getPontos() + pontos); // Atualiza o objeto em memória
            System.out.println(pontos + " pontos adicionados com sucesso!");
            return true;
        } else {
            System.out.println("Cliente não encontrado para adicionar pontos.");
            return false;
        }
    }

    public static void registrarPontosAvaliacao(UserCliente cliente, String descricao) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                JsonArray historicoPontos = clienteJson.has("historicoPontos")
                        ? clienteJson.getAsJsonArray("historicoPontos")
                        : new JsonArray();

                JsonObject pontoItem = new JsonObject();
                pontoItem.addProperty("tipo", "avaliacao");
                pontoItem.addProperty("descricao", descricao);
                pontoItem.addProperty("pontos", 1);
                pontoItem.addProperty("data", new java.util.Date().toString());

                historicoPontos.add(pontoItem);
                clienteJson.add("historicoPontos", historicoPontos);
                DatabaseJSON.salvarClientes(clientes);
                break;
            }
        }
    }

    public static void exibirDetalhesPontos(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                int pontosTotais = consultarPontos(cliente);
                int pontosCompras = 0;
                int pontosAvaliacoes = 0;

                // Calcula pontos de compras
                if (clienteJson.has("historicoCompras")) {
                    JsonArray historicoCompras = clienteJson.getAsJsonArray("historicoCompras");
                    pontosCompras = calcularPontosTotaisCompras(historicoCompras);
                }

                // Pontos de avaliações são a diferença
                pontosAvaliacoes = pontosTotais - pontosCompras;

                System.out.println("\n════════ DETALHES DE PONTOS ════════");
                System.out.println("PONTOS TOTAIS: " + pontosTotais);
                System.out.println("  - Pontos por compras: " + pontosCompras);
                System.out.println("  - Pontos por avaliações: " + pontosAvaliacoes);

                // Exibir histórico recente de pontos
                if (clienteJson.has("historicoPontos")) {
                    System.out.println("\nÚLTIMAS ATIVIDADES:");
                    JsonArray historicoPontos = clienteJson.getAsJsonArray("historicoPontos");
                    int limite = Math.min(5, historicoPontos.size()); // Mostra até 5 itens

                    for (int i = historicoPontos.size() - 1; i >= Math.max(0, historicoPontos.size() - 5); i--) {
                        JsonObject ponto = historicoPontos.get(i).getAsJsonObject();
                        System.out.printf("  %s: +%d pts (%s)%n",
                                ponto.get("descricao").getAsString(),
                                ponto.get("pontos").getAsInt(),
                                ponto.get("data").getAsString());
                    }
                }

                return;
            }
        }
        System.out.println("Cliente não encontrado.");
    }

    public static void exibirPontos(UserCliente cliente) {
        int pontos = consultarPontos(cliente);
        System.out.println("\n" + cliente.getNome() + ", você possui " + pontos + " pontos acumulados.");
    }

    public static int consultarPontos(UserCliente cliente) {
        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                return clienteJson.has("pontos") ? clienteJson.get("pontos").getAsInt() : 0;
            }
        }
        return 0;
    }

    public static boolean efetuarCompra(UserCliente cliente, Scanner scanner) {
        // Exibe itens do carrinho
        exibirItensCarrinho(cliente);
        JsonArray itens = arrayItens(cliente);

        // Validação de carrinho vazio
        if (itens == null || itens.size() <= 0) {
            System.out.println("Carrinho vazio. Não é possível realizar a compra.");
            return false;
        }

        // Cálculos iniciais
        double valorCompra = valorCarrinho(itens);
        double desconto = 0;
        boolean usarPontos = false;
        int pontosDisponiveis = consultarPontos(cliente);

        // Verificação e oferta de desconto por pontos
        if (pontosDisponiveis >= 10) {
            System.out.print("\nVocê tem " + pontosDisponiveis + " pontos. Deseja usar 10 pontos para 10% de desconto? (s/n): ");
            String resposta = scanner.nextLine().trim().toLowerCase();

            if (resposta.equals("s")) {
                desconto = Math.min(valorCompra * 0.10, valorCompra); // Garante que o desconto não exceda o valor total
                usarPontos = true;
                System.out.println("Desconto de R$" + desconto + " aplicado!");
            }
        }

        // Cálculo de pontos ganhos e valor final
        double valorFinal = valorCompra - desconto;
        int pontosGanhos = calcularPontos(valorFinal);

        // Resumo detalhado da compra
        System.out.println("\n═ RESUMO FINAL DA COMPRA ═");
        System.out.println("Valor total dos produtos: R$" + String.format("%.2f", valorCompra));
        if (desconto > 0) {
            System.out.println("Desconto aplicado: -R$" + String.format("%.2f", desconto));
        }
        System.out.println("Valor final a pagar: R$" + String.format("%.2f", valorFinal));
        System.out.println("Pontos a serem ganhos: " + pontosGanhos);
        System.out.println("Saldo atual de pontos: " + pontosDisponiveis);
        if (usarPontos) {
            System.out.println("Saldo após esta compra: " + (pontosDisponiveis - 10 + pontosGanhos));
        }

        // Confirmação final
        System.out.print("\n╔ Deseja confirmar a compra? (s/n): ");
        String confirmacao = scanner.nextLine().trim().toLowerCase();
        if (!confirmacao.equals("s")) {
            System.out.println("╚ Compra cancelada.");
            return false;
        }

        // Processamento dos itens
        for (JsonElement itemElement : itens) {
            JsonObject itemJson = itemElement.getAsJsonObject();
            String nomeProduto = itemJson.get("nome").getAsString();
            int quantidadeCompra = itemJson.get("quantidade").getAsInt();
            String lojaNome = itemJson.get("loja").getAsString();
            double valorProduto = itemJson.get("valor").getAsDouble();

            if (!ProdutoDAO.atualizarEstoqueLojaCompra(lojaNome, nomeProduto, quantidadeCompra)) {
                System.out.println("Erro ao atualizar o estoque da loja.");
                return false;
            }

            if (!adicionarHistoricoCompra(cliente, nomeProduto, quantidadeCompra, lojaNome, valorProduto)) {
                System.out.println("Erro ao adicionar item ao histórico de compras.");
                return false;
            }
        }

        // Atualização de pontos
        if (usarPontos && !removerPontos(cliente, 10)) {
            System.out.println("Erro ao subtrair pontos utilizados!");
            return false;
        }

        if (pontosGanhos > 0) {
            adicionarPontos(cliente, pontosGanhos);
        }

        // Finalização
        limparCarrinho(cliente);
        System.out.println("╚ Compra finalizada com sucesso!");
        if (usarPontos) {
            System.out.println("  10 pontos foram debitados do seu saldo.");
        }
        System.out.println("  Pontos ganhos nesta compra: " + pontosGanhos);
        System.out.println("  Novo saldo de pontos: " + consultarPontos(cliente));

        return true;
    }

    public static boolean removerPontos(UserCliente cliente, int pontos) {
        if (pontos <= 0) return false;

        JsonArray clientes = DatabaseJSON.carregarClientes();

        for (JsonElement element : clientes) {
            JsonObject clienteJson = element.getAsJsonObject();
            if (clienteJson.get("id").getAsInt() == cliente.getId()) {
                int pontosAtuais = clienteJson.has("pontos") ? clienteJson.get("pontos").getAsInt() : 0;
                int novosPontos = Math.max(0, pontosAtuais - pontos); // Não permite negativos

                clienteJson.addProperty("pontos", novosPontos);
                DatabaseJSON.salvarClientes(clientes);
                cliente.setPontos(novosPontos);
                return true;
            }
        }
        return false;
    }

    public static boolean verificarConsistenciaPontos(UserCliente cliente) {
        int pontosMemoria = cliente.getPontos();
        int pontosArmazenados = consultarPontos(cliente);

        if (pontosMemoria != pontosArmazenados) {
            System.err.println("Inconsistência encontrada! Memória: " + pontosMemoria
                    + " | Armazenado: " + pontosArmazenados);
            // Corrige automaticamente
            cliente.setPontos(pontosArmazenados);
            return false;
        }
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

