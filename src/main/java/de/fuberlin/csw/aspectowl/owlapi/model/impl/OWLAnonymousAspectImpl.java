package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAnonymousAspect;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;
import uk.ac.manchester.cs.owl.owlapi.OWLAnonymousClassExpressionImpl;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Is in package uk.ac.manchester.cs.owl.owlapi because it needs to access the index and compareObjectOfSameType
 * methods, which are protected.
 */
public class OWLAnonymousAspectImpl extends OWLAnonymousClassExpressionImpl implements OWLAnonymousAspect {

    static final OWLObjectTypeIndexProvider OWLOBJECT_TYPEINDEX_PROVIDER = new OWLObjectTypeIndexProvider();

    private Method indexMethod;
    private Method compareObjectOfSameTypeMethod;

    private OWLAnonymousClassExpressionImpl ceDelegate;
    private OWLAspectImplDelegate aspectDelegate;

    public OWLAnonymousAspectImpl(OWLAnonymousClassExpression classExpression) {
        try {
            this.ceDelegate = (OWLAnonymousClassExpressionImpl) classExpression;
            this.aspectDelegate = new OWLAspectImplDelegate(this);

            Class poorInnocentClass = classExpression.getClass();

            indexMethod = poorInnocentClass.getMethod("index");
            compareObjectOfSameTypeMethod = poorInnocentClass.getMethod("compareObjectOfSameType", Object.class);

            Arrays.asList(indexMethod, compareObjectOfSameTypeMethod).forEach(method -> method.setAccessible(true));

        } catch (ClassCastException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
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
        try {
            return (Integer) indexMethod.invoke(ceDelegate);
        } catch (IllegalAccessException | InvocationTargetException e)  {
            e.printStackTrace();
            return 0;
        }
//        return ceDelegate.index() + 17; // there are 17 types of anonymous class expressions in OWL 2 (index 1 to 17), we assign aspect anonymous CEs indexes from 18 to 35.
    }

    @Override
    protected int compareObjectOfSameType(@Nonnull OWLObject object) {
        try {
            return (Integer)compareObjectOfSameTypeMethod.invoke(ceDelegate, object);
        } catch (IllegalAccessException | InvocationTargetException e)  {
            e.printStackTrace();
            return 0;
        }
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
