package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import com.google.common.collect.Sets;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLNamedAspect;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import javax.annotation.Nonnull;
import java.util.Set;

public class OWLNamedAspectImpl extends OWLClassImpl implements OWLNamedAspect {

    private OWLAspectImplDelegate delegate = new OWLAspectImplDelegate(this);

    /**
     * @param iri class iri
     */
    public OWLNamedAspectImpl(@Nonnull IRI iri) {
        super(iri);
    }

    @Override
    public Set<OWLAxiom> getPointcut() {
        return delegate.getPointcut();
    }

    @Override
    public Set<OWLObjectProperty> getAccessibilityRelations() {
        return Sets.union(delegate.getAccessibilityRelations(), getObjectPropertiesInSignature());
    }
}
