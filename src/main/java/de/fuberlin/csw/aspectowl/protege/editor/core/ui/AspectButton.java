package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.awt.*;

public class AspectButton extends MListButton {

    private static final Color ROLL_OVER_COLOR = new Color(0, 0, 0);

    private static final String ASPECT_STRING = "A";

    private OWLAxiom axiom;
    private OWLOntology ontology;

    private OWLOntologyAspectManager aspectManager = OWLOntologyAspectManager.instance();

    public AspectButton(OWLAxiom axiom, OWLOntology ontology) {
        super("Aspects", ROLL_OVER_COLOR, null);
        this.axiom = axiom;
        this.ontology = ontology;
    }

    @Override
    public Color getBackground() {
        if (aspectManager.hasAssertedAspects(ontology, axiom)) {
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

        Font font = g.getFont().deriveFont(Font.BOLD, OWLRendererPreferences.getInstance().getFontSize());
//        Font font = g.getFont().deriveFont(Font.BOLD, 12);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics(font);
        final Rectangle stringBounds = fontMetrics.getStringBounds(ASPECT_STRING, g).getBounds();
        int baseline = fontMetrics.getLeading() + fontMetrics.getAscent();

        // thought about having different font colors for asserted and inferred axioms, but
        // Protege's built-in buttons do not have that, so I won't add it
//        g.setColor(ontology == null ? Color.RED : g.getColor());

        g.drawString(ASPECT_STRING, x + w / 2 - stringBounds.width / 2, y + (h - stringBounds.height) / 2 + baseline );
    }

    @Override
    public String getName() {
        if (aspectManager.hasAssertedAspects(ontology, axiom)) {
            return "View or edit aspects referencing this axiom.";
        }
        return "This axiom is not target of any aspect. Click to add aspects.";
    }
}
