package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

public class OWLEntityPointcut implements OWLPointcut<OWLAnnotationSubject> {
    private OWLJoinPoint entity;

    public OWLEntityPointcut(OWLJoinPoint entity) {
        this.entity = entity;
    }

    public OWLJoinPoint getJoinPointEntity() {
        return entity;
    }

    @Override
    public int compareTo(OWLPointcut o) {
        if (o instanceof OWLEntityPointcut) {
            OWLAnnotationSubject otherEntity = ((OWLEntityPointcut)o).getJoinPointEntity().getSubject();
            if (otherEntity instanceof IRI) {
                if (this.entity.getSubject() instanceof IRI) {
                    return ((IRI) otherEntity).compareTo((IRI) entity.getSubject());
                }
                // anonymous come last
                return -1;
            }
            // other is anonymous
            if (this.entity.getSubject() instanceof IRI) {
                return 1;
            }
            return ((OWLAnonymousIndividual) entity.getSubject()).compareTo(((OWLAnonymousIndividual) otherEntity));
        }
        // entity pointcuts come last
        return -1;
    }
}
