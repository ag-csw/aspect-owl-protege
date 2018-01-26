package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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

    private ConcurrentHashMap<OWLAxiom, Set<OWLAspectAssertionAxiom>> aspectsForObject = new ConcurrentHashMap<>();

    /**
     * Returns a stream of all aspects asserted for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all aspects asserted for the given join point
     */
    public Stream<OWLAspect> getAssertedAspects(OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForObject.get(potentialJoinPoint)).orElse(Collections.emptySet()).stream().map(assertionAxiom -> assertionAxiom.getAspect());
    }

    /**
     * Returns a stream of all aspects asserted for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all aspects asserted for the given join point
     */
    public Stream<OWLAspectAssertionAxiom> getAspectAssertionAxioms(OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForObject.get(potentialJoinPoint)).orElse(Collections.emptySet()).stream();
    }

    public void removeAssertedAspect(OWLAxiom axiom, OWLAspect aspect) {
        Optional.ofNullable(aspectsForObject.get(axiom)).orElse(Collections.emptySet()).remove(aspect);
    }

    public boolean hasAssertedAspects(OWLAxiom axiom) {
        return !Optional.ofNullable(aspectsForObject.get(axiom)).orElse(Collections.emptySet()).isEmpty();
    }

    /**
     * Returns a stream of all inferred aspects for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all inferred aspects for the given join point
     */
    public Stream<OWLAspect> getInferredAspects(OWLAxiom potentialJoinPoint) {
        // TODO
        return Stream.empty();
    }

    public void addAspect(OWLAspectAssertionAxiom aspect, OWLAxiom axiom) {
        Set<OWLAspectAssertionAxiom> aspects = aspectsForObject.get(axiom);
        if (aspects == null) {
            aspects = new HashSet<>();
            aspectsForObject.put(axiom, aspects);
        }
        aspects.add(aspect);
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
            addAspect(aspectAssertionAxiom, axiom);
        }
    }

    @Override
    public void visit(RemoveAxiom change) {
        aspectsForObject.remove(change.getAxiom());
    }

    @Override
    public void visit(RemoveOntologyAnnotation change) {
        aspectsForObject.remove(change.getAnnotation());
    }
}
