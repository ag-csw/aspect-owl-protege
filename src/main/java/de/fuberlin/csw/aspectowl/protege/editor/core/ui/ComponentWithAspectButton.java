package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ComponentWithAspectButton extends JCheckBox {

    private AspectButton aspectButton;
//    private JComponent guest;

    public ComponentWithAspectButton(OWLAxiom axiom, OWLOntology ontology, String text, ActionListener aspectButtonActionListener, OWLOntologyAspectManager am) {
        super(text);
//         setLayout(new BorderLayout());

         aspectButton = new AspectButton(axiom, ontology, am);
         aspectButton.setActionListener(aspectButtonActionListener);

//         add(target, BorderLayout.CENTER);
//         add(new JButton(), BorderLayout.LINE_END);

//        Box parent = (Box)target.getParent();
//        parent.getComponent()
    }

    @Override
    public int getWidth() {
        return super.getWidth() + 20;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        aspectButton.setSize(height);
        aspectButton.setLocation(0, 0);
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;

        aspectButton.paintButtonContent(g2d);

        g.translate(20, 0);
        super.paint(g);
    }
}
