import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class PainelDesenho extends JPanel {
    private Tree arvore;

    private double zoomAtual = 1.0;
    private double zoomAlvo = 1.0;
    private double xOffsetAtual = 0;
    private double yOffsetAtual = 0;
    private double xOffsetAlvo = 0;
    private double yOffsetAlvo = 0;

    private Point startPoint;
    private Timer animacaoTimer;

    public PainelDesenho(Tree arvore) {
        this.arvore = arvore;
        configurarEventosMouse();
        configurarMotorAnimacao();
    }

    public void setArvore(Tree arvore) {
        this.arvore = arvore;
        repaint();
    }

    private void configurarEventosMouse() {
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoomAnterior = zoomAtual;

                if (e.getWheelRotation() < 0) {
                    zoomAtual *= 1.1;
                } else {
                    zoomAtual /= 1.1;
                }

                zoomAtual = Math.max(0.05, Math.min(zoomAtual, 10.0));
                zoomAlvo = zoomAtual;

                double escalaMudanca = zoomAtual / zoomAnterior;
                xOffsetAtual = e.getX() - (e.getX() - xOffsetAtual) * escalaMudanca;
                yOffsetAtual = e.getY() - (e.getY() - yOffsetAtual) * escalaMudanca;

                xOffsetAlvo = xOffsetAtual;
                yOffsetAlvo = yOffsetAtual;

                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
        animacaoTimer = new Timer(16, e -> {
            boolean precisaRepaint = false;
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

    public void ajustarParaCaberNaTela() {
        if (arvore.getRoot() == null || getWidth() == 0)
            return;

        int profundidade = calcularProfundidade(arvore.getRoot());
        double larguraRealNecessaria = Math.max(800, Math.pow(2, Math.max(0, profundidade - 5)) * 65);
        double alturaRealNecessaria = profundidade * 80 + 100;

        double escalaX = getWidth() / larguraRealNecessaria;
        double escalaY = getHeight() / alturaRealNecessaria;

        zoomAlvo = Math.min(1.0, Math.min(escalaX, escalaY));
        xOffsetAlvo = (getWidth() - (larguraRealNecessaria * zoomAlvo)) / 2.0;
        yOffsetAlvo = 20.0 * zoomAlvo;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (arvore == null || arvore.getRoot() == null)
            return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = new AffineTransform();
        at.translate(xOffsetAtual, yOffsetAtual);
        at.scale(zoomAtual, zoomAtual);
        g2.setTransform(at);
        int profundidade = calcularProfundidade(arvore.getRoot());

        if (arvore.getRoot() != null) {

            int raio = Math.max(15, 25 - profundidade);
            int divisor = (int) Math.pow(2, Math.min(Math.max(profundidade, 1), 3));
            int espacoInicial = Math.max(30, getWidth() / divisor);

            desenharNo(g, arvore.getRoot(), getWidth() / 2, 50, espacoInicial, 60, raio, 0);
        }
    }

    private void desenharNo(Graphics g, No no, int x, int y, int espacoX, int espacoY, int raio, int nivel) {

        if (no.esq != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - espacoX, y + espacoY);
            desenharNo(g, no.esq, x - espacoX, y + espacoY, Math.max(20, espacoX / 2), espacoY, raio, nivel + 1);
        }

        if (no.dir != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + espacoX, y + espacoY);
            desenharNo(g, no.dir, x + espacoX, y + espacoY, Math.max(20, espacoX / 2), espacoY, raio, nivel + 1);
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

        Font fonteOriginal = g.getFont();
        Font fontePequena = fonteOriginal.deriveFont(10f);
        g.setFont(fontePequena);
        FontMetrics fmPq = g.getFontMetrics();

        // --- Altura do nó (acima, à direita) ---
        int alturaDoNo = calcularProfundidade(no) - 1;
        String textoAltura = "h:" + alturaDoNo;
        int alturaX = x + (raio / 2);
        int alturaY = y - raio - 2;
        int wA = fmPq.stringWidth(textoAltura);
        int hA = fmPq.getAscent();
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(alturaX - 1, alturaY - hA + 1, wA + 2, hA + 2);
        g.setColor(new Color(220, 20, 60)); // Crimson
        g.drawString(textoAltura, alturaX, alturaY);

        // --- Nível / Profundidade do nó (abaixo, à esquerda) ---
        String textoNivel = "n:" + nivel;
        int nivelX = x - raio - fmPq.stringWidth(textoNivel) - 1;
        int nivelY = y + raio + fmPq.getAscent();
        int wN = fmPq.stringWidth(textoNivel);
        int hN = fmPq.getAscent();
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRect(nivelX - 1, nivelY - hN + 1, wN + 2, hN + 2);
        g.setColor(new Color(30, 100, 200)); // Azul
        g.drawString(textoNivel, nivelX, nivelY);

        g.setFont(fonteOriginal);
        g.setColor(Color.BLACK);
    }

    private int calcularProfundidade(No no) {
        if (no == null)
            return 0;
        return 1 + Math.max(calcularProfundidade(no.esq), calcularProfundidade(no.dir));

    }
}