package Admin;

import Cliente.ClienteDAO;
import Loja.LojaDAO;
import User.UserAdmin;
import User.UserCliente;
import User.UserLoja;

import java.util.List;

public class AdminSystem {
    private static List<UserAdmin> admins;
    private static List<UserCliente> clientes;
    private static List<UserLoja> lojas;

    /**
     * Cria um administrador no sistema
     * @param nome Nome do administrador
     * @param email Email do administrador
     * @param senha Senha do administrador
     * @return O id do administrador cadastrado (maior que zero), ou 0 (zero) caso não tiver sido possível cadastrar
     */
    public static int criarAdminDiretamente(String nome, String email, String senha){
        return AdminDAO.cadastrarAdmin(nome, email, senha);
    }

    /**
     * Lista todos os admins
     * @return Uma lista com os admins cadastrados
     */
    public static List<UserAdmin> listarAdmins() {
        return AdminDAO.listarTodos();
    }

    /**
     * Autentica um administrador
     * @param id O id numérico do administrador
     * @param senha Senha do administrador
     * @return O admin autenticado, ou null se a autenticação falhou
     */
    public static UserAdmin autenticarAdmin(int id, String senha) {
        return AdminDAO.autenticar(id, senha);
    }

    /**
     * Lista todos os clientes
     * @return Uma lista com os clientes cadastrados
     */
    public static List<UserCliente> listarClientes() {
        return ClienteDAO.listarTodos();
    }

    /**
     * Lista todos as lojas
     * @return Uma lista com as lojas cadastrados
     */
    public static List<UserLoja> listarLojas() {
        return LojaDAO.listarTodas();
    }

    /**
     * Remove um cliente
     * @param email O e-mail do cliente a ser removido
     * @return true se o cliente foi removido com sucesso, false se ocorreu algum erro
     */
    public static boolean removerCliente(String email) {
        return ClienteDAO.remover(email);
    }

    /**
     * Remove uma loja
     * @param cnpj O CNPJ da loja a ser removida
     * @return true se a loja foi removida com sucesso, false se ocorreu algum erro
     */
    public static boolean removerLoja(String cnpj){
        return LojaDAO.removerLoja(cnpj);
    }
}
