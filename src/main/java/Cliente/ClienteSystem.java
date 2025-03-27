package Cliente;

import User.UserCliente;

import java.util.Scanner;

public class ClienteSystem {

    public static void criarCliente(String nome, String email, String senha, String cpf) {
        ClienteDAO.cadastrarCliente(nome, email, senha, cpf);
    }

    public UserCliente autenticarCliente(String email, String senha){
        if (ClienteDAO.validarLogin(email, senha)) {
            return ClienteDAO.buscarPorEmail(email);
        }
        return null;
    }
// função de atualizar clientes ainda não esta funcionando corretamente
    public static void atualizarCliente(Scanner scanner, UserCliente cliente) {
        System.out.println("\n===== Atualização de Dados =====");
        System.out.println("Deixe em branco para manter os dados atuais.");

        System.out.print("Novo nome (" + cliente.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        if (nome.isEmpty()) nome = cliente.getNome();

        System.out.print("Novo e-mail (" + cliente.getEmail() + "): ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) email = cliente.getEmail();

        System.out.print("Nova senha: ");
        String senha = scanner.nextLine().trim();
        if (senha.isEmpty()) senha = cliente.getSenha();

        System.out.print("Novo CPF (" + cliente.getCpf() + "): ");
        String cpf = scanner.nextLine().trim();
        if (cpf.isEmpty()) cpf = cliente.getCpf();

        if(ClienteDAO.atualizar(cliente)){
            System.out.println("Dados do cliente atualizados com sucesso!");
            cliente.setNome(nome);
            cliente.setEmail(email);
            cliente.setSenha(senha);
            cliente.setCpf(cpf);
        } else{
            System.out.println("Erro ao atualizar dados.");
        }
    }
}
