package Admin;

import Cliente.ClienteDAO;
import Loja.LojaDAO;
import User.UserAdmin;
import User.UserCliente;
import User.UserLoja;

import java.util.List;
import java.util.Scanner;

public class AdminSystem {
    private static List<UserAdmin> admins;
    private static List<UserCliente> clientes;
    private static List<UserLoja> lojas;


    public static void criarAdmin(Scanner scanner) {
        System.out.println("\n--- Cadastro de Administrador ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        // Aqui você pode escolher qual método usar (JSON ou SQL)
        int id = AdminDAO.cadastrarAdmin(nome, email, senha); // Versão unificada

        if (id > 0) {
            System.out.println("Admin cadastrado com sucesso! ID: " + id);
        } else {
            System.out.println("Falha ao cadastrar admin.");
        }
    }

    public static void criarAdminDiretamente(String nome, String email, String senha){
        AdminDAO.cadastrarAdmin(nome, email, senha);
    }

    // Lista todos os admins
    public static void listarAdmins() {
        List<UserAdmin> admins = AdminDAO.listarTodos();
        System.out.println("\n==== Admins Registrados ====");

        if (admins.isEmpty()) {
            System.out.println("Nenhum admin cadastrado.");
        } else {
            for (UserAdmin admin : admins) {
                System.out.println("ID: " + admin.getId() +
                        " | Nome: " + admin.getNome() +
                        " | Email: " + admin.getEmail());
            }
        }
    }

    // Autentica um admin
    public static UserAdmin autenticarAdmin(Scanner scanner) {
        System.out.println("\n--- Login Administrador ---");
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpa o buffer
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        UserAdmin admin = AdminDAO.autenticar(id, senha);

        if (admin != null) {
            System.out.println("Login bem-sucedido! Bem-vindo, " + admin.getNome());
        } else {
            System.out.println("ID ou senha incorretos.");
        }

        return admin;
    }

    public static void listarClientes() {
        List<UserCliente> cleintes = ClienteDAO.listarTodos();
        System.out.println("\n==== Clientes Registrados ====");
        for (UserCliente cliente : cleintes) {
            System.out.println("ID: " + cliente.getId() + " | Nome: " + cliente.getNome() + " | Email: " + cliente.getEmail()+ " | cpf: " + cliente.getCpf());
        }
    }

    public static void listarLojas() {
        List<UserLoja> lojas = LojaDAO.litarTodas();
        System.out.println("\n==== Lojas Registrados ====");
        for (UserLoja loja : lojas) {
            System.out.println("ID: " + loja.getId() + " | Nome: " + loja.getNome() + " | Email: " + loja.getEmail()+ " | cnpj: " + loja.getCnpj());
        }

    }

    public static void removerCliente(Scanner scanner) {
        System.out.print("Digite o e-mail do cliente que deseja remover: ");
        String email = scanner.nextLine();

        if (ClienteDAO.remover(email)) {
            System.out.println("Cliente removido com sucesso!");
        } else {
            System.out.println("Falha ao remover o cliente. Verifique se o e-mail está correto.");
        }
    }

    public static void removerLoja(Scanner scanner){
        System.out.print("Digite o CNPJ da loja a ser removida: ");
        String cnpj = scanner.nextLine().trim();
        if (LojaDAO.removerLoja(cnpj)){
            System.out.println("Loja removida com sucesso!");
        } else {
            System.out.println("Falha ao remover o cliente. Verifique se o CNPJ está correto.");
        }


    }


}
