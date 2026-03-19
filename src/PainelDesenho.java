import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class PainelDesenho extends JPanel {
    private Tree arvore;

    // Variáveis de estado da Câmera (Viewport)
    private double zoomAtual = 1.0;
    private double zoomAlvo = 1.0;
    private double xOffsetAtual = 0;
    private double yOffsetAtual = 0;
    private double xOffsetAlvo = 0;
    private double yOffsetAlvo = 0;

    private Point startPoint;
    private Timer animacaoTimer;
    private boolean usuarioControlando = false;

    public PainelDesenho(Tree arvore) {
        this.arvore = arvore;
        configurarEventosMouse();
        configurarMotorAnimacao();
    }

    /**
     * Atualiza a referência da árvore
     */
    public void setArvore(Tree arvore) {
        this.arvore = arvore;
        repaint();
    }

    private void configurarEventosMouse() {
        // Controle de zoom (scroll de proximidade)
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                usuarioControlando = true; // Interrompe qualquer auto-ajuste
                double zoomAnterior = zoomAtual;

                if (e.getWheelRotation() < 0) {
                    zoomAtual *= 1.1; // Aproximar
                } else {
                    zoomAtual /= 1.1; // Afastar
                }

                // Limita o zoom para evitar inversão ou perda da imagem
                zoomAtual = Math.max(0.05, Math.min(zoomAtual, 10.0));
                zoomAlvo = zoomAtual;

                // Matemática para manter o zoom focado sob o cursor do mouse
                double escalaMudanca = zoomAtual / zoomAnterior;
                xOffsetAtual = e.getX() - (e.getX() - xOffsetAtual) * escalaMudanca;
                yOffsetAtual = e.getY() - (e.getY() - yOffsetAtual) * escalaMudanca;

                xOffsetAlvo = xOffsetAtual;
                yOffsetAlvo = yOffsetAtual;

                repaint();
            }
        });

        // Controle de Pan (arrastar a tela)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                usuarioControlando = true;
                startPoint = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (startPoint != null) {
                    int dx = e.getX() - startPoint.x;
                    int dy = e.getY() - startPoint.y;

                    xOffsetAtual += dx;
                    yOffsetAtual += dy;
                    xOffsetAlvo = xOffsetAtual;
                    yOffsetAlvo = yOffsetAtual;

                    startPoint = e.getPoint();
                    repaint();
                }
            }
        });
    }

    private void configurarMotorAnimacao() {
        // Loop de animação a ~60 FPS para transições suaves
        animacaoTimer = new Timer(16, e -> {
            boolean precisaRepaint = false;

            // Interpolação Linear (LERP) para suavizar a transição do zoom e posição
            if (Math.abs(zoomAtual - zoomAlvo) > 0.001) {
                zoomAtual += (zoomAlvo - zoomAtual) * 0.1;
                precisaRepaint = true;
            }
            if (Math.abs(xOffsetAtual - xOffsetAlvo) > 1.0) {
                xOffsetAtual += (xOffsetAlvo - xOffsetAtual) * 0.1;
                precisaRepaint = true;
            }
            if (Math.abs(yOffsetAtual - yOffsetAlvo) > 1.0) {
                yOffsetAtual += (yOffsetAlvo - yOffsetAtual) * 0.1;
                precisaRepaint = true;
            }

            if (precisaRepaint) {
                repaint();
            }
        });
        animacaoTimer.start();
    }

    // Chame este método sempre que a árvore crescer para recalcular os limites suavemente
    public void ajustarParaCaberNaTela() {
        if (arvore.getRoot() == null || getWidth() == 0) return;

        usuarioControlando = false;

        int profundidade = calcularProfundidade(arvore.getRoot());
        double larguraRealNecessaria = Math.max(800, Math.pow(2, Math.max(0, profundidade - 5)) * 65);
        double alturaRealNecessaria = profundidade * 80 + 100;

        double escalaX = getWidth() / larguraRealNecessaria;
        double escalaY = getHeight() / alturaRealNecessaria;

        // Define os novos alvos. O Timer cuidará da transição suave.
        zoomAlvo = Math.min(1.0, Math.min(escalaX, escalaY));
        xOffsetAlvo = (getWidth() - (larguraRealNecessaria * zoomAlvo)) / 2.0;
        yOffsetAlvo = 20.0 * zoomAlvo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (arvore == null || arvore.getRoot() == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = new AffineTransform();
        at.translate(xOffsetAtual, yOffsetAtual);
        at.scale(zoomAtual, zoomAtual);
        g2.setTransform(at);

        // O desenho sempre assume um espaço lógico fixo, o AffineTransform cuida do resto
        int profundidade = calcularProfundidade(arvore.getRoot());

        if (arvore.getRoot() != null) {

            int raio = Math.max(15, 25 - profundidade);
            int divisor = (int) Math.pow(2, Math.min(Math.max(profundidade, 1), 3));
            int espacoInicial = Math.max(30, getWidth() / divisor);

            desenharNo(g, arvore.getRoot(), getWidth() / 2, 50, espacoInicial, 60, raio);
        }
    }

    private void desenharNo(Graphics g, No no, int x, int y, int espacoX, int espacoY, int raio) {

        if (no.esq != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - espacoX, y + espacoY);
            desenharNo(g, no.esq, x - espacoX, y + espacoY, Math.max(20, espacoX / 2), espacoY, raio);
        }

        if (no.dir != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + espacoX, y + espacoY);
            desenharNo(g, no.dir, x + espacoX, y + espacoY, Math.max(20, espacoX / 2), espacoY, raio);
        }

        g.setColor(new Color(135, 206, 250));
        g.fillOval(x - raio, y - raio, 2 * raio, 2 * raio);
        g.setColor(Color.BLACK);
        g.drawOval(x - raio, y - raio, 2 * raio, 2 * raio);

        String texto = String.valueOf(no.item);
        FontMetrics fm = g.getFontMetrics();
        int textoX = x - (fm.stringWidth(texto) / 2);
        int textoY = y + (fm.getAscent() / 2) - 2;
        g.drawString(texto, textoX, textoY);
    }

    private int calcularProfundidade(No no) {
        if (no == null) return 0;
        return 1 + Math.max(calcularProfundidade(no.esq), calcularProfundidade(no.dir));

    }
}