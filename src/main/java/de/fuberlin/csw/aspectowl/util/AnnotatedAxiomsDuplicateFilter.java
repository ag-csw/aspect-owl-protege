/**
 * 
 */
package de.fuberlin.csw.aspectowl.util;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This class filters duplicated axioms from inferred ontologies to circumvent a
 * particularity most OWL reasoners are affected with. Most OWL reasoners
 * duplicate annotated axioms when generating the inferred model, leaving one
 * annotated and one non-annotated version of each axiom in the ontology. This
 * class removes all non-annotated axioms if an annotated version of the same
 * axiom exists, leaving only the annotated versions.
 * 
 * @author ralph
 */
public class AnnotatedAxiomsDuplicateFilter {
	
	public static void filter(OWLOntology ontology) {
		
		OWLOntologyManager om = ontology.getOWLOntologyManager();
		
		Set<OWLAxiom> axioms = ontology.getAxioms();
		for (OWLAxiom axiom : axioms) {
			if (!axiom.getAnnotations().isEmpty()) {
				OWLAxiom axiomWithoutAnnotations = axiom.getAxiomWithoutAnnotations();
				if (ontology.containsAxiom(axiomWithoutAnnotations)) {
					om.removeAxiom(ontology, axiomWithoutAnnotations);
				}
			}
		}
	}
}
