public class Tree {
    private No root;
    private int count;

    public Tree() { root=null; count=0; }

    public No getRoot() {
        return root;
    }
    
    public int getCount() {
        return count;
    }

    public void setRoot(No root) {
        this.root = root;
    }

    public void inserir(long v) {
        No novo = new No();
        novo.item = v;
        novo.dir = null;
        novo.esq = null;

        if (root == null) {
            root = novo;
            count++;
        }
        else  { // se nao for a raiz
            No atual = root;
            No anterior;
            while(true) {
                anterior = atual;
                if (v <= atual.item) {
                    atual = atual.esq;
                    if (atual == null) {
                        anterior.esq = novo;
                        count++;
                        return;
                    }
                }
                else {
                    atual = atual.dir;
                    if (atual == null) {
                        anterior.dir = novo;
                        count++;
                        return;
                    }
                }
            }
        }
    }

    public void resetar() {
        root = null;
        count = 0;
    }

    public int altura(No no) {
        if (no == null) return -1;
        int altEsq = altura(no.esq);
        int altDir = altura(no.dir);
        return 1 + Math.max(altEsq, altDir);
    }

    public int getNivelArvore() {
        return altura(root);
    }

    public boolean isBalanceada(No no) {
        if (no == null) return true;
        int diff = Math.abs(altura(no.esq) - altura(no.dir));
        return diff <= 1 && isBalanceada(no.esq) && isBalanceada(no.dir);
    }

    public boolean isCompleta() {
        if (root == null) return true;
        java.util.Queue<No> fila = new java.util.LinkedList<>();
        fila.add(root);
        boolean encontrouNulo = false;
        while (!fila.isEmpty()) {
            No atual = fila.poll();
            if (atual.esq != null) {
                if (encontrouNulo) return false;
                fila.add(atual.esq);
            } else {
                encontrouNulo = true;
            }
            if (atual.dir != null) {
                if (encontrouNulo) return false;
                fila.add(atual.dir);
            } else {
                encontrouNulo = true;
            }
        }
        return true;
    }

    public boolean isCheia(No no) {
        if (no == null) return true;
        if ((no.esq == null && no.dir != null) || (no.esq != null && no.dir == null)) return false;
        return isCheia(no.esq) && isCheia(no.dir);
    }

    public boolean isDegenerada(No no) {
        if (no == null) return true;
        if (no.esq != null && no.dir != null) return false;
        return isDegenerada(no.esq) && isDegenerada(no.dir);
    }

    public String getTiposArvore() {
        if (root == null) return "Vazia";
        StringBuilder tipos = new StringBuilder("Árvore Binária de Busca");
        if (isBalanceada(root)) tipos.append(", Balanceada");
        if (isCompleta()) tipos.append(", Completa");
        if (isCheia(root)) tipos.append(", Cheia");
        if (isDegenerada(root)) tipos.append(", Degenerada");
        return tipos.toString();
    }

}
