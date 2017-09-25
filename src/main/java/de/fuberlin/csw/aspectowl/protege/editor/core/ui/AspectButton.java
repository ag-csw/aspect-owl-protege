package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import org.protege.editor.core.ui.list.MListButton;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class AspectButton extends MListButton {

    public static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);

    private static final String ASPECT_STRING = "A";

    private boolean aspectsPresent = false;

    public AspectButton() {
        super("Aspects", ROLL_OVER_COLOR, null);
    }

    public void setActionListener(ActionListener actionListener) {
        super.setActionListener(actionListener);
    }

    @Override
    public void paintButtonContent(Graphics2D g) {

        int stringWidth = g.getFontMetrics().getStringBounds("A", g).getBounds().width;
        int w = getBounds().width;
        int h = getBounds().height;
        g.drawString("A",
                getBounds().x + w / 2 - stringWidth / 2,
                getBounds().y + g.getFontMetrics().getAscent() / 2 + h / 2);


        Ellipse2D.Double eyeEllipse = new Ellipse2D.Double();
        Area ellipseArea1 = new Area(eyeEllipse);
        Area ellipseArea2 = new Area(eyeEllipse);


    }
}
