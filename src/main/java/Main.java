import Admin.AdminInterface;
import Cliente.ClienteInterface;
import Console.Widgets.Info;
import Console.Widgets.Menu;
import Loja.LojaInterface;
import Database.DatabaseJSON;


public class Main {

    public static void main(String[] args) {
        // Inicialização das interfaces
        AdminInterface InterfaceAdm = new AdminInterface();
        ClienteInterface InterfaceCliente = new ClienteInterface();
        LojaInterface InterfaceLoja = new LojaInterface();

        // configuração das bases de dados
        DatabaseJSON.inicializarJSON();
        InterfaceAdm.criarAdmTeste();

        // Menu principal
        Menu menu = new Menu()
                .adicionarCabecalho("==== Fazer Cadastro/Login como ====")
                .adicionarOpcao("Admin", () -> InterfaceAdm.loginAdmin())
                .adicionarOpcao("Loja", () -> InterfaceLoja.loginCadastroLoja())
                .adicionarOpcao("Cliente", () -> InterfaceCliente.loginCadastroCliente())
                .setPromptSaida("Sair do Sistema")
                .setPromptEntrada("Escolha uma opção (0-3): ");

        menu.mostrar(System.in, System.out);

        Info.mostrar(System.out, "Encerrando o sistema...");
    }

}