public class UserCliente extends User {
    private String cpf;

    public UserCliente(int id, String nome, String email, String senha, String cpf) {
        super(nome, email, senha);
        this.cpf = cpf;
    }

    public String getCpf(){return cpf;}
}

