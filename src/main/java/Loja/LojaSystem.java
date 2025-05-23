package Loja;

import User.UserCliente;
import User.UserLoja;

import java.util.Scanner;
import Produto.ProdutoSystem;

public class LojaSystem {

    public static void criarLoja(String nome, String email, String senha, String CNPJ){
        LojaDAO.cadastrarLoja(nome, email, senha, CNPJ);
    }

    public UserLoja autenticarLoja(String email, String senha){
        if (LojaDAO.validarLogin(email, senha) != null) {
            return LojaDAO.buscarPorEmail(email);
        }
        return null;
    }

    public static void atualizarLoja(Scanner scanner, UserLoja loja) {
        System.out.println("\n===== Atualização de Dados =====");
        System.out.println("Deixe em branco para manter os dados atuais.");

        System.out.print("Novo nome (" + loja.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = loja.getNome();

        System.out.print("Novo e-mail (" + loja.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = loja.getEmail();

        System.out.print("Nova senha: ");
        String senha = scanner.nextLine().trim();
        if (senha.isEmpty()) senha = loja.getSenha();

        System.out.print("Novo CNPJ (" + loja.getCnpj() + "): ");
        String cnpj = scanner.nextLine().trim();
        if (cnpj.isEmpty()) cnpj = loja.getCnpj();

        // Atualiza os atributos antes de salvar
        loja.setNome(nome);
        loja.setEmail(email);
        loja.setSenha(senha);
        loja.setCnpj(cnpj);

        // Tenta atualizar no banco de dados
        boolean sucesso = LojaDAO.atualizar(loja);
        if (sucesso) {
            System.out.println("Dados da loja atualizados com sucesso!");
        } else {
            System.out.println("Erro ao atualizar dados");
        }
    }

    public static void menuProdutos(UserLoja loja){
        ProdutoSystem.iniciar(loja);
    }

}
