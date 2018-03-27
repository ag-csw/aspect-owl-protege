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

    private ConcurrentHashMap<OntologyAxiomTuple, Set<OWLAspectAssertionAxiom>> aspectsForObject = CollectionFactory.createSyncMap();

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
     * Creates and returns a new OWLAspectAssertionAxiom for the given ontology, joint point, aspect and annotations.
     * @param ontology
     * @param joinPointAxiom
     * @param aspect
     * @param annotations
     * @return
     */
    public OWLAspectAssertionAxiom getAspectAssertionAxiom(OWLOntology ontology, OWLAxiom joinPointAxiom, OWLAspect aspect, Set<OWLAnnotation> annotations) {
        return new OWLAspectAssertionAxiomImpl(ontology, joinPointAxiom, aspect, annotations);
    }

    /**
     * Returns a set containing all aspects asserted for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all aspects asserted for the given join point
     */
    public Set<OWLAspect> getAssertedAspects(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForObject.get(new OntologyAxiomTuple(ontology, potentialJoinPoint))).orElse(Collections.emptySet()).stream().map(axiom -> axiom.getAspect()).collect(Collectors.toSet());
    }

    /**
     * Returns a set containing all aspects asserted for the given axiom.
     * @param ontology the contology containing the axiom declaration
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all aspects asserted for the given join point
     */
    public Set<OWLAspectAssertionAxiom> getAspectAssertionAxioms(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForObject.get(new OntologyAxiomTuple(ontology, potentialJoinPoint))).orElse(Collections.emptySet());
    }

    public void removeAspectAssertionAxiom(OWLOntology ontology, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        Optional.ofNullable(aspectsForObject.get(new OntologyAxiomTuple(ontology, aspectAssertionAxiom.getAxiom()))).orElse(Collections.emptySet()).remove(aspectAssertionAxiom);
    }

    public boolean hasAssertedAspects(OWLOntology ontology, OWLAxiom joinPointAxiom) {
        return !Optional.ofNullable(aspectsForObject.get(new OntologyAxiomTuple(ontology, joinPointAxiom))).orElse(Collections.emptySet()).isEmpty();
    }

    /**
     * Returns a stream of all inferred aspects for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all inferred aspects for the given join point
     */
    public Set<OWLAspect> getInferredAspects(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        // TODO
        return Collections.emptySet();
    }

    public void addAspect(OWLOntology ontology, OWLAxiom joinPointAxiom, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        OntologyAxiomTuple key = new OntologyAxiomTuple(ontology, joinPointAxiom);
        Set<OWLAspectAssertionAxiom> aspects = aspectsForObject.get(key);
        if (aspects == null) {
            aspects = new HashSet<>();
            aspectsForObject.put(key, aspects);
        }
        aspects.add(aspectAssertionAxiom);
    }

    public void addPointCut(OWLPointcut pointcut) {

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
            OWLAxiom joinPointAxiom = aspectAssertionAxiom.getAxiom();
            addAspect(change.getOntology(), joinPointAxiom, aspectAssertionAxiom);
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
        aspectsForObject.remove(change.getAnnotation());
    }

    private class OntologyAxiomTuple {

        private OWLOntology ontology;
        private OWLAxiom axiom;

        public OntologyAxiomTuple(@Nonnull OWLOntology ontology, @Nonnull OWLAxiom axiom) {
            this.ontology = ontology;
            this.axiom = axiom;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OntologyAxiomTuple that = (OntologyAxiomTuple) o;
            return Objects.equals(ontology, that.ontology) &&
                    Objects.equals(axiom, that.axiom);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ontology, axiom);
        }
    }
}
