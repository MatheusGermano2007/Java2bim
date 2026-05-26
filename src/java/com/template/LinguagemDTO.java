package com.template;

public class LinguagemDTO {
    private int id;
    private String nome;
    private String criador;
    private String tipo;
    private int anoCriacao;

    public LinguagemDTO() {
    }

    public LinguagemDTO(String nome, String criador, String tipo, int anoCriacao) {
        this.nome = nome;
        this.criador = criador;
        this.tipo = tipo;
        this.anoCriacao = anoCriacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCriador() {
        return criador;
    }

    public void setCriador(String criador) {
        this.criador = criador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getAnoCriacao() {
        return anoCriacao;
    }

    public void setAnoCriacao(int anoCriacao) {
        this.anoCriacao = anoCriacao;
    }
}
