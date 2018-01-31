package uk.ac.manchester.cs.owl.owlapi;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAnonymousAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAspectImplDelegate;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Is in package uk.ac.manchester.cs.owl.owlapi because it needs to access the index and compareObjectOfSameType
 * methods, which are protected.
 */
public class OWLAnonymousAspectImpl extends OWLAnonymousClassExpressionImpl implements OWLAnonymousAspect {

    static final OWLObjectTypeIndexProvider OWLOBJECT_TYPEINDEX_PROVIDER = new OWLObjectTypeIndexProvider();

    private OWLAnonymousClassExpressionImpl ceDelegate;
    private OWLAspectImplDelegate aspectDelegate;

    public OWLAnonymousAspectImpl(OWLAnonymousClassExpression classExpression) {
        if (!(classExpression instanceof OWLClassExpressionImpl)) {
            throw new IllegalArgumentException("Can only deal with default implementations of entity types.");
        }

        this.ceDelegate = (OWLAnonymousClassExpressionImpl)classExpression;
        this.aspectDelegate = new OWLAspectImplDelegate(this);
    }

    @Override
    public Set<OWLAxiom> getPointcut() {
        return aspectDelegate.getPointcut();
    }

    @Override
    public Set<OWLObjectProperty> getAccessibilityRelations() {
        return aspectDelegate.getAccessibilityRelations();
    }

    @Nonnull
    @Override
    public ClassExpressionType getClassExpressionType() {
        return ceDelegate.getClassExpressionType();
    }

    @Override
    public boolean isClassExpressionLiteral() {
        return ceDelegate.isClassExpressionLiteral();
    }

    @Override
    public void accept(@Nonnull OWLClassExpressionVisitor visitor) {
        ceDelegate.accept(visitor);
    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLClassExpressionVisitorEx<O> visitor) {
        return ceDelegate.accept(visitor);
    }

    @Override
    protected int index() {
        return ceDelegate.index() + 17; // there are 17 types of anonymous class expressions in OWL 2 (index 1 to 17), we assign aspect anonymous CEs indexes from 18 to 35.
    }

    @Override
    protected int compareObjectOfSameType(@Nonnull OWLObject object) {
        return ceDelegate.compareObjectOfSameType(object);
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        ceDelegate.accept(visitor);
    }

    @Nonnull
    @Override
    public <O> O accept(@Nonnull OWLObjectVisitorEx<O> visitor) {
        return ceDelegate.accept(visitor);
    }

    @Override
    public void addSignatureEntitiesToSet(@Nonnull Set<OWLEntity> entities) {
        ceDelegate.addSignatureEntitiesToSet(entities);
    }

    @Override
    public void addAnonymousIndividualsToSet(@Nonnull Set<OWLAnonymousIndividual> anons) {
        ceDelegate.addAnonymousIndividualsToSet(anons);
    }
}
