package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import javax.annotation.Nonnull;

public abstract class OWLPointcutChange extends OWLOntologyChange {

    private OWLPointcut pointcut;

    /**
     * @param ont the ontology to which the change is to be applied
     */
    public OWLPointcutChange(@Nonnull OWLOntology ont, @Nonnull OWLPointcut pointcut) {
        super(ont);
        this.pointcut = pointcut;
    }

    public OWLPointcut getPointcut() {
        return pointcut;
    }


}
