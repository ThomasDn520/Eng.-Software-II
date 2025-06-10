package Loja;

import User.UserLoja;

import java.util.List;
import java.util.Scanner;

public class LojaSystem {

    public static void criarLoja(String nome, String email, String senha, String CNPJ){
        LojaDAO.cadastrarLoja(nome, email, senha, CNPJ);
    }

    public static UserLoja autenticarLoja(String email, String senha){
        if (LojaDAO.validarLogin(email, senha) != null) {
            return LojaDAO.buscarPorEmail(email);
        }
        return null;
    }

    // FIXME: Produtos e avaliações não são passados para a loja atualizada
    public static boolean atualizarLoja(UserLoja loja) {
        return LojaDAO.atualizar(loja);
    }

    // TODO: remover, só está sendo usado em testes
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

    /**
     * Busca a média de notas dessa loja
     * @param nomeLoja O nome da loja
     * @return A média das notas dessa loja, -1 se a loja não tiver sido avaliada ou null se a loja não foi encontrada
     */
    public static Double buscarNotaMediaLoja(String nomeLoja) {
        for(UserLoja loja: LojaDAO.listarTodas()) {
            if(loja.getNome().equalsIgnoreCase(nomeLoja)) {
                List<Double> notas = LojaDAO.listarNotas(loja.getCnpj());
                if(notas.isEmpty())
                    return -1.0;
                double somaNotas = 0;
                for(double nota: notas)
                    somaNotas += nota;
                return somaNotas/notas.size();
            }
        }
        return null;
    }
}
