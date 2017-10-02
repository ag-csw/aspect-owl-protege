package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;

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
    public Color getBackground() {
        if (aspectsPresent) {
            return Color.ORANGE;
        }
        else {
            return super.getBackground();
        }
    }
    @Override
    public void paintButtonContent(Graphics2D g) {

        int w = getBounds().width;
        int h = getBounds().height;
        int x = getBounds().x;
        int y = getBounds().y;

//        Font font = g.getFont().deriveFont(Font.BOLD, OWLRendererPreferences.getInstance().getFontSize());
        Font font = g.getFont().deriveFont(Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics(font);
        final Rectangle stringBounds = fontMetrics.getStringBounds(ASPECT_STRING, g).getBounds();
        int baseline = fontMetrics.getLeading() + fontMetrics.getAscent();

        g.drawString(ASPECT_STRING, x + w / 2 - stringBounds.width / 2, y + (h - stringBounds.height) / 2 + baseline );

        g.setFont(font);
    }

    @Override
    public String getName() {
        if (aspectsPresent) {
            return "View or edit aspects referencing this axiom.";
        }
        return "This axiom is not target of any aspect. Click to add aspects.";
    }

    public void setAspectsPresent(boolean aspectsPresent) {
        this.aspectsPresent = aspectsPresent;
    }

}
