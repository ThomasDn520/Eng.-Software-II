package Produto;

import java.util.UUID;

public class Produto {
    private String id;
    private String nome;
    private double valor;
    private String tipo;
    private int quantidade;
    private String marca;
    private String descricao;

    public Produto(String nome, double valor, String tipo, int quantidade, String marca, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.valor = valor;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.marca = marca;
        this.descricao = descricao;
    }

    // Getters e Setters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}