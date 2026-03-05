import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class PainelDesenho extends JPanel {
    private Tree arvore;

    public PainelDesenho(Tree arvore) {
        this.arvore = arvore;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arvore.getRoot() == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int profundidade = calcularProfundidade(arvore.getRoot());
        double larguraRealNecessaria = Math.pow(2, profundidade - 5) * 65;
        double alturaRealNecessaria = profundidade * 80 + 50;
        double escalaX = getWidth() / larguraRealNecessaria;
        double escalaY = getHeight() / alturaRealNecessaria;
        double escalaFinal = Math.min(1.0, Math.min(escalaX, escalaY));


        AffineTransform at = new AffineTransform();

        double transX = (getWidth() - (larguraRealNecessaria * escalaFinal)) / 2;
        at.translate(transX, 20);
        at.scale(escalaFinal, escalaFinal);

        g2.setTransform(at);


        desenharNo(g2, arvore.getRoot(), (int)larguraRealNecessaria / 2, 50, (int)larguraRealNecessaria / 4);
    }

    private void desenharNo(Graphics2D g, No no, int x, int y, int espacoX) {
        int raio = 25;
        int espacoY = 80;

        if (no.esq != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - espacoX, y + espacoY);
            desenharNo(g, no.esq, x - espacoX, y + espacoY, espacoX / 2);
        }

        if (no.dir != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + espacoX, y + espacoY);
            desenharNo(g, no.dir, x + espacoX, y + espacoY, espacoX / 2);
        }

        g.setColor(new Color(135, 206, 250));
        g.fillOval(x - raio, y - raio, 2 * raio, 2 * raio);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
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