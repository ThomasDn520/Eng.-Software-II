public class Cliente {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String cpf;

    public Cliente(int id, String nome, String email, String senha, String cpf) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getCpf() { return cpf; }

    public void setNome(String nome) { this.nome = nome; }
    public void setSenha(String senha) { this.senha = senha; }
}
