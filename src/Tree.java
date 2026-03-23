import java.util.ArrayList;
import java.util.List;

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

    public int getAltura() {
        return calcularAltura(root);
    }

    private int calcularAltura(No no) {
        if (no == null) {
            return -1;
        }
        return 1 + Math.max(calcularAltura(no.esq), calcularAltura(no.dir));
    }

    public List<String> getCaminhos() {
        List<String> caminhos = new ArrayList<>();
        if (root != null) {
            encontrarCaminhos(root, "", caminhos);
        }
        return caminhos;
    }

    private void encontrarCaminhos(No no, String caminhoAtual, List<String> caminhos) {
        if (no == null) return;
        
        caminhoAtual += no.item;
        
        if (no.esq == null && no.dir == null) {
            caminhos.add(caminhoAtual);
        } else {
            caminhoAtual += " -> ";
            if (no.esq != null) encontrarCaminhos(no.esq, caminhoAtual, caminhos);
            if (no.dir != null) encontrarCaminhos(no.dir, caminhoAtual, caminhos);
        }
    }

}
