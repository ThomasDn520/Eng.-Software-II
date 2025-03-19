package User;

import User.User;

public class UserLoja extends User {

    private String cnpj;
    public UserLoja(int id, String nome, String email, String senha, String cnpj) {

        super(id, nome, email, senha);
        this.cnpj = cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getCnpj(){
        return cnpj;
    };

    }



