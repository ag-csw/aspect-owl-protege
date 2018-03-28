package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.HasAnnotations;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public abstract class OWLPointcut implements HasAnnotations {

    private final List<OWLAnnotation> annos;

    public OWLPointcut(Set<OWLAnnotation> annos) {
        this.annos = CollectionFactory.sortOptionally(annos);
    }

    /**
     * Returns a set containing the axioms that are asserted to belong to this pointcut.
     * @return
     */
    public abstract Set<OWLAxiom> getAssertedAxiomsInPointcut();

    /**
     * Returns a set containing the axioms that are inferred to belong to this pointcut.
     * @return
     */
    public abstract Set<OWLAxiom> getInferredAxiomsInPointcut();

    @Nonnull
    @Override
    public Set<OWLAnnotation> getAnnotations() {
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(annos);
    }
}
