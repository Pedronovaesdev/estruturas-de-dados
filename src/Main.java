import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private Tree arvore;
    private PainelDesenho painelArvore;
    private JTextField campoEntrada;
    private JLabel lblContador;
    private JLabel lblAltura;
    private JButton btnTipoArvore;
    private JComboBox<TreeType> comboTipoArvore;

    private java.util.List<String> historicoArquivos = new java.util.ArrayList<>();
    private int indiceHistorico = -1;
    private int proximaOrdem = 0;
    private JButton btnAnterior;
    private JButton btnProximo;

    public Main() {
        arvore = new Tree();

        setTitle("Estruturas de Dados - Simulador de Árvores");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelControles = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 5));
        painelControles.setBackground(Color.LIGHT_GRAY);

        campoEntrada = new JTextField(10);
        painelControles.add(new JLabel("Valor:"));
        painelControles.add(campoEntrada);

        comboTipoArvore = new JComboBox<>(TreeType.values());
        comboTipoArvore.setSelectedItem(TreeType.BST);
        comboTipoArvore.addActionListener(e -> {
            arvore.setType((TreeType) comboTipoArvore.getSelectedItem());
            atualizarUI();
        });
        painelControles.add(new JLabel("Tipo:"));
        painelControles.add(comboTipoArvore);

        JButton btnInserir = new JButton("Inserir");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnSalvarRelatorio = new JButton("Salvar Relatório");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnResetar = new JButton("Resetar");
        JButton btnInverter = new JButton("Inverter");
        JButton btnCaminhos = new JButton("Caminhos");
        JButton btnExemplos = new JButton("Exemplos");
        JButton btnPercursos = new JButton("Percursos");
        JButton btnAnalise = new JButton("Análise");
        JButton btnSair = new JButton("Sair");

        btnAnterior = new JButton("< Anterior");
        btnProximo = new JButton("Próximo >");
        btnAnterior.setEnabled(false);
        btnProximo.setEnabled(false);

        lblContador = new JLabel("Nós: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));
        lblAltura = new JLabel("Altura: -1");
        lblAltura.setFont(new Font("Arial", Font.BOLD, 12));

        btnTipoArvore = new JButton("Info");
        btnTipoArvore.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, arvore.getTiposArvore(), "Propriedades da Árvore", JOptionPane.INFORMATION_MESSAGE);
        });

        painelControles.add(btnInserir);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnSalvar);
        painelControles.add(btnSalvarRelatorio);
        painelControles.add(btnCarregar);
        painelControles.add(btnResetar);
        painelControles.add(btnInverter);
        painelControles.add(btnCaminhos);
        painelControles.add(btnExemplos);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(lblContador);
        painelControles.add(lblAltura);
        painelControles.add(btnTipoArvore);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnPercursos);
        painelControles.add(btnAnalise);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnAnterior);
        painelControles.add(btnProximo);
        painelControles.add(btnSair);

        painelArvore = new PainelDesenho(arvore);
        painelArvore.setBackground(Color.WHITE);

        add(painelControles, BorderLayout.NORTH);
        add(painelArvore, BorderLayout.CENTER);

        btnInserir.addActionListener(e -> inserirNumero());
        campoEntrada.addActionListener(e -> inserirNumero());
        btnSalvar.addActionListener(e -> GerenciadorArvore.iniciarDialogoSalvar(this, arvore, proximaOrdem++));
        btnSalvarRelatorio.addActionListener(e -> GerenciadorArvore.salvarRelatorioPassos(this, arvore));
        btnCarregar.addActionListener(e -> carregarArvore());
        btnResetar.addActionListener(e -> resetarArvore());
        btnInverter.addActionListener(e -> inverterArvore());
        btnCaminhos.addActionListener(e -> mostrarCaminhos());
        btnSair.addActionListener(e -> System.exit(0));
        btnAnterior.addActionListener(e -> navegarHistorico(-1));
        btnProximo.addActionListener(e -> navegarHistorico(1));
        btnPercursos.addActionListener(e -> mostrarPercursos());
        btnAnalise.addActionListener(e -> mostrarAnaliseCompleta());
        btnExemplos.addActionListener(e -> mostrarExemplos());
    }

    private void inserirNumero() {
        try {
            long valor = Long.parseLong(campoEntrada.getText());
            Tree.RelatorioRB relatorio = arvore.inserirComLogica(valor);
            campoEntrada.setText("");
            campoEntrada.requestFocus();
            atualizarUI();
            autoSave();
            if (arvore.getType() != TreeType.BST && relatorio.teveBalanceamento) {
                exibirPopupRelatorio(relatorio);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoEntrada.setText("");
        }
    }

    private void exibirPopupRelatorio(Tree.RelatorioRB relatorio) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ação executada na árvore ").append(arvore.getType()).append(":\n\n");
        for (String passo : relatorio.passos) {
            sb.append(" • ").append(passo).append("\n");
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Relatório de Operação", JOptionPane.INFORMATION_MESSAGE);
    }

    private void carregarArvore() {
        Tree novaArvore = GerenciadorArvore.iniciarDialogoCarregar(this);
        if (novaArvore != null) {
            arvore = novaArvore;
            comboTipoArvore.setSelectedItem(arvore.getType());
            painelArvore.setArvore(arvore);
            atualizarUI();
            autoSave();
        }
    }

    private void resetarArvore() {
        if (JOptionPane.showConfirmDialog(this, "Resetar árvore?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            arvore.resetar();
            atualizarUI();
            autoSave();
        }
    }

    private void inverterArvore() {
        arvore.inverter();
        atualizarUI();
    }

    private void mostrarCaminhos() {
        java.util.List<String> caminhos = arvore.getCaminhos();
        StringBuilder sb = new StringBuilder("Caminhos:\n");
        for (String c : caminhos) sb.append(c).append("\n");
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())), "Caminhos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPercursos() {
        String[] opcoes = {"NLR (Pré-Ordem)", "LNR (Em Ordem)", "LRN (Pós-Ordem)"};
        String escolha = (String) JOptionPane.showInputDialog(this, "Percurso:", "Selecionar", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha != null) {
            java.util.List<Long> res = arvore.buscarPorPercurso(escolha.substring(0, 3));
            JOptionPane.showMessageDialog(this, res.toString(), "Resultado " + escolha, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarExemplos() {
        String[] opcoes = {"BST: Degenerada", "AVL: Rotação Simples", "AVL: Rotação Dupla", "RB: Caso 1 (Recoloração)", "RB: Caso 2/3 (Rotação)"};
        String escolha = (String) JOptionPane.showInputDialog(this, "Exemplos:", "Selecionar", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);
        if (escolha != null) {
            arvore.resetar();
            long[] valores;
            if (escolha.startsWith("BST")) {
                arvore.setType(TreeType.BST);
                valores = new long[]{10, 20, 30, 40, 50};
            } else if (escolha.contains("Simples")) {
                arvore.setType(TreeType.AVL);
                valores = new long[]{30, 20, 10};
            } else if (escolha.contains("Dupla")) {
                arvore.setType(TreeType.AVL);
                valores = new long[]{30, 10, 20};
            } else if (escolha.contains("Caso 1")) {
                arvore.setType(TreeType.RED_BLACK);
                valores = new long[]{10, 20, 30, 40};
            } else {
                arvore.setType(TreeType.RED_BLACK);
                valores = new long[]{30, 20, 10};
            }
            comboTipoArvore.setSelectedItem(arvore.getType());
            for (long v : valores) arvore.inserirComLogica(v);
            atualizarUI();
        }
    }

    private void atualizarUI() {
        lblContador.setText("Nós: " + arvore.getCount());
        lblAltura.setText("Altura: " + arvore.getAltura());
        painelArvore.repaint();
        painelArvore.ajustarParaCaberNaTela();
    }

    private void autoSave() {
        String filename = "src/salvar_arvore/history/arvore_step_" + proximaOrdem + ".json";
        if (GerenciadorArvore.salvarArvoreJson(arvore, filename, "AutoSave_" + proximaOrdem, proximaOrdem)) {
            while (historicoArquivos.size() > indiceHistorico + 1) historicoArquivos.remove(historicoArquivos.size() - 1);
            historicoArquivos.add(filename);
            indiceHistorico++;
            proximaOrdem++;
            atualizarBotoesHistorico();
        }
    }

    private void navegarHistorico(int direcao) {
        int novoIndice = indiceHistorico + direcao;
        if (novoIndice >= 0 && novoIndice < historicoArquivos.size()) {
            Tree nova = GerenciadorArvore.carregarArvoreJson(historicoArquivos.get(novoIndice));
            if (nova != null) {
                arvore = nova;
                indiceHistorico = novoIndice;
                comboTipoArvore.setSelectedItem(arvore.getType());
                painelArvore.setArvore(arvore);
                atualizarUI();
                atualizarBotoesHistorico();
            }
        }
    }

    private void atualizarBotoesHistorico() {
        btnAnterior.setEnabled(indiceHistorico > 0);
        btnProximo.setEnabled(indiceHistorico < historicoArquivos.size() - 1);
    }

    private void mostrarAnaliseCompleta() {
        StringBuilder sb = new StringBuilder("Análise:\n");
        sb.append("Tipo: ").append(arvore.getType()).append("\n");
        sb.append("Altura: ").append(arvore.getAltura()).append("\n");
        sb.append("Nós: ").append(arvore.getCount()).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
