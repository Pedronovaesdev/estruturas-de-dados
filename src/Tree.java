import java.util.ArrayList;
import java.util.List;

public class Tree {
    private No root;
    private int count;

    private static final String NLR = "NLR";
    private static final String LNR = "LNR";
    private static final String LRN = "LRN";

    public Tree() { root=null; count=0; }

    public No getRoot() {
        return root;
    }
    
    public int getCount() {
        return count;
    }

    public void setRoot(No root) {
        this.root = root;
        this.count = contarNos(root);
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
        if (no.esq == null && no.dir == null) return true;
        return isCheia(no.esq) && isCheia(no.dir);
    }

    public boolean isDegenerada(No no) {
        if (no == null) return true;
        if (no.esq != null && no.dir != null) return false;
        return isDegenerada(no.esq) && isDegenerada(no.dir);
    }

    public String getTiposArvore() {
        if (root == null) return "Vazia";
        String tipo = new String();
        if (isBalanceada(root)) tipo = "Balanceada";
        if (isCompleta()) tipo = "Completa";
        if (isCheiaEstritamente()) tipo = "Cheia";
        if (isDegenerada(root)) tipo = "Degenerada";

        return tipo;
    }

    // Uma árvore cheia (estritamente binária) é aquela em que todos os nós internos têm exatamente 2 filhos
    // e todos os nós folhas estão no mesmo nível (altura)
    public boolean isCheiaEstritamente() {
        int altura = getAltura();
        return isCheiaEstritamenteHelper(root, 0, altura);
    }

    private boolean isCheiaEstritamenteHelper(No no, int nivel, int altura) {
        if (no == null) return true;
        if (no.esq == null && no.dir == null) {
            return nivel == altura;
        }
        if (no.esq == null || no.dir == null) return false;
        return isCheiaEstritamenteHelper(no.esq, nivel + 1, altura) && isCheiaEstritamenteHelper(no.dir, nivel + 1, altura);
    }

    public int getAltura() {
        return altura(root);
    }

    public int getNivelNo(No alvo) {
        return getNivelNoHelper(root, alvo, 0);
    }

    public int getProfundidadeNo(No alvo) {
        return getNivelNo(alvo);
    }

    private int getNivelNoHelper(No atual, No alvo, int nivel) {
        if (atual == null) return -1;
        if (atual == alvo) return nivel;
        int esq = getNivelNoHelper(atual.esq, alvo, nivel + 1);
        if (esq != -1) return esq;
        return getNivelNoHelper(atual.dir, alvo, nivel + 1);
    }

    public List<Long> buscarPorPercurso(String ordem) {
        List<Long> resultado = new ArrayList<>();

        if (ordem == null || ordem.trim().isEmpty()) {
            throw new IllegalArgumentException("Ordem inválida. Use NLR, LNR ou LRN.");
        }

        String ordemNormalizada = ordem.toUpperCase();
        if (!ordemNormalizada.equals(NLR) && !ordemNormalizada.equals(LNR) && !ordemNormalizada.equals(LRN)) {
            throw new IllegalArgumentException("Ordem inválida. Use NLR, LNR ou LRN.");
        }

        percorrer(root, ordemNormalizada, resultado);
        return resultado;
    }

    private int contarNos(No no) {
        if (no == null) {
            return 0;
        }
        return 1 + contarNos(no.esq) + contarNos(no.dir);
    }

    private void percorrer(No no, String ordem, List<Long> resultado) {
        if (no == null) {
            return;
        }

        switch (ordem) {
            case NLR:
                resultado.add(no.item);
                percorrer(no.esq, ordem, resultado);
                percorrer(no.dir, ordem, resultado);
                break;
            case LNR:
                percorrer(no.esq, ordem, resultado);
                resultado.add(no.item);
                percorrer(no.dir, ordem, resultado);
                break;
            case LRN:
                percorrer(no.esq, ordem, resultado);
                percorrer(no.dir, ordem, resultado);
                resultado.add(no.item);
                break;
            default:
                throw new IllegalArgumentException("Ordem inválida. Use NLR, LNR ou LRN.");
        }
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
