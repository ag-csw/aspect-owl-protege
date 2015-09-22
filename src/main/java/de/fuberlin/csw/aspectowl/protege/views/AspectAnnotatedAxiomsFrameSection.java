/**
 * 
 */
package de.fuberlin.csw.aspectowl.protege.views;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.AxiomListFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import de.fuberlin.csw.aspectowl.util.AspectOWLUtils;


/**
 * @author ralph
 *
 */
public class AspectAnnotatedAxiomsFrameSection extends AbstractOWLFrameSection<Set<OWLAxiom>, OWLAxiom, OWLAxiom> {

	private static final Logger log = Logger.getLogger(AspectAnnotatedAxiomsFrameSection.class);
	
	private OWLOntology aspectAnnotatedInferredAxioms;
	
	/**
	 * @param editorKit
	 * @param label
	 * @param frame
	 */
	public AspectAnnotatedAxiomsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends Set<OWLAxiom>> frame) {
		super(editorKit, "Axioms with aspects", frame);
	}
	
	public void setAspectAnnotatedInferredAxioms(OWLOntology aspectAnnotatedInferredAxioms) {
		this.aspectAnnotatedInferredAxioms = aspectAnnotatedInferredAxioms;
	}

	/**
	 * @see org.protege.editor.owl.ui.frame.OWLFrameSection#getRowComparator()
	 */
	@Override
    public Comparator<OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom>>() {

            public int compare(OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o1,
                    OWLFrameSectionRow<Set<OWLAxiom>, OWLAxiom, OWLAxiom> o2) {

                int diff = o1.getAxiom().compareTo(o2.getAxiom());
                if(diff != 0) {
                    return diff;
                }
                else if (o1.getOntology() == null  && o2.getOntology() == null) {
                    return 0;
                }
                else if (o1.getOntology() == null) {
                    return -1;
                }
                else if (o2.getOntology() == null) {
                    return +1;
                }
                else {
                    return o1.getOntology().compareTo(o2.getOntology());
                }
            }
        };
	}
	
	/**
	 * @see org.protege.editor.owl.ui.frame.AbstractOWLFrameSection#createAxiom(org.semanticweb.owlapi.model.OWLAxiom)
	 */
	@Override
	protected OWLAxiom createAxiom(OWLAxiom axiom) {
		return axiom;
	}

	/**
	 * @see org.protege.editor.owl.ui.frame.AbstractOWLFrameSection#getObjectEditor()
	 */
	@Override
	public OWLObjectEditor<OWLAxiom> getObjectEditor() {
		return null;
	}

	/**
	 * @see org.protege.editor.owl.ui.frame.AbstractOWLFrameSection#refill(org.semanticweb.owlapi.model.OWLOntology)
	 */
	@Override
	protected void refill(OWLOntology ontology) {
		Set<OWLAnnotationProperty> aspectAnnotationProperties = AspectOWLUtils.getAllAspectAnnotationProperties(ontology);
		
		for (OWLAxiom axiom : ontology.getAxioms()) {
			for (OWLAnnotationProperty aspectAnnotationProperty : aspectAnnotationProperties) {
				if (!axiom.getAnnotations(aspectAnnotationProperty).isEmpty()) {
					addRow(new AxiomListFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), axiom));
				}
			}
		}
	}
	
	@Override
	protected void refillInferred() {
		if (aspectAnnotatedInferredAxioms == null)
			return;
		
        for (OWLAxiom axiom : new TreeSet<OWLAxiom>(aspectAnnotatedInferredAxioms.getAxioms())) {
            boolean add = true;
            for (OWLOntology actOnt : getOWLModelManager().getActiveOntologies()) {
                if (actOnt.containsAxiom(axiom)) {
                    add = false;
                    break;
                }
            }
            if (add) {
            	log.info("Adding inferred axiom: " + axiom);
            	addInferredRowIfNontrivial(new AxiomListFrameSectionRow(getOWLEditorKit(), this, null, getRootObject(), axiom));
            }
        }

	}

	/**
	 * @see org.protege.editor.owl.ui.frame.AbstractOWLFrameSection#clear()
	 */
	@Override
	protected void clear() {
	}

}
