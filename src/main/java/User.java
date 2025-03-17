
public abstract class User {
    protected int Id;
    protected String nome;
    protected String email;
    protected String senha;

    public User(int id, String nome, String email, String senha) {
        this.Id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getId(){
        return Id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha(){return senha;}

    public boolean validarSenha(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    @Override
    public String toString() {
        return "Nome: " + nome + " | Email: " + email;
    }
}

