package de.fuberlin.csw.aspectowl.protege.editor.core.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ComponentWithAspectButton extends JComponent {

    private AspectButton aspectButton = new AspectButton();
    private JComponent guest;

    public ComponentWithAspectButton(JComponent target, ActionListener aspectButtonActionListener) {
         setLayout(new BorderLayout());

         aspectButton.setActionListener(aspectButtonActionListener);

         add(target, BorderLayout.CENTER);
         add(new JButton(), BorderLayout.LINE_END);

        Box parent = (Box)target.getParent();
//        parent.getComponent()
    }
}
