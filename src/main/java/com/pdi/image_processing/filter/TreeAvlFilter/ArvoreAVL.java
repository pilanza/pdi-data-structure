package com.pdi.image_processing.filter.TreeAvlFilter;

public class ArvoreAVL {

    private NoAVL raiz;

    public ArvoreAVL() {
        this.raiz = null;
    }

    // ── Auxiliares ───────────────────────────────────────────────────────────

    private int altura(NoAVL no) {
        return (no == null) ? 0 : no.altura;
    }

    private int tamanho(NoAVL no) {
        return (no == null) ? 0 : no.size;
    }

    private int max(int a, int b) {
        return (a > b) ? a : b;
    }

    private void atualizar(NoAVL no) {
        if (no != null) {
            no.altura = 1 + max(altura(no.esquerda), altura(no.direita));
            no.size = no.quantidade_extra + tamanho(no.esquerda) + tamanho(no.direita);
        }
    }

    private int fatorBalanceamento(NoAVL no) {
        return (no == null) ? 0 : altura(no.esquerda) - altura(no.direita);
    }

    // ── Rotações ─────────────────────────────────────────────────────────────

    private NoAVL rotacaoDireita(NoAVL y) {
        NoAVL x  = y.esquerda;
        NoAVL T2 = x.direita;
        x.direita  = y;
        y.esquerda = T2;
        atualizar(y);
        atualizar(x);
        return x;
    }

    private NoAVL rotacaoEsquerda(NoAVL x) {
        NoAVL y  = x.direita;
        NoAVL T2 = y.esquerda;
        y.esquerda = x;
        x.direita  = T2;
        atualizar(x);
        atualizar(y);
        return y;
    }

    // ── Inserção ─────────────────────────────────────────────────────────────

    private NoAVL adicionar(NoAVL no, int valor) {
        if (no == null) return new NoAVL(valor);

        if (valor < no.valor)
            no.esquerda = adicionar(no.esquerda, valor);
        else if (valor > no.valor)
            no.direita = adicionar(no.direita, valor);
        else {
            no.quantidade_extra++;
            atualizar(no);
            return no;
        }

        atualizar(no);
        int fb = fatorBalanceamento(no);

        // LL
        if (fb > 1 && valor < no.esquerda.valor)
            return rotacaoDireita(no);
        // RR
        if (fb < -1 && valor > no.direita.valor)
            return rotacaoEsquerda(no);
        // LR
        if (fb > 1 && valor > no.esquerda.valor) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }
        // RL
        if (fb < -1 && valor < no.direita.valor) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    public void adicionar(int valor) {
        raiz = adicionar(raiz, valor);
    }

    // ── Remoção ──────────────────────────────────────────────────────────────

    private NoAVL menorValor(NoAVL no) {
        NoAVL atual = no;
        while (atual.esquerda != null) atual = atual.esquerda;
        return atual;
    }

    private NoAVL remover(NoAVL no, int valor) {
        if (no == null) return null;

        if (valor < no.valor)
            no.esquerda = remover(no.esquerda, valor);
        else if (valor > no.valor)
            no.direita = remover(no.direita, valor);
        else {
            if (no.quantidade_extra > 1) {
                no.quantidade_extra--;
            } else if (no.esquerda == null || no.direita == null) {
                NoAVL temp = (no.esquerda != null) ? no.esquerda : no.direita;
                if (temp == null) return null;
                no = temp;
            } else {
                NoAVL temp = menorValor(no.direita);
                no.valor = temp.valor;
                no.quantidade_extra = 1;
                no.direita = remover(no.direita, temp.valor);
            }
        }

        atualizar(no);
        int fb = fatorBalanceamento(no);

        if (fb > 1 && fatorBalanceamento(no.esquerda) >= 0)
            return rotacaoDireita(no);
        if (fb > 1 && fatorBalanceamento(no.esquerda) < 0) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }
        if (fb < -1 && fatorBalanceamento(no.direita) <= 0)
            return rotacaoEsquerda(no);
        if (fb < -1 && fatorBalanceamento(no.direita) > 0) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    public void remover(int valor) {
        raiz = remover(raiz, valor);
    }

    // ── K-ésimo elemento (mediana) ───────────────────────────────────────────

    private NoAVL kEsimo(NoAVL no, int k) {
        if (no == null) return null;
        int esquerda = tamanho(no.esquerda);
        if (k <= esquerda)
            return kEsimo(no.esquerda, k);
        if (k <= esquerda + no.quantidade_extra)
            return no;
        return kEsimo(no.direita, k - esquerda - no.quantidade_extra);
    }

    public int mediana() {
        int n = tamanho(raiz);
        return kEsimo(raiz, (n + 1) / 2).valor;
    }

    public int tamanho() {
        return tamanho(raiz);
    }
}