import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {
    private Tree arvore;
    private PainelDesenho painelArvore;
    private JTextField campoEntrada;

    public Main() {
        arvore = new Tree();

        setTitle("Árvore Binária de Busca");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelControles = new JPanel();
        painelControles.setBackground(Color.LIGHT_GRAY);

        painelControles.add(new JLabel("Número: "));
        campoEntrada = new JTextField(10);
        painelControles.add(campoEntrada);

        JButton btnInserir = new JButton("Inserir (1)");
        JButton btnSair = new JButton("Sair (0)");

        painelControles.add(btnInserir);
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

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Saindo do programa...");
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
            painelArvore.repaint();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            campoEntrada.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}