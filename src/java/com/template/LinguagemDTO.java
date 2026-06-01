package com.template;

// DTO (Data Transfer Object) - É a nossa "caixa de transporte".
// Serve apenas para guardar os dados e levá-los da tela para o banco
public class LinguagemDTO {

    // Atributos (características da linguagem)
    private int id;
    private String nome;
    private String criador;
    private String tipo;
    private int anoCriacao;

    // Construtor vazio: Cria uma "caixa" em branco.
    public LinguagemDTO() {
    }

    // Construtor preenchido: Cria a "caixa" já com os dados.
    public LinguagemDTO(String nome, String criador, String tipo, int anoCriacao) {
        this.nome = nome;
        this.criador = criador;
        this.tipo = tipo;
        this.anoCriacao = anoCriacao;
    }

    // === GETTERS e SETTERS ===

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