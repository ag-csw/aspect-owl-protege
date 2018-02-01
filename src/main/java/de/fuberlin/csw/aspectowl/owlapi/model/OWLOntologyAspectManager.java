package de.fuberlin.csw.aspectowl.owlapi.model;

import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAspectAssertionAxiomImpl;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLNamedAspectImpl;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;
import uk.ac.manchester.cs.owl.owlapi.OWLAnonymousAspectImpl;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public OWLAspect getAspect(OWLOntology ontology, OWLAxiom joinPointAxiom, OWLClassExpression expr) {
        if (expr.isAnonymous()) {
            return new OWLAnonymousAspectImpl((OWLAnonymousClassExpression)expr);
        }
        return new OWLNamedAspectImpl(((OWLClass) expr).getIRI());
    }

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

    public void removeAspectAssertionAxiom(OWLOntology ontology, OWLAxiom jointPointAxiom, OWLAspectAssertionAxiom aspectAssertionAxiom) {
        Optional.ofNullable(aspectsForObject.get(new OntologyAxiomTuple(ontology, jointPointAxiom))).orElse(Collections.emptySet()).remove(aspectAssertionAxiom);
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
            aspectsForObject.remove(new OntologyAxiomTuple(change.getOntology(), change.getAxiom()));
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
        public boolean equals(Object obj) {
            if (!(obj instanceof  OntologyAxiomTuple))
                return false;
            OntologyAxiomTuple other = (OntologyAxiomTuple)obj;
            return this.ontology.equals(other.ontology) && this.axiom.equals(other.axiom);
        }

        @Override
        public int hashCode() {
            return ontology.hashCode() * 37 + axiom.hashCode();
        }
    }
}
