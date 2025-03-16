import java.util.List;

public class AdminSystem {
    private static List<UserAdmin> admins;

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

    public UserAdmin autenticarAdmin(int id, String senha) {
        if (AdminDAO.validarLogin(id, senha)) {
            return AdminDAO.buscarPorId(id);
        }
        return null;
    }


}
