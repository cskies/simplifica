package com.simplifica.model;

public class Pessoa {
    private String nome;
    public int idade;

    public Pessoa(String nome, int idade) {
        this.nome = nome;
        this.idade = idade;
    }

    private String getNome() { return nome; }
    public void apresentar() {
        System.out.println("Olá, sou " + nome);
    }
}
