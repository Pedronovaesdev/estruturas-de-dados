import java.util.List;

public class TestAnalise {
    public static void main(String[] args) {
        Tree arvore = new Tree();
        
        // Inserindo os valores conforme mostrado na imagem
        arvore.inserir(30);
        arvore.inserir(18);
        arvore.inserir(37);
        arvore.inserir(14);
        arvore.inserir(20);
        arvore.inserir(38);
        arvore.inserir(45);
        arvore.inserir(11);
        arvore.inserir(17);
        arvore.inserir(19);
        arvore.inserir(21);
        arvore.inserir(25);
        arvore.inserir(42);
        arvore.inserir(53);
        arvore.inserir(5);
        arvore.inserir(12);
        arvore.inserir(16);
        arvore.inserir(20);
        arvore.inserir(25);
        arvore.inserir(39);

        System.out.println("═══════════════════════════════════════════");
        System.out.println("        ANÁLISE COMPLETA DA ÁRVORE");
        System.out.println("═══════════════════════════════════════════\n");

        // a) Nível da árvore e nível do nó 21
        System.out.println("a) NÍVEL:");
        System.out.println("   • Nível da Árvore: " + arvore.getNivelArvore());
        int nivelNo21 = arvore.getNivelNoPorValor(21);
        if (nivelNo21 >= 0) {
            System.out.println("   • Nível do Nó 21: " + nivelNo21);
        } else {
            System.out.println("   • Nível do Nó 21: Nó não encontrado!");
        }

        // b) Altura da árvore e altura do nó 21
        System.out.println("\nb) ALTURA:");
        System.out.println("   • Altura da Árvore: " + arvore.getAltura());
        int alturaNo21 = arvore.getAlturaNoPorValor(21);
        if (alturaNo21 >= 0) {
            System.out.println("   • Altura do Nó 21: " + alturaNo21);
        } else {
            System.out.println("   • Altura do Nó 21: Nó não encontrado!");
        }

        // c) Profundidade da árvore e profundidade do nó 21
        System.out.println("\nc) PROFUNDIDADE:");
        System.out.println("   • Profundidade da Árvore: " + arvore.getProfundidadeArvore());
        int profundidadeNo21 = arvore.getNivelNoPorValor(21);
        if (profundidadeNo21 >= 0) {
            System.out.println("   • Profundidade do Nó 21: " + profundidadeNo21);
        } else {
            System.out.println("   • Profundidade do Nó 21: Nó não encontrado!");
        }

        // d) Os 3 tipos de percurso
        System.out.println("\nd) OS 3 TIPOS DE PERCURSO:");
        
        java.util.List<Long> nlr = arvore.buscarPorPercurso("NLR");
        System.out.print("   • Pré-Ordem (NLR): ");
        for (int i = 0; i < nlr.size(); i++) {
            System.out.print(nlr.get(i));
            if (i < nlr.size() - 1) System.out.print(" → ");
        }
        System.out.println();

        java.util.List<Long> lnr = arvore.buscarPorPercurso("LNR");
        System.out.print("   • Em Ordem (LNR): ");
        for (int i = 0; i < lnr.size(); i++) {
            System.out.print(lnr.get(i));
            if (i < lnr.size() - 1) System.out.print(" → ");
        }
        System.out.println();

        java.util.List<Long> lrn = arvore.buscarPorPercurso("LRN");
        System.out.print("   • Pós-Ordem (LRN): ");
        for (int i = 0; i < lrn.size(); i++) {
            System.out.print(lrn.get(i));
            if (i < lrn.size() - 1) System.out.print(" → ");
        }
        System.out.println();

        // e) Inverter a árvore
        System.out.println("\ne) INVERSÃO/ESPELHAMENTO DA ÁRVORE:");
        System.out.println("   Originalmente (Pré-Ordem): " + formatarPercurso(nlr));
        
        Tree arvoreCopia = cloneArvore(arvore);
        arvoreCopia.inverterArvore();
        
        java.util.List<Long> nlrInvertido = arvoreCopia.buscarPorPercurso("NLR");
        System.out.println("   Após Inversão (Pré-Ordem): " + formatarPercurso(nlrInvertido));
        
        System.out.println("\n   Comparação de Percursos da Árvore Invertida:");
        java.util.List<Long> lnrInvertido = arvoreCopia.buscarPorPercurso("LNR");
        System.out.println("   • Em Ordem (LNR): " + formatarPercurso(lnrInvertido));
        
        java.util.List<Long> lrnInvertido = arvoreCopia.buscarPorPercurso("LRN");
        System.out.println("   • Pós-Ordem (LRN): " + formatarPercurso(lrnInvertido));

        System.out.println("\n═══════════════════════════════════════════");
        System.out.println("Total de Nós na Árvore: " + arvore.getCount());
        System.out.println("Tipo da Árvore: " + arvore.getTiposArvore());
        System.out.println("═══════════════════════════════════════════\n");
    }

    private static String formatarPercurso(java.util.List<Long> lista) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lista.size(); i++) {
            sb.append(lista.get(i));
            if (i < lista.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }

    private static Tree cloneArvore(Tree arvore) {
        Tree novaArvore = new Tree();
        if (arvore.getRoot() != null) {
            novaArvore.setRoot(cloneNo(arvore.getRoot()));
        }
        return novaArvore;
    }

    private static No cloneNo(No no) {
        if (no == null) return null;
        No novo = new No();
        novo.item = no.item;
        novo.esq = cloneNo(no.esq);
        novo.dir = cloneNo(no.dir);
        return novo;
    }
}
