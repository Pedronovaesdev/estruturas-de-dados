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

    public static boolean salvarArvoreJson(Tree arvore, String caminhoArquivo, String nome, int ordem) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"nome\": \"").append(nome).append("\",\n");
            json.append("  \"ordem\": ").append(ordem).append(",\n");
            json.append("  \"arvore\": ");
            noToJson(arvore.getRoot(), json, 2);
            json.append("\n}");
            
            writer.write(json.toString());
            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar JSON: " + e.getMessage());
            return false;
        }
    }

    private static void noToJson(No no, StringBuilder sb, int indent) {
        if (no == null) {
            sb.append("null");
            return;
        }

        String space = " ".repeat(indent);
        sb.append("{\n");
        sb.append(space).append("  \"item\": ").append(no.item).append(",\n");
        sb.append(space).append("  \"esq\": ");
        noToJson(no.esq, sb, indent + 2);
        sb.append(",\n");
        sb.append(space).append("  \"dir\": ");
        noToJson(no.dir, sb, indent + 2);
        sb.append("\n").append(space).append("}");
    }

    public static Tree carregarArvoreJson(String caminhoArquivo) {
        try {
            String json = lerArquivoComoString(caminhoArquivo).trim();
            Tree arvore = new Tree();
            
            int arvoreStart = json.indexOf("\"arvore\":");
            if (arvoreStart == -1) return arvore;
            
            String arvoreJson = json.substring(json.indexOf("{", arvoreStart));
            arvore.setRoot(parseNoJson(arvoreJson, new int[]{0}));
            return arvore;
        } catch (IOException e) {
            System.err.println("Erro ao carregar JSON: " + e.getMessage());
            return null;
        }
    }

    private static No parseNoJson(String json, int[] pos) {
        skipWhitespace(json, pos);
        if (json.startsWith("null", pos[0])) {
            pos[0] += 4;
            return null;
        }

        if (pos[0] >= json.length() || json.charAt(pos[0]) != '{') return null;
        pos[0]++; // Pula '{'

        No no = new No();
        while (pos[0] < json.length() && json.charAt(pos[0]) != '}') {
            skipWhitespace(json, pos);
            if (pos[0] >= json.length() || json.charAt(pos[0]) == '}') break;
            
            if (json.startsWith("\"item\":", pos[0])) {
                pos[0] += 7;
                skipWhitespace(json, pos);
                int start = pos[0];
                while (pos[0] < json.length() && (Character.isDigit(json.charAt(pos[0])) || json.charAt(pos[0]) == '-')) {
                    pos[0]++;
                }
                no.item = Long.parseLong(json.substring(start, pos[0]));
            } else if (json.startsWith("\"esq\":", pos[0])) {
                pos[0] += 6;
                no.esq = parseNoJson(json, pos);
            } else if (json.startsWith("\"dir\":", pos[0])) {
                pos[0] += 6;
                no.dir = parseNoJson(json, pos);
            } else {
                pos[0]++;
            }
        }
        if (pos[0] < json.length() && json.charAt(pos[0]) == '}') {
            pos[0]++;
        }
        return no;
    }

    private static void skipWhitespace(String s, int[] pos) {
        while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) {
            pos[0]++;
        }
    }

    public static Tree carregarArvore(String caminhoArquivo) {
        if (caminhoArquivo.toLowerCase().endsWith(".json")) {
            return carregarArvoreJson(caminhoArquivo);
        }

        Tree arvore = new Tree();
        try {
            String texto = lerArquivoComoString(caminhoArquivo).trim();
            if (texto.isEmpty()) {
                return arvore;
            }

            // Se o arquivo começar com '{', provavelmente é um JSON mesmo sem a extensão .json
            if (texto.startsWith("{")) {
                return carregarArvoreJson(caminhoArquivo);
            }

            if (texto.startsWith("Árvore vazia")) {
                return arvore;
            }
            
            if (texto.startsWith("(")) {
                arvore.setRoot(parseParentesesAninhados(texto, new int[]{0}));
            } else {
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
        while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
            indice[0]++;
        }
        
        if (indice[0] >= texto.length() || texto.charAt(indice[0]) != '(') {
            return null;
        }
        
        indice[0]++; // Pula '('
        
        while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
            indice[0]++;
        }
        
        if (indice[0] < texto.length() && texto.charAt(indice[0]) == ')') {
            indice[0]++; // Pula ')'
            return null;
        }
        
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
            
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
            no.esq = parseParentesesAninhados(texto, indice);
            
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
            no.dir = parseParentesesAninhados(texto, indice);
            
            while (indice[0] < texto.length() && Character.isWhitespace(texto.charAt(indice[0]))) {
                indice[0]++;
            }
            
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
        
        if (no.esq != null) {
            sb.append(" ");
            converterParentesesAninhados(no.esq, sb);
        } else {
            sb.append(" ()");
        }
        
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
