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

    private ConcurrentHashMap<OWLAxiom, Set<OWLAspect>> aspectsForObject = new ConcurrentHashMap<>();

    /**
     * Returns a stream of all aspects asserted for the given axiom.
     * @param potentialJoinPoint a potential join point consisting of an owl axiom
     * @return a stream containing all aspects asserted for the given join point
     */
    public Stream<OWLAspect> getAssertedAspects(OWLAxiom potentialJoinPoint) {
        return Optional.ofNullable(aspectsForObject.get(potentialJoinPoint)).orElse(Collections.emptySet()).stream();
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

    public void addAspect(OWLAspect aspect, OWLAxiom axiom) {
        aspectsForObject.putIfAbsent(axiom, Optional.of(aspectsForObject.get(axiom)).orElse(new HashSet<>())).add(aspect);
    }

    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) throws OWLException {
        changes.forEach(change -> change.accept(this));
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
