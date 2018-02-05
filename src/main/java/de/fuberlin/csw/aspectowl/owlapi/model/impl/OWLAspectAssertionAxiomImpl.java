package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import com.google.common.collect.Sets;
import de.fuberlin.csw.aspectowl.owlapi.model.*;
import de.fuberlin.csw.aspectowl.protege.views.AspectAssertionsList;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLLogicalAxiomImplWithEntityAndAnonCaching;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class OWLAspectAssertionAxiomImpl extends OWLLogicalAxiomImplWithEntityAndAnonCaching implements OWLAspectAssertionAxiom {

    private OWLOntology ontology;
    private OWLAxiom joinPointAxiom;
    private OWLAspect  aspect;

    public OWLAspectAssertionAxiomImpl(@Nonnull OWLOntology ontology, @Nonnull OWLAxiom joinPointAxiom, @Nonnull OWLAspect aspect, @Nonnull Collection<? extends OWLAnnotation> annotations) {
        super(annotations);
        this.ontology = ontology;
        this.joinPointAxiom = joinPointAxiom;
        this.aspect = aspect;
    }

    @Override
    public OWLAspect getAspect() {
        return aspect;
    }

    @Override
    public OWLAxiom getAxiom() {
        return joinPointAxiom;
    }

    @Override
    public void accept(@Nonnull OWLAxiomVisitor visitor) {
        if (visitor instanceof OWLAspectAxiomVisitor) {
            ((OWLAspectAxiomVisitor)visitor).visit(this);
        }
    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLAxiomVisitorEx<O> visitor) {
        if (visitor instanceof OWLAspectAxiomVisitorEx) {
            return ((OWLAspectAxiomVisitorEx<O>)visitor).visit(this);
        }
        return null;
    }

    @Nonnull
    @Override
    public OWLAxiom getAxiomWithoutAnnotations() {
        return new OWLAspectAssertionAxiomImpl(ontology, joinPointAxiom, aspect, Collections.EMPTY_SET);
    }

    @Nonnull
    @Override
    public OWLAxiom getAnnotatedAxiom(@Nonnull Set<OWLAnnotation> annotations) {
        return new OWLAspectAssertionAxiomImpl(ontology, joinPointAxiom, aspect, Sets.union(getAnnotations(), annotations));
    }

    @Nonnull
    @Override
    public AxiomType<?> getAxiomType() {
        return AspectAssertionsList.OWL_AXIOM_ASSERTION_AXIOM_TYPE;
    }

    @Override
    protected int compareObjectOfSameType(@Nonnull OWLObject object) {
        OWLAspectAssertionAxiomImpl otherAx = (OWLAspectAssertionAxiomImpl) object;
        int diff = getAxiom().compareTo(otherAx.getAxiom());
        if (diff != 0) {
            return diff;
        } else {
            return getAspect().compareTo(otherAx.getAspect());
        }
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        if (visitor instanceof OWLAspectAxiomVisitor) {
            ((OWLAspectAxiomVisitor) visitor).visit(this);
        }
    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> visitor) {
        if (visitor instanceof OWLAspectAxiomVisitorEx) {
            return ((OWLAspectAxiomVisitorEx<O>) visitor).visit(this);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OWLAspectAssertionAxiomImpl)) return false;
        if (!super.equals(o)) return false;
        OWLAspectAssertionAxiomImpl that = (OWLAspectAssertionAxiomImpl) o;
        return Objects.equals(ontology, that.ontology) &&
                Objects.equals(joinPointAxiom, that.joinPointAxiom) &&
                Objects.equals(aspect, that.aspect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ontology, joinPointAxiom, aspect);
    }
}
