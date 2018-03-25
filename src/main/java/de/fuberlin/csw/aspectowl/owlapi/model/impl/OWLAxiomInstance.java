package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import de.fuberlin.csw.aspectowl.owlapi.model.AspectContainer;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

public class OWLAxiomInstance implements AspectContainer {
    private final OWLAxiom ax;
    private final OWLOntology ont;


    public OWLAxiomInstance(OWLAxiom ax, OWLOntology ont) {
        this.ax = checkNotNull(ax);
        this.ont = ont;
    }

    public OWLAxiom getAxiom() {
        return ax;
    }


    public OWLOntology getOntology() {
        return ont;
    }

    @Override
    public Set<OWLAspect> getAspects() {
        return OWLOntologyAspectManager.instance().getAssertedAspects(ont, ax);
    }


    public boolean isAsserted() {
        return !isInferred();
    }

    public boolean isInferred() {
        return ont == null;
    }


    @Override
    public String toString() {
        return toStringHelper("OWLAxiomInstance")
                .addValue(ax)
                .addValue(ont.getOntologyID())
                .toString();
    }

}
