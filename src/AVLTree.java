public class AVLTree extends Tree {

    @Override
    public void inserir(long v) {
        setRoot(inserirRecursivo(getRoot(), v));
    }

    private No inserirRecursivo(No no, long v) {
        if (no == null) {
            No novo = new No();
            novo.item = v;
            return novo;
        }
        if (v <= no.item) {
            no.esq = inserirRecursivo(no.esq, v);
        } else {
            no.dir = inserirRecursivo(no.dir, v);
        }
        return balancear(no);
    }

    private int alturaNo(No no) {
        if (no == null) return -1;
        return 1 + Math.max(alturaNo(no.esq), alturaNo(no.dir));
    }

    public int fatorBalanceamento(No no) {
        if (no == null) return 0;
        return alturaNo(no.esq) - alturaNo(no.dir);
    }

    // Rotação simples à direita — corrige desequilíbrio LL
    public No rotacaoDireita(No y) {
        No x = y.esq;
        No t2 = x.dir;
        x.dir = y;
        y.esq = t2;
        return x;
    }

    // Rotação simples à esquerda — corrige desequilíbrio RR
    public No rotacaoEsquerda(No x) {
        No y = x.dir;
        No t2 = y.esq;
        y.esq = x;
        x.dir = t2;
        return y;
    }

    // Aplica rotação conforme o caso (LL, RR, LR, RL)
    private No balancear(No no) {
        if (no == null) return null;
        int fb = fatorBalanceamento(no);

        // Caso LL: subárvore esquerda pesada e filho esquerdo também pende para esquerda
        if (fb > 1 && fatorBalanceamento(no.esq) >= 0) {
            System.out.println("[AVL] Rebalanceando nó " + no.item + " (fb=" + fb + ") — caso LL → rotação simples à direita");
            return rotacaoDireita(no);
        }

        // Caso LR: subárvore esquerda pesada, mas filho esquerdo pende para direita
        if (fb > 1 && fatorBalanceamento(no.esq) < 0) {
            System.out.println("[AVL] Rebalanceando nó " + no.item + " (fb=" + fb + ") — caso LR → rotação dupla esquerda-direita");
            no.esq = rotacaoEsquerda(no.esq);
            return rotacaoDireita(no);
        }

        // Caso RR: subárvore direita pesada e filho direito também pende para direita
        if (fb < -1 && fatorBalanceamento(no.dir) <= 0) {
            System.out.println("[AVL] Rebalanceando nó " + no.item + " (fb=" + fb + ") — caso RR → rotação simples à esquerda");
            return rotacaoEsquerda(no);
        }

        // Caso RL: subárvore direita pesada, mas filho direito pende para esquerda
        if (fb < -1 && fatorBalanceamento(no.dir) > 0) {
            System.out.println("[AVL] Rebalanceando nó " + no.item + " (fb=" + fb + ") — caso RL → rotação dupla direita-esquerda");
            no.dir = rotacaoDireita(no.dir);
            return rotacaoEsquerda(no);
        }

        return no;
    }

    // Rebalanceia toda a árvore (útil após carregar uma árvore desbalanceada)
    public void balancearArvore() {
        setRoot(balancearRecursivo(getRoot()));
    }

    private No balancearRecursivo(No no) {
        if (no == null) return null;
        no.esq = balancearRecursivo(no.esq);
        no.dir = balancearRecursivo(no.dir);
        return balancear(no);
    }

    @Override
    public String getTiposArvore() {
        if (getRoot() == null) return "AVL (vazia)";
        return "AVL — " + super.getTiposArvore();
    }
}
