package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import org.protege.editor.owl.ui.renderer.OWLClassIcon;

import java.awt.*;

public class OWLAspectIcon extends OWLClassIcon {

    public static final Color COLOR = new Color(153, 204, 0);

    public static final BasicStroke HOLLOW_STROKE = new BasicStroke(2);

    private Type type;

    public OWLAspectIcon() {
        this(Type.PRIMITIVE, FillType.FILLED);
    }

    public OWLAspectIcon(Type type) {
        this(type, FillType.FILLED);
    }

    public OWLAspectIcon(Type type, FillType fillType) {
        super(type, fillType);
        this.type = type;
    }


    @Override
    public Color getEntityColor() {
        return COLOR;
    }

    /**
     * Draw the icon at the specified location.  Icon implementations
     * may use the Component argument to get properties useful for
     * painting, e.g. the foreground or background color.
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            final int clsSize = getBaseSize() - 2;

            int dW = getIconWidth() / 2 - clsSize / 2;
            int xC = x + dW;
            int dH = getIconHeight() / 2 - clsSize / 2;
            int yC = y + dH;
            if(getFillType() == FillType.FILLED) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(xC,
                        yC,
                        clsSize,
                        clsSize);
                g2.setColor(COLOR);
                g2.fillOval(xC + 1,
                        yC + 1,
                        clsSize - 2,
                        clsSize - 2);
            }
            else {
                g2.setStroke(HOLLOW_STROKE);
                g2.setColor(getEntityColor());
                g2.drawOval(xC + 1,
                        yC + 1,
                        clsSize - 2,
                        clsSize - 2);
            }


            if (type.equals(Type.DEFINED)) {
                if (getFillType() == FillType.FILLED) {
                    g2.setColor(Color.WHITE);
                }
                else {
                    g2.setColor(getEntityColor());
                }
                int centreSize = (int) Math.sqrt(clsSize / 2 * clsSize / 2);
                int boxWidth = (centreSize / 2) * 2;
                int boxHeight = (centreSize / 5) * 5;
                int boxX = (getIconWidth() - boxWidth) / 2;
                int boxY = (getIconHeight() - boxHeight) / 2;
                g2.fillRect(x + boxX, y + boxY, boxWidth, boxHeight);
                int stripeHeight = boxHeight / 5;
                g2.setColor(COLOR);
                g2.fillRect(x + boxX, y + boxY + stripeHeight, boxWidth, stripeHeight);
                g2.fillRect(x + boxX, y + boxY + stripeHeight * 3, boxWidth, stripeHeight);
            }

        } finally {
            g2.dispose();
        }
    }
}
