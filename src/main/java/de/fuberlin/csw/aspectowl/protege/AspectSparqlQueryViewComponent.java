/**
 * 
 */
package de.fuberlin.csw.aspectowl.protege;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.list.OWLAxiomList;
import org.protege.editor.owl.ui.prefix.PrefixUtilities;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import de.fuberlin.csw.aspectowl.inference.AspectSparqlQueryExecutor;

/**
 * @author ralph
 *
 */
public class AspectSparqlQueryViewComponent extends AbstractActiveOntologyViewComponent {
	
	private static final long serialVersionUID = 5901526849643522633L;

	private static final Logger log = Logger.getLogger(AspectSparqlQueryExecutor.class);

	private UIHelper uiHelper;
//	private NamespaceUtil nsUtil;
	private AspectSparqlQueryExecutor sparqlExecutor;
	private JTextPane queryPane;

	private JButton executeButton;
	
	private OWLAnnotation selectedAspectAnnotation;
	
	/**
	 * 
	 */
	public AspectSparqlQueryViewComponent() {
	}

	/**
	 * @see org.protege.editor.owl.ui.view.AbstractOWLViewComponent#initialiseOWLView()
	 */
	@Override
	protected void initialiseOntologyView() throws Exception {
		sparqlExecutor = new AspectSparqlQueryExecutor(getOWLWorkspace());
//		nsUtil = new NamespaceUtil();
		uiHelper = new UIHelper(getOWLEditorKit());
		setLayout(new BorderLayout());
		add(getQueryPane(), BorderLayout.CENTER);
		add(getBottomPanel(), BorderLayout.SOUTH);
	}
	
	private JTextPane getQueryPane() {
		queryPane = new JTextPane();
		queryPane.setText(getSampleQuery());
		return queryPane;
	}
	
	/**
	 * 
	 * @return
	 */
	private JPanel getBottomPanel() {
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		
		JButton entitySelectionButton = new JButton("Select aspect");
		entitySelectionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				OWLAnnotationEditor annotationEditor = new OWLAnnotationEditor(getOWLEditorKit());
				int ret = uiHelper.showValidatingDialog("Select aspect to apply", annotationEditor.getEditorComponent(), null);

	            if (ret == JOptionPane.OK_OPTION) {
	                selectedAspectAnnotation = annotationEditor.getEditedObject();
	            }
                executeButton.setEnabled(selectedAspectAnnotation != null);
			}
		});
		panel.add(entitySelectionButton);
		
		executeButton = new JButton("Execute");
		executeButton.setEnabled(false);
		executeButton.addActionListener(new ActionListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String query = queryPane.getText();			
					OWLOntology module = sparqlExecutor.getOntologyModule(query);
					Set<OWLAxiom> axiomsInModule = module.getAxioms();
					
					// show results
					
					OWLAxiomList axiomList = new OWLAxiomList(getOWLEditorKit());
					axiomList.setListData(axiomsInModule.toArray());
					axiomList.setFixedCellHeight(24);

					JPanel panel = new JPanel(new BorderLayout());
//			        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//			        panel.setPreferredSize(new Dimension(400, 300));
			        JScrollPane scrollPane = new JScrollPane(axiomList);
					panel.add(scrollPane, BorderLayout.CENTER);
					
					panel.add(new JLabel("<html><body style='width:400px'>Do you really want to add the aspect <b>" + selectedAspectAnnotation.getProperty().getIRI().getFragment() + " " + selectedAspectAnnotation.getValue() +  " </b>to these axioms?</body></html>"), BorderLayout.NORTH);
					
					int result = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(getOWLWorkspace()), panel, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						Set<OWLAnnotation> annotation = Collections.singleton(selectedAspectAnnotation);
						OWLOntologyManager om = getOWLModelManager().getOWLOntologyManager();
						OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
						
				        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
						for (OWLAxiom axiom : axiomsInModule) {
							OWLAxiom axiomWithAspect = axiom.getAnnotatedAxiom(annotation);
							changes.add(new AddAxiom(activeOntology, axiomWithAspect));
						}
						om.applyChanges(changes);
					}
					
				}
				catch (Exception ex) {
					ProtegeApplication.getErrorLog().logError(ex);
					JOptionPane.showMessageDialog(getOWLWorkspace(), ex.getMessage() + "\nSee the logs for more information.");
				}
			}
		});
		
		panel.add(executeButton);
		
		return panel;
		
	}
	
	@Override
	protected void updateView(OWLOntology activeOntology) throws Exception {
		log.info("updateView");
	}

	/**
	 * @see org.protege.editor.owl.ui.view.AbstractOWLViewComponent#disposeOWLView()
	 */
	@Override
	protected void disposeOntologyView() {
		sparqlExecutor = null;
//		nsUtil = null;
		uiHelper = null;
	}
	
	/**
	 * Generates a sample query to be filled in the query text box.
	 * In particular, ns prefix mappings for all loaded ontologies (if defined)
	 * are added to the query.
	 * @return
	 */
	private String getSampleQuery() {
		
		StringBuilder buf = new StringBuilder();
		
		HashMap<String, String> namespace2PrefixMap = new HashMap<String, String>();
			
		// fill in default namespace prefixes
		// not necessary, because all these prefix mappings are duplicated in the PrefixManager
//		namespace2PrefixMap.putAll(nsUtil.getNamespace2PrefixMap());
		
		// fill in namespace prefixes declared in active ontology
		PrefixManager prefixManager = PrefixUtilities.getPrefixOWLOntologyFormat(getOWLModelManager());
		namespace2PrefixMap.putAll(prefixManager.getPrefixName2PrefixMap());
		
		fillBufferWithPrefixes(buf, namespace2PrefixMap);
		
		
		buf.append("PREFIX aspect: <http://www.corporate-semantic-web.de/ontologies/aspect_owl#>\n");
		buf.append("PREFIX ex: <http://csw.inf.fu-berlin.de/aood/example#>\n");
				
		buf.append("CONSTRUCT {ex:Bonn ?p ?o}\n");
		buf.append("	WHERE {ex:Bonn ?p ?o}");
		return buf.toString();
	}
	
	private void fillBufferWithPrefixes(StringBuilder buf, Map<String, String> namespace2PrefixMap) {
		for (Entry<String, String> entry: namespace2PrefixMap.entrySet()) {
			String prefix = entry.getKey();
			String ns = entry.getValue();
			buf.append("PREFIX ");
			buf.append(prefix); // prefix already contains :
			buf.append(" <");
			buf.append(ns);
			buf.append(">\n");
		}

	}


}
