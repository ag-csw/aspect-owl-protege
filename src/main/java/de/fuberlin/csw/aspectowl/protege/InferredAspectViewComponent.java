package de.fuberlin.csw.aspectowl.protege;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.view.AbstractActiveOntologyViewComponent;
import org.semanticweb.owlapi.change.AxiomChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.change.OWLOntologyChangeRecord;
import org.semanticweb.owlapi.change.RemoveAxiomData;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import de.fuberlin.csw.aspectowl.inference.InferredAspectAnnotationGenerator;
import de.fuberlin.csw.aspectowl.protege.views.AspectAnnotatedAxiomsFrame;


/**
 * This class implements the Aspects view component.
 * 
 * @author ralph
 *
 */
public class InferredAspectViewComponent extends AbstractActiveOntologyViewComponent {

	private static final long serialVersionUID = -1509104956712586572L;

	private static final Logger log = Logger.getLogger(InferredAspectViewComponent.class);
	
//	private static final HashSet<InferredAspectViewComponent> components = new HashSet<InferredAspectViewComponent>();
	
	private InferredAspectAnnotationGenerator inferredAspectAnnotationGenerator;
	
	private OWLFrameList<Set<OWLAxiom>> frameList;
	
	private AspectAnnotatedAxiomsFrame frame;
	
//	private OWLOntology inferredAspectAnnotatedAxioms;
	
	private OWLModelManagerListener listener = new OWLModelManagerListener() {

		@Override
		public void handleChange(OWLModelManagerChangeEvent event) {
			if (isSynchronizing()) {
				if (event.isType(EventType.ONTOLOGY_CLASSIFIED)) {
					try {
						log.info("Hurray! New inferred axioms coming in!");
						frame.setInferredAspectAnnotatedAxioms(inferredAspectAnnotationGenerator.inferAspects());
						updateView(getOWLModelManager().getActiveOntology());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	};
	
	private OWLOntologyChangeListener changeListener = new OWLOntologyChangeListener() {
		
		@Override
		public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
				throws OWLException {
			for (OWLOntologyChange change : changes) {
				if (change instanceof OWLAxiomChange) {
					Set<OWLEntity> signature = change.getSignature();
					OWLAxiom axiom = ((OWLAxiomChange)change).getAxiom();
					if (axiom instanceof OWLAnnotationAssertionAxiom) {
						signature = ((OWLAnnotationAssertionAxiom)axiom).getSubject().getSignature();
					}
					System.out.println("Signature: " + signature);
				}
			}
		}
	};

	public InferredAspectViewComponent() throws Exception {
		
	}

//	private AspectPanel aspectPanel;


	// @Override
	protected void initialiseOntologyView() throws Exception {

		log.info("Aspect View Component initialized");

		setLayout(new BorderLayout());
		
		frame = new AspectAnnotatedAxiomsFrame(getOWLEditorKit());
		frameList = new OWLFrameList<Set<OWLAxiom>>(getOWLEditorKit(), frame);
		add(new JScrollPane(frameList), BorderLayout.CENTER);
		updateHeader();
		
		inferredAspectAnnotationGenerator = new InferredAspectAnnotationGenerator(getOWLWorkspace());
		getOWLModelManager().addListener(listener);
		
		getOWLModelManager().addOntologyChangeListener(changeListener);
		
		updateView(getOWLModelManager().getActiveOntology());
	}

	private void updateHeader() {
        getView().setHeaderText("(ontology classified using " + getOWLModelManager().getOWLReasonerManager().getCurrentReasonerName() + ")");
    }
	
	@Override
	protected void disposeOntologyView() {
		log.info("Aspect View Component disposed");
		
		getOWLModelManager().removeListener(listener);
		getOWLModelManager().removeOntologyChangeListener(changeListener);
		
		frameList.dispose();
		
//		components.remove(this);
//		if (components.isEmpty()) {
//			getOWLModelManager().removeListener(InferredAspectAnnotationGenerator.getInstance(getOWLWorkspace()));
//		}
	}

	@Override
	protected void updateView(OWLOntology activeOntology) throws Exception {
        if (isSynchronizing()) {
            frameList.setRootObject(activeOntology.getAxioms());
            updateHeader();
        }
	}

}
