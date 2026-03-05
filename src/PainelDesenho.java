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
            int altura = calcularAltura(arvore.getRoot());

            int raio = Math.max(15, 25 - altura);
            int divisor = (int) Math.pow(2, Math.min(Math.max(altura, 1), 3));
            int espacoInicial = Math.max(30, getWidth() / divisor);

            desenharNo(g, arvore.getRoot(), getWidth() / 2, 50, espacoInicial, 60, raio);
        }
    }

    private void desenharNo(Graphics g, No no, int x, int y, int espacoX, int espacoY, int raio) {

        if (no.esq != null) {
            g.drawLine(x, y, x - espacoX, y + espacoY);
            desenharNo(g, no.esq, x - espacoX, y + espacoY, Math.max(20, espacoX / 2), espacoY, raio);
        }

        if (no.dir != null) {
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

    private int calcularAltura(No no) {
        if (no == null) {
            return 0;
        }

        return 1 + Math.max(calcularAltura(no.esq), calcularAltura(no.dir));
    }
}