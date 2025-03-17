import java.util.List;
import java.util.Scanner;

public class AdminSystem {
    private static List<UserAdmin> admins;
    private static List<UserCliente> clientes;

    public static void criarAdmin(String nome, String email, String senha) {
        AdminDAO.cadastrarAdmin(nome, email, senha);
    }

    public static void listarAdmins() {
        List<UserAdmin> admins = AdminDAO.listarTodos();
        System.out.println("\n==== Admins Registrados ====");
        for (UserAdmin admin : admins) {
            System.out.println("ID: " + admin.getId() + " | Nome: " + admin.getNome() + " | Email: " + admin.getEmail());
        }
    }

    public static void listarClientes() {
        List<UserCliente> cleintes = ClienteDAO.listarTodos();
        System.out.println("\n==== Clientes Registrados ====");
        for (UserCliente cliente : cleintes) {
            System.out.println("ID: " + cliente.getId() + " | Nome: " + cliente.getNome() + " | Email: " + cliente.getEmail()+ " | cpf: " + cliente.getCpf());
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

    public UserAdmin autenticarAdmin(int id, String senha) {
        if (AdminDAO.validarLogin(id, senha)) {
            return AdminDAO.buscarPorId(id);
        }
        return null;
    }


}
