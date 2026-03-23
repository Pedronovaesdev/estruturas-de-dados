import javax.swing.*;
import java.io.*;

public class GerenciadorArvore {

    public static boolean salvarArvore(Tree arvore, String caminhoArquivo) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            String conteudo = arvoreParaString(arvore);
            writer.write(conteudo);
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
        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("===") || linha.startsWith("Nível")
                        || linha.startsWith("Tipos") || linha.startsWith("Árvore")) {
                    continue;
                }
                try {
                    if (linha.startsWith("Valor: ")) {
                        String valorStr = linha.split("\\|")[0].replace("Valor: ", "").trim();
                        long valor = Long.parseLong(valorStr);
                        arvore.inserir(valor);
                    } else {
                        long valor = Long.parseLong(linha);
                        arvore.inserir(valor);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Valor ignorado (inválido): " + linha);
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

    private static String arvoreParaString(Tree arvore) {
        if (arvore.getRoot() == null) {
            return "Árvore vazia\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Informações da Árvore ===\n");
        sb.append("Nível da Árvore: ").append(arvore.getNivelArvore()).append("\n");
        sb.append("Tipos de Árvores: ").append(arvore.getTiposArvore()).append("\n");
        sb.append("\n=== Nós (Pré-Ordem) ===\n");
        preOrdem(arvore.getRoot(), sb, 0);
        return sb.toString();
    }

    private static void preOrdem(No no, StringBuilder sb, int profundidade) {
        if (no == null) {
            return;
        }

        sb.append("Valor: ").append(no.item);
        sb.append(" | Nível do Nó: ").append(profundidade);
        sb.append(" | Profundidade do Nó: ").append(profundidade);
        sb.append("\n");
        preOrdem(no.esq, sb, profundidade + 1);
        preOrdem(no.dir, sb, profundidade + 1);
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
