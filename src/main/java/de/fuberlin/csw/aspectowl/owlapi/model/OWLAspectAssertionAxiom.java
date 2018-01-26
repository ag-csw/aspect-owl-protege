package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAxiom;

public interface OWLAspectAssertionAxiom extends OWLAxiom {
    public OWLAspect getAspect();
    public OWLAxiom getAxiom();
}
