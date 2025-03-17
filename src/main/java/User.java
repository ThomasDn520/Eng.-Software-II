
public abstract class User {
    protected int Id;
    protected String nome;
    protected String email;
    protected String senha;

    private static int contadorId = 0;

    public User(String nome, String email, String senha) {
        this.Id = gerarId();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    private int gerarId() {
        return contadorId++;
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

