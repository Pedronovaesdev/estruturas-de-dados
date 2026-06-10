import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Tree {
    private No root;
    private int count;
    public List<String> historicoPassos = new ArrayList<>();

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

    public void setRoot(No root) {
        this.root = root;
        this.count = contarNos(root);
        // Ao carregar, precisamos reconstruir os ponteiros de pai
        reconstruirPais(null, this.root);
    }

    private void reconstruirPais(No pai, No atual) {
        if (atual == null) return;
        atual.pai = pai;
        reconstruirPais(atual, atual.esq);
        reconstruirPais(atual, atual.dir);
    }

    public void inserir(long v) {
        No novo = new No();
        novo.item = v;
        novo.dir = null;
        novo.esq = null;
        novo.pai = null;
        novo.isRed = true;

        if (root == null) {
            root = novo;
            root.isRed = false; // Raiz é sempre preta
            count++;
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
                    return; // Valor já existe
                }
            }
            
            novo.pai = anterior;
            if (v < anterior.item) {
                anterior.esq = novo;
            } else {
                anterior.dir = novo;
            }
            count++;
            // Aqui não balanceia, é o inserir simples
        }
    }

    public void resetar() {
        root = null;
        count = 0;
        if (historicoPassos != null) {
            historicoPassos.clear();
        }
    }

    public void inverter() {
        inverterRecursivo(root);
    }

    private void inverterRecursivo(No no) {
        if (no == null) {
            return;
        }

        No temporario = no.esq;
        no.esq = no.dir;
        no.dir = temporario;

        inverterRecursivo(no.esq);
        inverterRecursivo(no.dir);
    }

    public int altura(No no) {
        if (no == null)
            return -1;
        int altEsq = altura(no.esq);
        int altDir = altura(no.dir);
        return 1 + Math.max(altEsq, altDir);
    }

    public int getNivelArvore() {
        return altura(root);
    }

    public boolean isBalanceada(No no) {
        if (no == null)
            return true;
        int diff = Math.abs(altura(no.esq) - altura(no.dir));
        return diff <= 1 && isBalanceada(no.esq) && isBalanceada(no.dir);
    }

    public boolean isCompleta() {
        if (root == null)
            return true;
        LinkedList<No> fila = new LinkedList<>();
        fila.add(root);

        boolean encontrouNulo = false;
        while (!fila.isEmpty()) {
            No atual = fila.poll();
            if (atual.esq != null) {
                if (encontrouNulo)
                    return false;
                fila.add(atual.esq);
            } else {
                encontrouNulo = true;
            }

            if (atual.dir != null) {
                if (encontrouNulo)
                    return false;
                fila.add(atual.dir);
            } else {
                encontrouNulo = true;
            }
        }
        return true;
    }

    public boolean isCheia(No no) {
        if (no == null)
            return true;
        if ((no.esq == null && no.dir != null) || (no.esq != null && no.dir == null))
            return false;
        if (no.esq == null && no.dir == null)
            return true;
        return isCheia(no.esq) && isCheia(no.dir);
    }

    public boolean isDegenerada(No no) {
        if (no == null)
            return true;
        if (no.esq != null && no.dir != null)
            return false;
        return isDegenerada(no.esq) && isDegenerada(no.dir);
    }

    public String getTiposArvore() {
        if (root == null)
            return "Vazia";
        
        List<String> tipos = new ArrayList<>();
        tipos.add("Red-Black");
        
        if (isBalanceada(root)) tipos.add("Balanceada (Padrão AVL)");
        if (isCompleta()) tipos.add("Completa");
        if (isCheiaEstritamente()) tipos.add("Cheia");
        if (isDegenerada(root)) tipos.add("Degenerada");

        return String.join(", ", tipos);
    }

    public boolean isCheiaEstritamente() {
        int altura = getAltura();
        return isCheiaEstritamenteHelper(root, 0, altura);
    }

    private boolean isCheiaEstritamenteHelper(No no, int nivel, int altura) {
        if (no == null)
            return true;
        if (no.esq == null && no.dir == null) {
            return nivel == altura;
        }
        if (no.esq == null || no.dir == null)
            return false;
        return isCheiaEstritamenteHelper(no.esq, nivel + 1, altura)
                && isCheiaEstritamenteHelper(no.dir, nivel + 1, altura);
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
        if (atual == null)
            return -1;
        if (atual == alvo)
            return nivel;
        int esq = getNivelNoHelper(atual.esq, alvo, nivel + 1);
        if (esq != -1)
            return esq;
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
        if (no == null)
            return;

        caminhoAtual += no.item + (no.isRed ? "(R)" : "(B)");

        if (no.esq == null && no.dir == null) {
            caminhos.add(caminhoAtual);
        } else {
            caminhoAtual += " -> ";
            if (no.esq != null)
                encontrarCaminhos(no.esq, caminhoAtual, caminhos);
            if (no.dir != null)
                encontrarCaminhos(no.dir, caminhoAtual, caminhos);
        }
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

    public int getAlturaNoPorValor(long valor) {
        No no = buscarNo(valor);
        if (no == null) return -1;
        return altura(no);
    }

    public int getNivelNoPorValor(long valor) {
        No no = buscarNo(valor);
        if (no == null) return -1;
        return getNivelNo(no);
    }

    public int getProfundidadeArvore() {
        return getAltura();
    }

    public void inverterArvore() {
        root = inverterNoHelper(root);
    }

    private No inverterNoHelper(No no) {
        if (no == null) return null;
        No novoEsq = inverterNoHelper(no.dir);
        No novoDir = inverterNoHelper(no.esq);
        no.esq = novoEsq;
        no.dir = novoDir;
        return no;
    }

    // --- Lógica Red-Black ---

    public static class RelatorioRB {
        public boolean teveBalanceamento = false;
        public String tipoAcao = "";
        public List<String> passos = new ArrayList<>();
    }

    public RelatorioRB inserirRB(long v) {
        RelatorioRB relatorio = new RelatorioRB();
        relatorio.passos.add("Iniciando inserção Red-Black do valor: " + v);

        No novo = new No();
        novo.item = v;
        novo.isRed = true; // Todo novo nó nasce vermelho

        if (root == null) {
            root = novo;
            root.isRed = false; // Raiz deve ser preta
            relatorio.passos.add("Nó " + v + " inserido como raiz (cor alterada para PRETO).");
        } else {
            No atual = root;
            No pai = null;
            while (atual != null) {
                pai = atual;
                if (v < atual.item) {
                    relatorio.passos.add("Valor " + v + " < " + atual.item + " -> Esquerda.");
                    atual = atual.esq;
                } else if (v > atual.item) {
                    relatorio.passos.add("Valor " + v + " > " + atual.item + " -> Direita.");
                    atual = atual.dir;
                } else {
                    relatorio.passos.add("Valor " + v + " já existe. Abortando.");
                    return relatorio;
                }
            }

            novo.pai = pai;
            if (v < pai.item) {
                pai.esq = novo;
            } else {
                pai.dir = novo;
            }
            relatorio.passos.add("Nó " + v + " inserido como filho de " + pai.item + " (cor: VERMELHO).");
            
            if (pai.isRed) {
                relatorio.teveBalanceamento = true;
                balancearAposInsercao(novo, relatorio);
            }
        }

        this.count = contarNos(root);
        
        if (historicoPassos == null) historicoPassos = new ArrayList<>();
        historicoPassos.add("=== Inserção RB: " + v + " ===");
        historicoPassos.addAll(relatorio.passos);
        historicoPassos.add("");

        return relatorio;
    }

    private void balancearAposInsercao(No z, RelatorioRB relatorio) {
        while (z.pai != null && z.pai.isRed) {
            if (z.pai == z.pai.pai.esq) {
                No y = z.pai.pai.dir; // tio
                if (y != null && y.isRed) {
                    // Caso 1: Tio é vermelho -> recolorir
                    relatorio.passos.add("Caso 1: Tio (" + y.item + ") é VERMELHO.");
                    z.pai.isRed = false;
                    y.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Recoloração: Pai e Tio -> PRETO, Avô -> VERMELHO.");
                    z = z.pai.pai;
                } else {
                    // Caso 2 ou 3: Tio é preto
                    if (z == z.pai.dir) {
                        // Caso 2: z é filho direito -> rotação à esquerda no pai
                        relatorio.passos.add("Caso 2: Tio é PRETO e nó é filho DIREITO.");
                        z = z.pai;
                        relatorio.passos.add("Rotação Esquerda no nó " + z.item + ".");
                        rotacaoEsquerda(z);
                    }
                    // Caso 3: z é filho esquerdo -> rotação à direita no avô
                    relatorio.passos.add("Caso 3: Tio é PRETO e nó é filho ESQUERDO.");
                    z.pai.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Recoloração: Pai -> PRETO, Avô -> VERMELHO.");
                    relatorio.passos.add("Rotação Direita no avô " + z.pai.pai.item + ".");
                    rotacaoDireita(z.pai.pai);
                }
            } else {
                // Simétrico
                No y = z.pai.pai.esq; // tio
                if (y != null && y.isRed) {
                    relatorio.passos.add("Caso 1 (Simétrico): Tio (" + y.item + ") é VERMELHO.");
                    z.pai.isRed = false;
                    y.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Recoloração: Pai e Tio -> PRETO, Avô -> VERMELHO.");
                    z = z.pai.pai;
                } else {
                    if (z == z.pai.esq) {
                        relatorio.passos.add("Caso 2 (Simétrico): Tio é PRETO e nó é filho ESQUERDO.");
                        z = z.pai;
                        relatorio.passos.add("Rotação Direita no nó " + z.item + ".");
                        rotacaoDireita(z);
                    }
                    relatorio.passos.add("Caso 3 (Simétrico): Tio é PRETO e nó é filho DIREITO.");
                    z.pai.isRed = false;
                    z.pai.pai.isRed = true;
                    relatorio.passos.add("Recoloração: Pai -> PRETO, Avô -> VERMELHO.");
                    relatorio.passos.add("Rotação Esquerda no avô " + z.pai.pai.item + ".");
                    rotacaoEsquerda(z.pai.pai);
                }
            }
        }
        root.isRed = false;
    }

    private void rotacaoEsquerda(No x) {
        No y = x.dir;
        x.dir = y.esq;
        if (y.esq != null) {
            y.esq.pai = x;
        }
        y.pai = x.pai;
        if (x.pai == null) {
            root = y;
        } else if (x == x.pai.esq) {
            x.pai.esq = y;
        } else {
            x.pai.dir = y;
        }
        y.esq = x;
        x.pai = y;
    }

    private void rotacaoDireita(No y) {
        No x = y.esq;
        y.esq = x.dir;
        if (x.dir != null) {
            x.dir.pai = y;
        }
        x.pai = y.pai;
        if (y.pai == null) {
            root = x;
        } else if (y == y.pai.dir) {
            y.pai.dir = x;
        } else {
            y.pai.esq = x;
        }
        x.dir = y;
        y.pai = x;
    }

}
