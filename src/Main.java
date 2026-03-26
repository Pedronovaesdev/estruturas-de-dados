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

    public Main() {
        arvore = new Tree();

        setTitle("Árvore Binária de Busca");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelControles.setBackground(Color.LIGHT_GRAY);

        campoEntrada = new JTextField(10);
        painelControles.add(campoEntrada);

        JButton btnInserir = new JButton("Inserir");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnResetar = new JButton("Resetar");
        JButton btnCaminhos = new JButton("Caminhos");
        JButton btnPercursos = new JButton("Percursos");
        JButton btnSair = new JButton("Sair");

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

        painelControles.add(btnInserir);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnSalvar);
        painelControles.add(btnCarregar);
        painelControles.add(btnResetar);
        painelControles.add(btnCaminhos);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(lblContador);
        painelControles.add(lblAltura);
        painelControles.add(btnTipoArvore);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnPercursos);
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
    }

    private void inserirNumero() {
        try {
            long valor = Long.parseLong(campoEntrada.getText());
            arvore.inserir(valor);
            campoEntrada.setText("");
            campoEntrada.requestFocus();
            atualizarUI();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um número válido!", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            campoEntrada.setText("");
        }
    }

    private void salvarArvore() {
        if (arvore.getRoot() == null) {
            JOptionPane.showMessageDialog(this, "A árvore está vazia! Não há nada para salvar.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String caminho = GerenciadorArvore.abrirDialogoSalvar(this);
        if (caminho != null) {
            if (GerenciadorArvore.salvarArvore(arvore, caminho)) {
                JOptionPane.showMessageDialog(this, "Árvore salva com sucesso em:\n" + caminho, "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Árvore resetada com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}