package de.fuberlin.csw.aspectowl.owlapi.model;

import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAspectAssertionAxiomImpl;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLNamedAspectImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAnonymousAspectImpl;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/* I know. Calling a class 'SomethingManager' is an anti-pattern. What brought me here was days of
  unsuccessful experimenting with OSGI byte code weaving (in order to add all the aspect related
  functionality to the existing OWL object interfaces in the OWLAPI, hacks for replacing the
  OWLDataFactory during or after the initialization of the Protégé OWL workspace (bottom line:
  not possible),   */
public class OWLOntologyAspectManager extends OWLOntologyChangeVisitorAdapter implements OWLOntologyChangeListener {

    private static OWLOntologyAspectManager instance;

    public static OWLOntologyAspectManager instance() {
        if (instance == null) {
            instance = new OWLOntologyAspectManager();
        }
        return instance;
    }

    private ConcurrentHashMap<OntologyObjectTuple<OWLPointcut>, Set<OWLAspectAssertionAxiom>> aspectsForAxiom = CollectionFactory.createSyncMap();

    /**
     * Creates and returns a new OWLAspect constructed from the given ontology, join point and advice class expression.
     * @param expr
     * @param annotations
     * @return
     */
    public OWLAspect getAspect(OWLClassExpression expr, Set<OWLAnnotation> annotations) {
        if (expr.isAnonymous()) {
            return new OWLAnonymousAspectImpl((OWLAnonymousClassExpression)expr, annotations);
        }
        return new OWLNamedAspectImpl(((OWLClass) expr).getIRI(), annotations);
    }

    /**
     * Creates and returns a new OWLAspectAssertionAxiom for the given ontology, joint point, and aspect.
     * (Needed for creating object aspects)
     * @param ontology
     * @param pointcut
     * @param aspect
     * @return
     */
    public OWLAspectAssertionAxiom getAspectAssertionAxiom(OWLOntology ontology, OWLPointcut pointcut, OWLAspect aspect) {
        return new OWLAspectAssertionAxiomImpl(ontology, pointcut, aspect);
    }

    /**
     * Returns a set containing all aspects asserted for the given object.
     * @param potentialJoinPoint a potential join point consisting of an owl object
     * @return a stream containing all aspects asserted for the given join point
     */
    public Set<OWLAspect> getAssertedAspects(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForAxiom.get(new OntologyObjectTuple(ontology, potentialJoinPoint))).orElse(Collections.emptySet()).stream().map(axiom -> axiom.getAspect()).collect(Collectors.toSet());
    }

    /**
     * Returns a set containing all aspects asserted for the given object.
     * @param ontology the contology containing the object declaration
     * @param potentialJoinPoint a potential join point consisting of an owl object
     * @return a stream containing all aspects asserted for the given join point
     */
    public Set<OWLAspectAssertionAxiom> getAspectAssertionAxioms(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        return CollectionFactory.getCopyOnRequestSet(Optional.ofNullable(aspectsForAxiom.get(new OntologyObjectTuple(ontology, potentialJoinPoint))).orElse(Collections.emptySet()));
    }

    public void removeAspectAssertionAxiom(OWLOntology ontology, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        Optional.ofNullable(aspectsForAxiom.get(new OntologyObjectTuple(ontology, aspectAssertionAxiom.getPointcut()))).orElse(Collections.emptySet()).remove(aspectAssertionAxiom);
    }

    public boolean hasAssertedAspects(OWLOntology ontology, OWLAxiom joinPointAxiom) {
        return !Optional.ofNullable(aspectsForAxiom.get(new OntologyObjectTuple(ontology, joinPointAxiom))).orElse(Collections.emptySet()).isEmpty();
    }

    /**
     * Returns a stream of all inferred aspects for the given object.
     * @param potentialJoinPoint a potential join point consisting of an owl object
     * @return a stream containing all inferred aspects for the given join point
     */
    public Set<OWLAspect> getInferredAspects(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        // TODO
        return Collections.emptySet();
    }

    public void addAspect(OWLOntology ontology, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        OntologyObjectTuple<OWLPointcut> key = new OntologyObjectTuple<>(ontology, aspectAssertionAxiom.getPointcut());
        Set<OWLAspectAssertionAxiom> aspects = aspectsForAxiom.get(key);
        if (aspects == null) {
            aspects = new HashSet<>();
            aspectsForAxiom.put(key, aspects);
        }
        aspects.add(aspectAssertionAxiom);
    }

    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) throws OWLException {
        changes.forEach(change -> change.accept(this));
    }

    @Override
    public void visit(AddAxiom change) {
        OWLAxiom axiom = change.getAxiom();
        if (axiom instanceof OWLAspectAssertionAxiom) {
            OWLAspectAssertionAxiom aspectAssertionAxiom = (OWLAspectAssertionAxiom)axiom;
            addAspect(change.getOntology(), aspectAssertionAxiom);
        }
    }

    @Override
    public void visit(RemoveAxiom change) {
        OWLAxiom axiom = change.getAxiom();
        if (axiom instanceof OWLAspectAssertionAxiom) {
            removeAspectAssertionAxiom(change.getOntology(), ((OWLAspectAssertionAxiom) axiom));
        }
    }

    @Override
    public void visit(RemoveOntologyAnnotation change) {
        change.getOntology();
        aspectsForAxiom.remove(change.getAnnotation());
    }

    private class OntologyObjectTuple<O> {

        private OWLOntology ontology;
        private O object;

        public OntologyObjectTuple(@Nonnull OWLOntology ontology, @Nonnull O object) {
            this.ontology = ontology;
            this.object = object;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OntologyObjectTuple<O> that = (OntologyObjectTuple<O>) o;
            return Objects.equals(ontology, that.ontology) &&
                    Objects.equals(object, that.object);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ontology, object);
        }
    }
}
