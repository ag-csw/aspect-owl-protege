package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import de.fuberlin.csw.aspectowl.owlapi.model.*;
import de.fuberlin.csw.aspectowl.protege.views.AspectAssertionsList;
import org.semanticweb.owlapi.model.*;
import uk.ac.manchester.cs.owl.owlapi.OWLLogicalAxiomImplWithEntityAndAnonCaching;

import javax.annotation.Nonnull;
import java.util.*;

public class OWLAspectAssertionAxiomImpl extends OWLLogicalAxiomImplWithEntityAndAnonCaching implements OWLAspectAssertionAxiom {

    private OWLOntology ontology;
    private OWLPointcut pointcut;
    private OWLAspect  aspect;

    public OWLAspectAssertionAxiomImpl(@Nonnull OWLOntology ontology, @Nonnull OWLPointcut pointcut, @Nonnull OWLAspect aspect) {
        super(aspect.getAnnotations());
        this.ontology = ontology;
        this.pointcut = pointcut;
        this.aspect = aspect;
    }

    @Override
    public OWLAspect getAspect() {
        return aspect;
    }

    @Override
    public OWLPointcut getPointcut() {
        return pointcut;
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
        OWLAspect aspectWithoutAnnotations = aspect.getAspectWithoutAnnotations();
        return new OWLAspectAssertionAxiomImpl(ontology, pointcut, aspectWithoutAnnotations);
    }

    @Nonnull
    @Override
    public OWLAxiom getAnnotatedAxiom(@Nonnull Set<OWLAnnotation> annotations) {
        return new OWLAspectAssertionAxiomImpl(ontology, pointcut, aspect);
    }

    @Nonnull
    @Override
    public AxiomType<?> getAxiomType() {
        return AspectAssertionsList.OWL_AXIOM_ASSERTION_AXIOM_TYPE;
    }

    @Override
    protected int compareObjectOfSameType(@Nonnull OWLObject object) {
        OWLAspectAssertionAxiomImpl otherAx = (OWLAspectAssertionAxiomImpl) object;
        int diff = getPointcut().compareTo(otherAx.getPointcut());
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
                Objects.equals(pointcut, that.pointcut) &&
                Objects.equals(aspect, that.aspect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ontology, pointcut, aspect);
    }

}
