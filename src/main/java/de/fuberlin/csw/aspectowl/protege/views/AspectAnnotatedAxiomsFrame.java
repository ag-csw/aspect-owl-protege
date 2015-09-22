/**
 * 
 */
package de.fuberlin.csw.aspectowl.protege.views;

import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrame;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author ralph
 *
 */
public class AspectAnnotatedAxiomsFrame extends AbstractOWLFrame<Set<OWLAxiom>> {

	private AspectAnnotatedAxiomsFrameSection section;
	/**
	 * @param owlEditorKit
	 */
	public AspectAnnotatedAxiomsFrame(OWLEditorKit owlEditorKit) {
        super(owlEditorKit.getModelManager().getOWLOntologyManager());
        addSection(section = new AspectAnnotatedAxiomsFrameSection(owlEditorKit, this));
	}
	
	public void setInferredAspectAnnotatedAxioms(OWLOntology aspectAnnotatedInferredAxioms) {
		section.setAspectAnnotatedInferredAxioms(aspectAnnotatedInferredAxioms);
	}

}
