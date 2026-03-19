import javax.swing.*;
import java.io.*;

public class GerenciadorArvore {

    public static boolean salvarArvore(Tree arvore, String caminhoArquivo) {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            String conteudo = arvoreParaString(arvore.getRoot());
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
                if (!linha.isEmpty()) {
                    try {
                        long valor = Long.parseLong(linha);
                        arvore.inserir(valor);
                    } catch (NumberFormatException e) {
                        System.err.println("Valor ignorado (inválido): " + linha);
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

    private static String arvoreParaString(No no) {
        if (no == null) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        preOrdem(no, sb);
        return sb.toString();
    }

    private static void preOrdem(No no, StringBuilder sb) {
        if (no == null) {
            return;
        }
        
        sb.append(no.item).append("\n");
        preOrdem(no.esq, sb);
        preOrdem(no.dir, sb);
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
