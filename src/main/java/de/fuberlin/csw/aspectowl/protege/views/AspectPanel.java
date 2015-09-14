package de.fuberlin.csw.aspectowl.protege.views;

import java.awt.CardLayout;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;


public class AspectPanel extends JPanel {

	private static final long serialVersionUID = 8529411224132944149L;
	
	private JPanel cardPanel;
	private CardLayout cardLayout;
	
	/**
	 * Create the panel.
	 */
	public AspectPanel() {
		setLayout(new MigLayout("", "[grow][][][][22.00]", "[][]"));

		cardPanel = new JPanel();

		add(cardPanel, "cell 0 0,growx");
		cardLayout = new CardLayout(0, 0);
		cardPanel.setLayout(cardLayout);
	

	}
}
