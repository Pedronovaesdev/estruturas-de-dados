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

        if (root == null) root = novo;
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
}
