package de.fuberlin.csw.aspectowl.protege;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLAnnotationCellRenderer;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLAnnotation;
public class SelectAspectsPanel extends JComponent {

	/**
	 * 
	 */
	private java.util.List<OWLAnnotation> items;
	private static final long serialVersionUID = 1L;
	private MList mList;
	private JScrollPane jScrollPane;
	private DefaultListModel defaultListModel;
	private JButton jButton;
	private OWLEditorKit eKit;

	public SelectAspectsPanel(final OWLEditorKit eKit, java.util.List<OWLAnnotation> items) {
		this.items = items;
		this.eKit = eKit;
		BorderLayout thisLayout = new BorderLayout();
		//this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(thisLayout);

		defaultListModel = new DefaultListModel();

		mList = new MList();
        
		mList.setModel(defaultListModel);
		mList.setBackground(getBackground());
		mList.setEnabled(false);
		mList.setOpaque(true);
		mList.setCellRenderer(new OWLAnnotationCellRenderer(eKit));
		
		jScrollPane = new JScrollPane();
		jScrollPane.setPreferredSize(new java.awt.Dimension(392, 245));
		jScrollPane.setViewportView(mList);

		jButton = new JButton();
		jButton.setText("add");
		
		AddAspectActionListener actionListener = new AddAspectActionListener(eKit, mList, items);
		jButton.addActionListener(actionListener);

		setLayout(new BorderLayout(6, 6));
		//setPreferredSize( new Dimension(100, 300));

		// we need to use the OWLCellRenderer, so create a singleton JList
		final OWLCellRenderer ren = new OWLCellRenderer(eKit);
		ren.setHighlightKeywords(true);


		final JScrollPane scroller = new JScrollPane();

	    add(jButton, BorderLayout.SOUTH); 
		add(scroller, BorderLayout.NORTH);

		scroller.setViewportView(mList);
		
		setVisible(true);
	}

	public void dispose() {
	}

}
