package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLOntologyChangeVisitorEx;
import org.semanticweb.owlapi.model.RemoveAxiom;

import javax.annotation.Nonnull;

public interface AspectOWLOntologyChangeVisitorEx<O> extends OWLOntologyChangeVisitorEx<O> {

    /**
     * visit AddAxiom type
     *
     * @param change
     *        change to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull AddPointcut change);

    /**
     * visit RemoveAxiom type
     *
     * @param change
     *        change to visit
     * @return visitor value
     */
    @Nonnull
    O visit(@Nonnull RemovePointcut change);

}
