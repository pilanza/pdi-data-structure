package com.pdi.image_processing.filter.TreeAvlFilter;

public class NoAVL {
    public int valor;
    public int altura;
    public int size;
    public int quantidade_extra;
    public NoAVL esquerda;
    public NoAVL direita;

    public NoAVL(int valor) {
        this.valor = valor;
        this.altura = 1;
        this.size = 1;
        this.quantidade_extra = 1;
        this.esquerda = null;
        this.direita = null;
    }
}