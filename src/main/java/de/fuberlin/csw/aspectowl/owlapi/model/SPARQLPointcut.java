package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;

import java.util.Objects;
import java.util.Set;

public class SPARQLPointcut extends OWLPointcut {

    private String query;

    public SPARQLPointcut(String query, Set<OWLAnnotation> annotations) {
        super(annotations);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public Set<OWLAxiom> getAssertedAxiomsInPointcut() {
        //TODO code already implemented in de.fuberlin.csw.aspectowl.inference.AspectSparqlQueryExecutor. Just call this code.
        return null;
    }

    @Override
    public Set<OWLAxiom> getInferredAxiomsInPointcut() {
        //TODO
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SPARQLPointcut)) return false;
        SPARQLPointcut that = (SPARQLPointcut) o;
        return Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return 13 * super.hashCode() + Objects.hash(query);
    }
}
