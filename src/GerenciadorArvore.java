import javax.swing.*;
import java.io.*;

public class GerenciadorArvore {

    public static boolean salvarArvore(Tree arvore, String caminhoArquivo) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            String conteudo = arvoreParaString(arvore.getRoot());
            writer.write(conteudo);
            writer.write(System.lineSeparator());
            writer.flush();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao salvar a árvore: " + e.getMessage(), 
                "Erro de Salvamento", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public static Tree carregarArvore(String caminhoArquivo) {
        Tree arvore = new Tree();
        try {
            String texto = lerArquivoComoString(caminhoArquivo).trim();
            if (texto.isEmpty()) {
                return arvore;
            }

            if (texto.startsWith("Árvore vazia")) {
                return arvore;
            }
            
            // Tenta ler no novo formato (Parênteses Aninhados)
            if (texto.startsWith("(")) {
                arvore.setRoot(parseParentesesAninhados(texto, new int[]{0}));
            } else {
                // Tenta ler no formato antigo (um valor por linha)
                try (BufferedReader readerAntigo = new BufferedReader(new FileReader(caminhoArquivo))) {
                    String linhaAntiga;
                    while ((linhaAntiga = readerAntigo.readLine()) != null) {
                        linhaAntiga = linhaAntiga.trim();
                        if (linhaAntiga.isEmpty() || linhaAntiga.startsWith("===") || linhaAntiga.startsWith("Nível")
                                || linhaAntiga.startsWith("Tipos") || linhaAntiga.startsWith("Árvore")) {
                            continue;
                        }

                        String candidato = linhaAntiga;
                        if (linhaAntiga.startsWith("Valor: ")) {
                            candidato = linhaAntiga.split("\\|")[0].replace("Valor: ", "").trim();
                        }

                        try {
                            long valor = Long.parseLong(candidato);
                            arvore.inserir(valor);
                        } catch (NumberFormatException e) {
                            System.err.println("Valor ignorado (inválido): " + linhaAntiga);
                        }
                    }
                }
            }

            return arvore;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Arquivo não encontrado: " + caminhoArquivo, 
                "Erro de Carregamento", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Erro ao carregar a árvore: " + e.getMessage(), 
                "Erro de Carregamento", 
                JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static String lerArquivoComoString(String caminhoArquivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                sb.append(linha);
            }
        }
        return sb.toString();
    }

    private static No parseParentesesAninhados(String texto, int[] indice) {
        // Pula espaços
        while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
            indice[0]++;
        }
        
        if (indice[0] >= texto.length() || texto.charAt(indice[0]) != '(') {
            return null;
        }
        
        indice[0]++; // Pula '('
        
        // Pula espaços
        while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
            indice[0]++;
        }
        
        // Verifica se é um parêntese vazio "()"
        if (indice[0] < texto.length() && texto.charAt(indice[0]) == ')') {
            indice[0]++; // Pula ')'
            return null;
        }
        
        // Extrai o valor do nó
        StringBuilder valor = new StringBuilder();
        while (indice[0] < texto.length() && texto.charAt(indice[0]) != '(' && texto.charAt(indice[0]) != ')') {
            char c = texto.charAt(indice[0]);
            if (!Character.isWhitespace(c)) {
                valor.append(c);
            }
            indice[0]++;
        }
        
        try {
            long v = Long.parseLong(valor.toString());
            No no = new No();
            no.item = v;
            
            // Pula espaços
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
            // Parse subárvore esquerda
            no.esq = parseParentesesAninhados(texto, indice);
            
            // Pula espaços
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
            // Parse subárvore direita
            no.dir = parseParentesesAninhados(texto, indice);
            
            // Pula espaços
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
            // Pula ')'
            if (indice[0] < texto.length() && texto.charAt(indice[0]) == ')') {
                indice[0]++;
            }
            
            return no;
        } catch (NumberFormatException e) {
            System.err.println("Erro ao parsear valor: " + valor.toString());
            return null;
        }
    }

    private static String arvoreParaString(No no) {
        if (no == null) {
            return "()";
        }

        StringBuilder sb = new StringBuilder();

        converterParentesesAninhados(no, sb);
        return sb.toString();
    }

    private static void converterParentesesAninhados(No no, StringBuilder sb) {
        if (no == null) {
            sb.append("()");
            return;
        }
                
        sb.append("(").append(no.item);
        
        // Subárvore esquerda
        if (no.esq != null) {
            sb.append(" ");
            converterParentesesAninhados(no.esq, sb);
        } else {
            sb.append(" ()");
        }
        
        // Subárvore direita
        if (no.dir != null) {
            sb.append(" ");
            converterParentesesAninhados(no.dir, sb);
        } else {
            sb.append(" ()");
        }
        
        sb.append(")");
    }

    public static String abrirDialogoSalvar(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Árvore");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File("arvore.txt"));
        
        int resultado = fileChooser.showSaveDialog(parent);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    public static String abrirDialogoCarregar(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Carregar Árvore");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int resultado = fileChooser.showOpenDialog(parent);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}
