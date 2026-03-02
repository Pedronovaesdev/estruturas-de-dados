import javax.swing.*;
import java.awt.*;

public class PainelDesenho extends JPanel {
    private Tree arvore;

    public PainelDesenho(Tree arvore) {
        this.arvore = arvore;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (arvore.getRoot() != null) {
            desenharNo(g, arvore.getRoot(), getWidth() / 2, 50, getWidth() / 4);
        }
    }

    private void desenharNo(Graphics g, No no, int x, int y, int espacoX) {
        int raio = 20;
        int espacoY = 60;

        if (no.esq != null) {
            g.drawLine(x, y, x - espacoX, y + espacoY);
            desenharNo(g, no.esq, x - espacoX, y + espacoY, espacoX / 2);
        }

        if (no.dir != null) {
            g.drawLine(x, y, x + espacoX, y + espacoY);
            desenharNo(g, no.dir, x + espacoX, y + espacoY, espacoX / 2);
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
}