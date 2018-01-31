package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;

public interface OWLAspectAxiomVisitorEx<O> extends OWLAxiomVisitorEx<O> {
    O visit(OWLAspectAssertionAxiom axiom);
}
