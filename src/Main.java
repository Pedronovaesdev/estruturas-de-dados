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
    private JCheckBox chkModoAVL;

    // Variáveis para histórico e auto-save
    private java.util.List<String> historicoArquivos = new java.util.ArrayList<>();
    private int indiceHistorico = -1;
    private int proximaOrdem = 0;
    private JButton btnAnterior;
    private JButton btnProximo;

    public Main() {
        arvore = new Tree();

        setTitle("Árvore Binária de Busca");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelControles = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 5));
        painelControles.setBackground(Color.LIGHT_GRAY);

        campoEntrada = new JTextField(10);
        painelControles.add(campoEntrada);

        JButton btnInserir = new JButton("Inserir");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnSalvarRelatorio = new JButton("Salvar Relatório");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnResetar = new JButton("Resetar");
        JButton btnInverter = new JButton("Inverter");
        JButton btnCaminhos = new JButton("Caminhos");
        JButton btnExemplosAVL = new JButton("Exemplos AVL");
        JButton btnPercursos = new JButton("Percursos");
        JButton btnAnalise = new JButton("Análise");
        JButton btnSair = new JButton("Sair");

        btnAnterior = new JButton("< Anterior");
        btnProximo = new JButton("Próximo >");
        btnAnterior.setEnabled(false);
        btnProximo.setEnabled(false);

        chkModoAVL = new JCheckBox("Modo AVL", true);
        chkModoAVL.setBackground(Color.LIGHT_GRAY);
        chkModoAVL.setFont(new Font("Arial", Font.BOLD, 12));

        lblContador = new JLabel("Nós: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));

        lblAltura = new JLabel("Altura: -1");
        lblAltura.setFont(new Font("Arial", Font.BOLD, 12));

        btnTipoArvore = new JButton("Tipo: Vazia");
        btnTipoArvore.setFont(new Font("Arial", Font.BOLD, 12));
        btnTipoArvore.setFocusable(false);
        btnTipoArvore.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, arvore.getTiposArvore(), "Tipo da Árvore", JOptionPane.INFORMATION_MESSAGE);
        });

        painelControles.add(chkModoAVL);
        painelControles.add(btnInserir);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnSalvar);
        painelControles.add(btnSalvarRelatorio);
        painelControles.add(btnCarregar);
        painelControles.add(btnResetar);
        painelControles.add(btnInverter);
        painelControles.add(btnCaminhos);
        painelControles.add(btnExemplosAVL);
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

        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inserirNumero();
            }
        });

        campoEntrada.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inserirNumero();
            }
        });

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarArvore();
            }
        });

        btnCarregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                carregarArvore();
            }
        });

        btnResetar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetarArvore();
            }
        });

        btnInverter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inverterArvore();
            }
        });

        btnCaminhos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarCaminhos();
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saindo do programa");
                System.exit(0);
            }
        });

        btnAnterior.addActionListener(e -> navegarHistorico(-1));
        btnProximo.addActionListener(e -> navegarHistorico(1));

        btnPercursos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] opcoes = {"NLR (Pré-Ordem)", "LNR (Em Ordem)", "LRN (Pós-Ordem)"};
                String escolha = (String) JOptionPane.showInputDialog(Main.this, 
                    "Escolha o tipo de percurso:", 
                    "Percursos", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    opcoes, 
                    opcoes[0]);

                if (escolha != null) {
                    String ordem = escolha.substring(0, 3); // Extrai NLR, LNR ou LRN

                    // Trata o caso de árvore vazia antes de realizar o percurso
                    if (arvore == null || arvore.getRoot() == null) {
                        JOptionPane.showMessageDialog(
                            Main.this,
                            "Árvore vazia. Adicione elementos antes de realizar percursos.",
                            "Árvore vazia",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    java.util.List<Long> resultado = arvore.buscarPorPercurso(ordem);
                    StringBuilder sb = new StringBuilder("Resultado do percurso " + ordem + ":\n\n");
                    for (Long valor : resultado) {
                        sb.append(valor).append("\n");
                    }

                    JTextArea textArea = new JTextArea(sb.toString());
                    textArea.setEditable(false);
                    textArea.setOpaque(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(300, 200));

                    JButton btnCopiar = new JButton("Copiar");
                    btnCopiar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            StringSelection selecao = new StringSelection(textArea.getText());
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selecao, null);
                            JOptionPane.showMessageDialog(Main.this, "Resultado copiado para a área de transferência.", "Copiado", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });

                    JPanel painelResultado = new JPanel(new BorderLayout(0, 8));
                    painelResultado.add(scrollPane, BorderLayout.CENTER);

                    JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    painelBotao.add(btnCopiar);
                    painelResultado.add(painelBotao, BorderLayout.SOUTH);

                    JOptionPane.showMessageDialog(Main.this, painelResultado, "Resultado do Percurso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnAnalise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAnaliseCompleta();
            }
        });

        btnExemplosAVL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] opcoes = {
                    "Rotação Simples à Esquerda (inserir 10, 20, 30)",
                    "Rotação Simples à Direita (inserir 30, 20, 10)",
                    "Rotação Dupla à Esquerda (inserir 10, 30, 20)",
                    "Rotação Dupla à Direita (inserir 30, 10, 20)"
                };
                String escolha = (String) JOptionPane.showInputDialog(Main.this,
                    "Escolha o exemplo de rotação AVL para carregar automaticamente:",
                    "Exemplos AVL",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]);

                if (escolha != null) {
                    arvore.resetar();
                    chkModoAVL.setSelected(true);
                    long[] valores;
                    if (escolha.contains("Simples à Esquerda")) {
                        valores = new long[]{10, 20, 30};
                    } else if (escolha.contains("Simples à Direita")) {
                        valores = new long[]{30, 20, 10};
                    } else if (escolha.contains("Dupla à Esquerda")) {
                        valores = new long[]{10, 30, 20};
                    } else {
                        valores = new long[]{30, 10, 20};
                    }

                    Tree.RelatorioAVL ultimoRelatorio = null;
                    for (long v : valores) {
                        ultimoRelatorio = arvore.inserirAVL(v);
                    }
                    atualizarUI();

                    if (ultimoRelatorio != null && ultimoRelatorio.teveRotacao) {
                        exibirPopupRotacao(ultimoRelatorio);
                    }
                }
            }
        });

        btnSalvarRelatorio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarRelatorioPassos();
            }
        });
    }

    private void inserirNumero() {
        try {
            long valor = Long.parseLong(campoEntrada.getText());
            if (chkModoAVL.isSelected()) {
                Tree.RelatorioAVL relatorio = arvore.inserirAVL(valor);
                campoEntrada.setText("");
                campoEntrada.requestFocus();
                atualizarUI();
                autoSave();
                if (relatorio.teveRotacao) {
                    exibirPopupRotacao(relatorio);
                }
            } else {
                arvore.inserir(valor);
                campoEntrada.setText("");
                campoEntrada.requestFocus();
                atualizarUI();
                autoSave();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um número válido!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            campoEntrada.setText("");
        }
    }

    private void exibirPopupRotacao(Tree.RelatorioAVL relatorio) {
        StringBuilder sb = new StringBuilder();
        sb.append("Tipo de Rotação Aplicada:\n").append(relatorio.tipoRotacao).append("\n\n");
        sb.append("Nó Pivô do Desbalanceamento:\n").append(relatorio.pivo).append("\n\n");
        sb.append("Passo a Passo da Inserção e Balanceamento:\n");
        for (String passo : relatorio.passos) {
            sb.append(" • ").append(passo).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 250));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Relatório de Execução AVL"));

        JButton btnSalvarTxt = new JButton("Salvar em TXT");
        btnSalvarTxt.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Salvar Rotação AVL");
            fc.setSelectedFile(new java.io.File("rotacao_avl_pivo_" + relatorio.pivo + ".txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (java.io.FileWriter fw = new java.io.FileWriter(fc.getSelectedFile())) {
                    fw.write(sb.toString());
                    fw.flush();
                    JOptionPane.showMessageDialog(this, "Relatório da rotação salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.add(btnSalvarTxt);

        JPanel painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.add(scrollPane, BorderLayout.CENTER);
        painelConteudo.add(painelBotoes, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, painelConteudo, "Balanceamento AVL Executado", JOptionPane.INFORMATION_MESSAGE);
    }

    private void salvarArvore() {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia! Não há nada para salvar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opcoes = {"Normal (.txt)", "JSON (.json)"};
        int escolha = JOptionPane.showOptionDialog(this, 
            "Escolha o formato de salvamento:", 
            "Salvar Árvore", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            opcoes, 
            opcoes[0]);

        if (escolha == -1) return;

        String caminho = GerenciadorArvore.abrirDialogoSalvar(this);
        if (caminho != null) {
            boolean sucesso;
            if (escolha == 0) { // Normal
                if (!caminho.toLowerCase().endsWith(".txt")) caminho += ".txt";
                sucesso = GerenciadorArvore.salvarArvore(arvore, caminho);
            } else { // JSON
                if (!caminho.toLowerCase().endsWith(".json")) caminho += ".json";
                sucesso = GerenciadorArvore.salvarArvoreJson(arvore, caminho, "ArvoreManual", proximaOrdem++);
            }

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Árvore salva com sucesso em:\n" + caminho, "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void salvarRelatorioPassos() {
        if (arvore.historicoPassos == null || arvore.historicoPassos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O relatório de passos está vazio! Nenhuma ação foi registrada ainda.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar Relatório Passo a Passo");
        fileChooser.setSelectedFile(new java.io.File("relatorio_passos_arvore.txt"));

        int resultado = fileChooser.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            try (java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile())) {
                writer.write("RELATÓRIO PASSO A PASSO DA CONSTRUÇÃO DA ÁRVORE\n");
                writer.write("===============================================\n\n");
                for (String linha : arvore.historicoPassos) {
                    writer.write(linha + "\n");
                }
                writer.flush();
                JOptionPane.showMessageDialog(this, "Relatório de passos salvo com sucesso!", "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar relatório: " + e.getMessage(), "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarArvore() {
        String caminho = GerenciadorArvore.abrirDialogoCarregar(this);
        if (caminho != null) {
            Tree novaArvore = GerenciadorArvore.carregarArvore(caminho);
            if (novaArvore != null) {
                arvore = novaArvore;
                painelArvore.setArvore(arvore);
                painelArvore.ajustarParaCaberNaTela();
                atualizarUI();
                autoSave();
                JOptionPane.showMessageDialog(this,
                        "Árvore carregada com sucesso!\nNós carregados: " + arvore.getCount(), "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void resetarArvore() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Deseja realmente resetar a árvore?\nIsso limpará todos os nós!",
                "Confirmar Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            arvore.resetar();
            painelArvore.ajustarParaCaberNaTela();
            atualizarUI();
            autoSave();
            JOptionPane.showMessageDialog(this, "Árvore resetada com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void inverterArvore() {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia!", "Inverter Árvore", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        arvore.inverter();
        painelArvore.ajustarParaCaberNaTela();
        atualizarUI();
    }

    private void mostrarCaminhos() {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia!", "Caminhos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        java.util.List<String> caminhos = arvore.getCaminhos();
        StringBuilder sb = new StringBuilder("Caminhos da Raiz até as Folhas:\n\n");
        for (String c : caminhos) {
            sb.append(c).append("\n");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Caminhos da Árvore", JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarUI() {
        lblContador.setText("Nós: " + arvore.getCount());
        lblAltura.setText("Altura: " + arvore.getAltura());
        btnTipoArvore.setText("Tipo: " + arvore.getTiposArvore());
        painelArvore.repaint();
        painelArvore.ajustarParaCaberNaTela();
    }

    private void mostrarAnaliseCompleta() {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia!", "Análise Completa", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("===== ANÁLISE COMPLETA DA ÁRVORE =====\n\n");

        // Informações gerais da árvore
        sb.append("INFORMAÇÕES GERAIS DA ÁRVORE:\n");
        sb.append("   • Profundidade da Árvore: ").append(arvore.getProfundidadeArvore()).append("\n");
        sb.append("   • Altura da Árvore: ").append(arvore.getAltura()).append("\n");
        sb.append("   • Nível da Árvore: ").append(arvore.getNivelArvore()).append("\n");
        sb.append("   • Total de Nós: ").append(arvore.getCount()).append("\n");

        // Criar a interface com JTextArea
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Análise Completa da Árvore", JOptionPane.INFORMATION_MESSAGE);
    }

    private void autoSave() {
        String filename = "src/salvar_arvore/history/arvore_step_" + proximaOrdem + ".json";
        if (GerenciadorArvore.salvarArvoreJson(arvore, filename, "AutoSave_" + proximaOrdem, proximaOrdem)) {
            // Se salvamos um novo estado, invalidamos o histórico à frente
            while (historicoArquivos.size() > indiceHistorico + 1) {
                historicoArquivos.remove(historicoArquivos.size() - 1);
            }
            historicoArquivos.add(filename);
            indiceHistorico++;
            proximaOrdem++;
            atualizarBotoesHistorico();
        }
    }

    private void navegarHistorico(int direcao) {
        int novoIndice = indiceHistorico + direcao;
        if (novoIndice >= 0 && novoIndice < historicoArquivos.size()) {
            String caminho = historicoArquivos.get(novoIndice);
            Tree novaArvore = GerenciadorArvore.carregarArvoreJson(caminho);
            if (novaArvore != null) {
                arvore = novaArvore;
                indiceHistorico = novoIndice;
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

    private Tree cloneArvore(Tree arvore) {
        Tree novaArvore = new Tree();
        if (arvore.getRoot() != null) {
            novaArvore.setRoot(cloneNo(arvore.getRoot()));
        }
        return novaArvore;
    }

    private No cloneNo(No no) {
        if (no == null) return null;
        No novo = new No();
        novo.item = no.item;
        novo.esq = cloneNo(no.esq);
        novo.dir = cloneNo(no.dir);
        return novo;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}