package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.HasAnnotations;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.util.CollectionFactory;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public abstract class OWLAxiomPointcut implements OWLPointcut<OWLAxiom> {

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj.getClass() == this.getClass())) {
            return false;
        }
        return ((OWLAxiomPointcut) obj).getAssertedAxiomsInPointcut().equals(getAssertedAxiomsInPointcut());
    }

    @Override
    public int compareTo(OWLPointcut<OWLAxiom> o) {
        // TODO
        return 0;
    }
}
