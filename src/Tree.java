import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tree {
    private No root;
    private int count;
    private TreeType type = TreeType.BST;
    public List<String> historicoPassos = new ArrayList<>();
    private List<Long> valoresInseridos = new ArrayList<>();

    private static final String NLR = "NLR";
    private static final String LNR = "LNR";
    private static final String LRN = "LRN";

    public Tree() {
        root = null;
        count = 0;
        historicoPassos.clear();
    }

    public No getRoot() {
        return root;
    }

    public int getCount() {
        return count;
    }

    public TreeType getType() {
        return type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }

    public List<Long> getValoresInseridos() {
        return new ArrayList<>(valoresInseridos);
    }

    public void setRoot(No root) {
        this.root = root;
        this.count = contarNos(root);
        reconstruirPais(null, this.root);
        // Se a ordem de inserção estiver vazia ao carregar, fazemos um percurso em nível como fallback
        if (valoresInseridos.isEmpty() && root != null) {
            popularOrdemPorNivel();
        }
    }

    private void popularOrdemPorNivel() {
        LinkedList<No> fila = new LinkedList<>();
        fila.add(root);
        while (!fila.isEmpty()) {
            No n = fila.poll();
            valoresInseridos.add(n.item);
            if (n.esq != null) fila.add(n.esq);
            if (n.dir != null) fila.add(n.dir);
        }
    }

    private void reconstruirPais(No pai, No atual) {
        if (atual == null) return;
        atual.pai = pai;
        reconstruirPais(atual, atual.esq);
        reconstruirPais(atual, atual.dir);
    }

    // Método principal de inserção que decide qual lógica usar
    public RelatorioRB inserirComLogica(long v) {
        switch (type) {
            case AVL:
                return inserirAVL(v);
            case RED_BLACK:
                return inserirRB(v);
            default:
                inserir(v);
                RelatorioRB rel = new RelatorioRB();
                rel.passos.add("Inserção simples BST do valor: " + v);
                return rel;
        }
    }

    public void inserir(long v) {
        No novo = new No();
        novo.item = v;
        novo.isRed = true;

        if (root == null) {
            root = novo;
            root.isRed = false;
            count++;
            valoresInseridos.add(v);
        } else {
            No atual = root;
            No anterior = null;
            while (atual != null) {
                anterior = atual;
                if (v < atual.item) {
                    atual = atual.esq;
                } else if (v > atual.item) {
                    atual = atual.dir;
                } else {
                    return;
                }
            }
            novo.pai = anterior;
            if (v < anterior.item) {
                anterior.esq = novo;
            } else {
                anterior.dir = novo;
            }
            count++;
            valoresInseridos.add(v);
        }
    }

    public void resetar() {
        root = null;
        count = 0;
        historicoPassos.clear();
        valoresInseridos.clear();
    }

    public void inverter() {
        inverterRecursivo(root);
    }

    private void inverterRecursivo(No no) {
        if (no == null) return;
        No temp = no.esq;
        no.esq = no.dir;
        no.dir = temp;
        inverterRecursivo(no.esq);
        inverterRecursivo(no.dir);
    }

    public int altura(No no) {
        if (no == null) return -1;
        return 1 + Math.max(altura(no.esq), altura(no.dir));
    }

    public int getAltura() {
        return altura(root);
    }

    public String getTiposArvore() {
        if (root == null) return "Vazia";
        List<String> tipos = new ArrayList<>();
        tipos.add(type.getDescricao());
        if (isBalanceada(root)) tipos.add("Balanceada (Padrão AVL)");
        if (isCompleta()) tipos.add("Completa");
        if (isCheiaEstritamente()) tipos.add("Cheia");
        if (isDegenerada(root)) tipos.add("Degenerada");
        return String.join(", ", tipos);
    }

    public boolean isBalanceada(No no) {
        if (no == null) return true;
        int diff = Math.abs(altura(no.esq) - altura(no.dir));
        return diff <= 1 && isBalanceada(no.esq) && isBalanceada(no.dir);
    }

    public boolean isCompleta() {
        if (root == null) return true;
        LinkedList<No> fila = new LinkedList<>();
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

    public boolean isCheiaEstritamente() {
        return isCheiaEstritamenteHelper(root, 0, getAltura());
    }

    private boolean isCheiaEstritamenteHelper(No no, int nivel, int altura) {
        if (no == null) return true;
        if (no.esq == null && no.dir == null) return nivel == altura;
        if (no.esq == null || no.dir == null) return false;
        return isCheiaEstritamenteHelper(no.esq, nivel + 1, altura) && isCheiaEstritamenteHelper(no.dir, nivel + 1, altura);
    }

    public boolean isDegenerada(No no) {
        if (no == null) return true;
        if (no.esq != null && no.dir != null) return false;
        return isDegenerada(no.esq) && isDegenerada(no.dir);
    }

    public List<Long> buscarPorPercurso(String ordem) {
        List<Long> resultado = new ArrayList<>();
        percorrer(root, ordem.toUpperCase(), resultado);
        return resultado;
    }

    private void percorrer(No no, String ordem, List<Long> resultado) {
        if (no == null) return;
        if (ordem.equals(NLR)) resultado.add(no.item);
        percorrer(no.esq, ordem, resultado);
        if (ordem.equals(LNR)) resultado.add(no.item);
        percorrer(no.dir, ordem, resultado);
        if (ordem.equals(LRN)) resultado.add(no.item);
    }

    public List<String> getCaminhos() {
        List<String> caminhos = new ArrayList<>();
        encontrarCaminhos(root, "", caminhos);
        return caminhos;
    }

    private void encontrarCaminhos(No no, String caminhoAtual, List<String> caminhos) {
        if (no == null) return;
        caminhoAtual += no.item + (type == TreeType.RED_BLACK ? (no.isRed ? "(R)" : "(B)") : "");
        if (no.esq == null && no.dir == null) {
            caminhos.add(caminhoAtual);
        } else {
            caminhoAtual += " -> ";
            encontrarCaminhos(no.esq, caminhoAtual, caminhos);
            encontrarCaminhos(no.dir, caminhoAtual, caminhos);
        }
    }

    private int contarNos(No no) {
        if (no == null) return 0;
        return 1 + contarNos(no.esq) + contarNos(no.dir);
    }

    public No buscarNo(long valor) {
        return buscarNoHelper(root, valor);
    }

    private No buscarNoHelper(No no, long valor) {
        if (no == null) return null;
        if (no.item == valor) return no;
        if (valor < no.item) return buscarNoHelper(no.esq, valor);
        return buscarNoHelper(no.dir, valor);
    }

    // --- Lógica AVL ---
    public RelatorioRB inserirAVL(long v) {
        if (buscarNo(v) != null) return new RelatorioRB(); 
        RelatorioRB relatorio = new RelatorioRB();
        relatorio.passos.add("Iniciando inserção AVL do valor: " + v);
        root = inserirAVLRecursivo(root, null, v, relatorio);
        this.count = contarNos(root);
        valoresInseridos.add(v);
        historicoPassos.add("=== Inserção AVL: " + v + " ===");
        historicoPassos.addAll(relatorio.passos);
        return relatorio;
    }

    private No inserirAVLRecursivo(No no, No pai, long v, RelatorioRB relatorio) {
        if (no == null) {
            No novo = new No();
            novo.item = v;
            novo.pai = pai;
            relatorio.passos.add("Nó " + v + " inserido.");
            return novo;
        }

        if (v < no.item) {
            no.esq = inserirAVLRecursivo(no.esq, no, v, relatorio);
        } else if (v > no.item) {
            no.dir = inserirAVLRecursivo(no.dir, no, v, relatorio);
        } else return no;

        int fator = altura(no.esq) - altura(no.dir);
        if (fator > 1) {
            relatorio.teveBalanceamento = true;
            if (altura(no.esq.esq) >= altura(no.esq.dir)) {
                relatorio.passos.add("Rotação Simples Direita no " + no.item);
                return rotacaoDireitaAVL(no);
            } else {
                relatorio.passos.add("Rotação Dupla Direita no " + no.item);
                no.esq = rotacaoEsquerdaAVL(no.esq);
                return rotacaoDireitaAVL(no);
            }
        }
        if (fator < -1) {
            relatorio.teveBalanceamento = true;
            if (altura(no.dir.dir) >= altura(no.dir.esq)) {
                relatorio.passos.add("Rotação Simples Esquerda no " + no.item);
                return rotacaoEsquerdaAVL(no);
            } else {
                relatorio.passos.add("Rotação Dupla Esquerda no " + no.item);
                no.dir = rotacaoDireitaAVL(no.dir);
                return rotacaoEsquerdaAVL(no);
            }
        }
        return no;
    }

    private No rotacaoEsquerdaAVL(No x) {
        No y = x.dir;
        x.dir = y.esq;
        if (y.esq != null) y.esq.pai = x;
        y.pai = x.pai;
        y.esq = x;
        x.pai = y;
        return y;
    }

    private No rotacaoDireitaAVL(No y) {
        No x = y.esq;
        y.esq = x.dir;
        if (x.dir != null) x.dir.pai = y;
        x.pai = y.pai;
        x.dir = y;
        y.pai = x;
        return x;
    }

    // --- Lógica Red-Black ---
    public static class RelatorioRB {
        public boolean teveBalanceamento = false;
        public List<String> passos = new ArrayList<>();
    }

    public RelatorioRB inserirRB(long v) {
        if (buscarNo(v) != null) return new RelatorioRB();
        RelatorioRB relatorio = new RelatorioRB();
        relatorio.passos.add("Iniciando inserção Red-Black do valor: " + v);
        No novo = new No();
        novo.item = v;
        novo.isRed = true;

        if (root == null) {
            root = novo;
            root.isRed = false;
            relatorio.passos.add("Nó " + v + " inserido como raiz (PRETO).");
        } else {
            No atual = root;
            No pai = null;
            while (atual != null) {
                pai = atual;
                if (v < atual.item) atual = atual.esq;
                else if (v > atual.item) atual = atual.dir;
                else return relatorio;
            }
            novo.pai = pai;
            if (v < pai.item) pai.esq = novo;
            else pai.dir = novo;
            relatorio.passos.add("Nó " + v + " inserido. Iniciando balanceamento.");
            if (pai.isRed) {
                relatorio.teveBalanceamento = true;
                balancearAposInsercao(novo, relatorio);
            }
        }
        this.count = contarNos(root);
        valoresInseridos.add(v);
        historicoPassos.add("=== Inserção RB: " + v + " ===");
        historicoPassos.addAll(relatorio.passos);
        return relatorio;
    }

    private void balancearAposInsercao(No z, RelatorioRB relatorio) {
        while (z.pai != null && z.pai.isRed) {
            if (z.pai == z.pai.pai.esq) {
                No y = z.pai.pai.dir;
                if (y != null && y.isRed) {
                    z.pai.isRed = false;
                    y.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Caso 1: Recoloração.");
                    z = z.pai.pai;
                } else {
                    if (z == z.pai.dir) {
                        z = z.pai;
                        rotacaoEsquerda(z);
                        relatorio.passos.add("Caso 2: Rotação Esquerda.");
                    }
                    z.pai.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Caso 3: Rotação Direita no " + z.pai.pai.item);
                    rotacaoDireita(z.pai.pai);
                }
            } else {
                No y = z.pai.pai.esq;
                if (y != null && y.isRed) {
                    z.pai.isRed = false;
                    y.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Caso 1 (Simétrico): Recoloração.");
                    z = z.pai.pai;
                } else {
                    if (z == z.pai.esq) {
                        z = z.pai;
                        rotacaoDireita(z);
                        relatorio.passos.add("Caso 2 (Simétrico): Rotação Direita.");
                    }
                    z.pai.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Caso 3 (Simétrico): Rotação Esquerda no " + z.pai.pai.item);
                    rotacaoEsquerda(z.pai.pai);
                }
            }
        }
        root.isRed = false;
    }

    private void rotacaoEsquerda(No x) {
        No y = x.dir;
        x.dir = y.esq;
        if (y.esq != null) y.esq.pai = x;
        y.pai = x.pai;
        if (x.pai == null) root = y;
        else if (x == x.pai.esq) x.pai.esq = y;
        else x.pai.dir = y;
        y.esq = x;
        x.pai = y;
    }

    private void rotacaoDireita(No y) {
        No x = y.esq;
        y.esq = x.dir;
        if (x.dir != null) x.dir.pai = y;
        x.pai = y.pai;
        if (y.pai == null) root = x;
        else if (y == y.pai.dir) y.pai.dir = x;
        else y.pai.esq = x;
        x.dir = y;
        y.pai = x;
    }
}
