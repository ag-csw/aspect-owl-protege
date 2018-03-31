package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import com.google.common.collect.Sets;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLNamedAspect;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

public class OWLNamedAspectImpl extends OWLClassImpl implements OWLNamedAspect {

    private final OWLAspectImplDelegate delegate;

    /**
     * @param iri class iri
     * @param annotations
     */
    public OWLNamedAspectImpl(@Nonnull IRI iri, Set<OWLAnnotation> annotations) {
        super(iri);
        delegate = new OWLAspectImplDelegate(this, annotations);
    }

    @Override
    public Set<OWLObjectProperty> getAccessibilityRelations() {
        return Sets.union(delegate.getAccessibilityRelations(), getObjectPropertiesInSignature());
    }

    @Override
    public OWLAspect getAspectWithoutAnnotations() {
        return new OWLNamedAspectImpl(getIRI(), Collections.EMPTY_SET);
    }

    @Nonnull
    @Override
    public Set<OWLAnnotation> getAnnotations() {
        return delegate.getAnnotations();
    }
}
