import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private Tree arvore;
    private PainelDesenho painelArvore;
    private JTextField campoEntrada;
    private JLabel lblContador;

    public Main() {
        arvore = new Tree();

        setTitle("Árvore Binária de Busca");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelControles.setBackground(Color.LIGHT_GRAY);

        painelControles.add(new JLabel("Número: "));
        campoEntrada = new JTextField(10);
        painelControles.add(campoEntrada);

        JButton btnInserir = new JButton("Inserir (1)");
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCarregar = new JButton("Carregar");
        JButton btnResetar = new JButton("Resetar");
        JButton btnSair = new JButton("Sair (0)");

        lblContador = new JLabel("Nós: 0");
        lblContador.setFont(new Font("Arial", Font.BOLD, 12));

        painelControles.add(btnInserir);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(btnSalvar);
        painelControles.add(btnCarregar);
        painelControles.add(btnResetar);
        painelControles.add(new JSeparator(JSeparator.VERTICAL));
        painelControles.add(lblContador);
        painelControles.add(Box.createHorizontalGlue());
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

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saindo do programa");
                System.exit(0);
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

    private void atualizarUI() {
        lblContador.setText("Nós: " + arvore.getCount());
        painelArvore.repaint();
        painelArvore.ajustarParaCaberNaTela();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}