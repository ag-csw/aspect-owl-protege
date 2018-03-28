package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class OWLModulePointcut extends OWLPointcut {

    private final List<OWLEntity> signature;

    public OWLModulePointcut(Set<OWLEntity> signature, Set<OWLAnnotation> annos) {
        super(annos);
        this.signature = CollectionFactory.sortOptionally(signature);
    }

    public Set<OWLEntity> getSignature() {
        return CollectionFactory.getCopyOnRequestSet(signature);
    }

    @Override
    public Set<OWLAxiom> getAssertedAxiomsInPointcut() {
        // TODO locality based module
        return null;
    }

    // TODO
    @Override
    public Set<OWLAxiom> getInferredAxiomsInPointcut() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OWLModulePointcut)) return false;
        OWLModulePointcut that = (OWLModulePointcut) o;
        return Objects.equals(signature, that.signature);
    }

    @Override
    public int hashCode() {
        return 17 * super.hashCode() + Objects.hash(signature);
    }
}
