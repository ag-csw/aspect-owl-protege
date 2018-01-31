package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;

public interface OWLAspectAxiomVisitor extends OWLAxiomVisitor {
    void visit(OWLAspectAssertionAxiom axiom);
}
