package de.fuberlin.csw.aspectowl.owlapi.model;

import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAnonymousAspectImpl;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAspectAssertionAxiomImpl;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLNamedAspectImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;

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

    private ConcurrentHashMap<OntologyObjectTuple<OWLPointcut>, Set<OWLAspectAssertionAxiom>> aspectsForPointcut = CollectionFactory.createSyncMap();

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
        return Optional.ofNullable(aspectsForPointcut.get(new OntologyObjectTuple(ontology, new OWLJoinPointAxiomPointcut(potentialJoinPoint)))).orElse(Collections.emptySet()).stream().map(axiom -> axiom.getAspect()).collect(Collectors.toSet());
    }

    /**
     * Returns a set containing all aspects asserted for the given object.
     * @param ontology the contology containing the object declaration
     * @param potentialJoinPoint a potential join point consisting of an owl object
     * @return a stream containing all aspects asserted for the given join point
     */
    public Set<OWLAspectAssertionAxiom> getAspectAssertionAxioms(OWLOntology ontology, OWLAxiom potentialJoinPoint) {
        return CollectionFactory.getCopyOnRequestSet(Optional.ofNullable(aspectsForPointcut.get(new OntologyObjectTuple(ontology, new OWLJoinPointAxiomPointcut(potentialJoinPoint)))).orElse(Collections.emptySet()));
    }

    public void removeAspectAssertionAxiom(OWLOntology ontology, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        Optional.ofNullable(aspectsForPointcut.get(new OntologyObjectTuple(ontology, aspectAssertionAxiom.getPointcut()))).orElse(Collections.emptySet()).remove(aspectAssertionAxiom);
    }

    public boolean hasAssertedAspects(OWLOntology ontology, OWLAxiom joinPointAxiom) {
        return !Optional.ofNullable(aspectsForPointcut.get(new OntologyObjectTuple(ontology, new OWLJoinPointAxiomPointcut(joinPointAxiom)))).orElse(Collections.emptySet()).isEmpty();
    }

    public boolean hasAspects(OWLOntology ontology) {
        for (OntologyObjectTuple tuple : aspectsForPointcut.keySet()) {
            if (tuple.ontology.equals(ontology)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAspectInOntology(OWLClassExpression clsExpr, Set<OWLOntology> activeOntologies) {
        // TODO this is called often and is not efficient. Needs some sort of caching.

//        return aspectsForPointcut.keySet().stream().filter(tuple -> activeOntologies.contains(tuple.ontology)).map(key ->
//                aspectsForPointcut.get(key)).flatMap(set -> set.stream()).map(axiom ->
//                axiom.getAspect()).filter(aspect -> aspect.equals(clsExpr)).count() != 0;

        for (OntologyObjectTuple<OWLPointcut> tuple : aspectsForPointcut.keySet()) {
            for (OWLOntology activeOntology : activeOntologies) {
                if (activeOntology.equals(tuple.ontology)) { // HashSet.contains does not work here because an OWLOntologyImpl's hashCode is calculated based on the ontology's ID which may change after it was added to the set.
                    for (OWLAspectAssertionAxiom axiom : aspectsForPointcut.get(tuple)) {
                        OWLAspect aspect = axiom.getAspect();
                        if (aspect instanceof OWLNamedAspect) {
                            if (aspect.equals(clsExpr)) {
                                return true;
                            }
                            if (getEquivalentClassExpressions((OWLClass) aspect, activeOntologies).contains(clsExpr)) {
                                return true;
                            }
                        } else {
                            if (((OWLAnonymousAspect) aspect).getClassExpression().equals(clsExpr)) {
                                return true;
                            }
                            if (getEquivalentClassExpressions((OWLAnonymousClassExpression) aspect, activeOntologies).contains(clsExpr)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private Set<OWLClassExpression> getEquivalentClassExpressions(OWLClass cls, Set<OWLOntology> activeOntologies) {
        HashSet<OWLClassExpression> result = new HashSet<>();
        for (OWLOntology ontology : activeOntologies) {
            for (OWLEquivalentClassesAxiom eqClassAxiom : ontology.getEquivalentClassesAxioms(cls)) {
                result.addAll(eqClassAxiom.getClassExpressions());
            }
        }
        return result;
    }

    private Set<OWLClassExpression> getEquivalentClassExpressions(OWLAnonymousClassExpression clsExpr, Set<OWLOntology> activeOntologies) {
        HashSet<OWLClassExpression> result = new HashSet<>();
        for (OWLOntology ontology : activeOntologies) {
            for (OWLClass cls : ontology.getClassesInSignature()) {
                for (OWLEquivalentClassesAxiom eqClassAxiom : ontology.getEquivalentClassesAxioms(cls)) {
                    Set<OWLClassExpression> equivalentExpressions = eqClassAxiom.getClassExpressions();
                    if (equivalentExpressions.contains(clsExpr)) {
                        result.addAll(equivalentExpressions);
                    }
                }
            }
        }
        return result;
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
        Set<OWLAspectAssertionAxiom> aspects = aspectsForPointcut.get(key);
        if (aspects == null) {
            aspects = new HashSet<>();
            aspectsForPointcut.put(key, aspects);
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
        } else {
            aspectsForPointcut.remove(new OntologyObjectTuple<>(change.getOntology(), new OWLJoinPointAxiomPointcut(axiom)));
        }
    }

    @Override
    public void visit(RemoveOntologyAnnotation change) {
        change.getOntology();
        aspectsForPointcut.remove(change.getAnnotation());
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
