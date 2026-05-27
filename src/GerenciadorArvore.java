import javax.swing.*;
import java.io.*;

public class GerenciadorArvore {

    public static void iniciarDialogoSalvar(JFrame parent, Tree arvore, int ordem) {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(parent, "A árvore está vazia! Não há nada para salvar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opcoes = {"Normal (.txt)", "JSON (.json)"};
        int escolha = JOptionPane.showOptionDialog(parent, 
            "Escolha o formato de salvamento:", 
            "Salvar Árvore", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            opcoes, 
            opcoes[0]);

        if (escolha == -1) return;

        String caminho = abrirDialogoSalvar(parent);
        if (caminho != null) {
            boolean sucesso;
            if (escolha == 0) { // Normal
                if (!caminho.toLowerCase().endsWith(".txt")) caminho += ".txt";
                sucesso = salvarArvore(arvore, caminho);
            } else { // JSON
                if (!caminho.toLowerCase().endsWith(".json")) caminho += ".json";
                sucesso = salvarArvoreJson(arvore, caminho, "ArvoreManual", ordem);
            }

            if (sucesso) {
                JOptionPane.showMessageDialog(parent, "Árvore salva com sucesso em:\n" + caminho, "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static Tree iniciarDialogoCarregar(JFrame parent) {
        String caminho = abrirDialogoCarregar(parent);
        if (caminho != null) {
            Tree novaArvore = carregarArvore(caminho);
            if (novaArvore != null) {
                JOptionPane.showMessageDialog(parent,
                        "Árvore carregada com sucesso!\nNós carregados: " + novaArvore.getCount(), "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                return novaArvore;
            }
        }
        return null;
    }

    public static void salvarRelatorioPassos(JFrame parent, Tree arvore) {
        if (arvore.historicoPassos == null || arvore.historicoPassos.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "O relatório de passos está vazio! Nenhuma ação foi registrada ainda.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório Passo a Passo");
        fileChooser.setSelectedFile(new java.io.File("relatorio_passos_arvore.txt"));

        int resultado = fileChooser.showSaveDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write("RELATÓRIO PASSO A PASSO DA CONSTRUÇÃO DA ÁRVORE\n");
                writer.write("===============================================\n\n");
                for (String linha : arvore.historicoPassos) {
                    writer.write(linha + "\n");
                }
                writer.flush();
                JOptionPane.showMessageDialog(parent, "Relatório de passos salvo com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(parent, "Erro ao salvar relatório: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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
