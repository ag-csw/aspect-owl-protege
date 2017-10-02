package de.fuberlin.csw.aspectowl.aspectj;


/*
 * A simple swing checkbox example with different constructors
 */

        import de.fuberlin.csw.aspectowl.protege.editor.core.ui.ComponentWithAspectButton;

        import javax.swing.*;
        import java.awt.*;

public class TestCheckbox {

    public static void main(String[] args) {
        // Create and set up a frame window
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Simple checkbox demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Define the panel to hold the checkbox
        JPanel panel = new JPanel();

        // Create checkbox with different constructors
//        JCheckBox checkbox1 = new JCheckBox("Apple", true);
        JCheckBox checkbox1 = new ComponentWithAspectButton("Apple", e -> System.out.println(e.getActionCommand()
        ));


        JCheckBox checkbox2 = new JCheckBox("Banana");
        JCheckBox checkbox3 = new JCheckBox("Grape", true);
        JCheckBox checkbox4 = new JCheckBox("Orange");
        JCheckBox checkbox5 = new JCheckBox("Pear", true);

        // Set up the title for the panel
        panel.setBorder(BorderFactory.createTitledBorder("Fruits"));

        // Add the checkbox into the panels
        panel.add(checkbox1);
        panel.add(checkbox2);
        panel.add(checkbox3);
        panel.add(checkbox4);
        panel.add(checkbox5);

        // Add the panel into the frame
        frame.add(panel);

        // Set the window to be visible as the default to be false
        frame.pack();
        frame.setVisible(true);

    }

}