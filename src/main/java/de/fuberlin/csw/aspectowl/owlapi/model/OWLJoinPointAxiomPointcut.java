package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.Set;

public class OWLJoinPointAxiomPointcut extends OWLAxiomPointcut {

    private OWLAxiom joinPointAxiom;

    public OWLJoinPointAxiomPointcut(OWLAxiom joinPointAxiom) {
        this.joinPointAxiom = joinPointAxiom;
    }

    @Override
    public Set<OWLAxiom> getAssertedAxiomsInPointcut() {
        return CollectionFactory.createSet(joinPointAxiom);
    }

    @Override
    public Set<OWLAxiom> getInferredAxiomsInPointcut() {
        // TODO
        return null;
    }
}
