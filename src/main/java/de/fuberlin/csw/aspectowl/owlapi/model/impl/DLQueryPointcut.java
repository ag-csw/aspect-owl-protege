package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLPointcut;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;

import java.util.Set;

public class DLQueryPointcut implements OWLPointcut {

    // TODO interface might need changes
    public DLQueryPointcut(OWLAspect aspect,
            OWLClassExpression dlQuery,
            Set<OWLAnnotation> annotations) {
    }
}
