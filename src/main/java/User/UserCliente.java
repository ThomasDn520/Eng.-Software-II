package User;

public class UserCliente extends User {
    private String cpf;
    private int pontos;

    public UserCliente(int id, String nome, String email, String senha, String cpf) {
        super(id, nome, email, senha);
        this.cpf = cpf;
        this.pontos = 0;
    }

    // Construtor adicional para carregar pontos existentes
    public UserCliente(int id, String nome, String email, String senha, String cpf, int pontos) {
        super(id, nome, email, senha);
        this.cpf = cpf;
        this.pontos = pontos;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        if (pontos >= 0) { // Validação para não permitir pontos negativos
            this.pontos = pontos;
        }
    }

    @Override
    public String toString() {
        return "UserCliente{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", cpf='" + cpf + '\'' +
                ", pontos=" + pontos +
                '}';
    }
}