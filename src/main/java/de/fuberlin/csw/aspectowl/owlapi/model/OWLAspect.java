/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

/**
 * @author ralph
 *
 */
public interface OWLAspect extends OWLAnnotation {
	
	/**
	 * Returns the pointcut of this aspect, i.e. the set of axioms that are
	 * segregated under this aspect.
	 * @return
	 */
	public Set<OWLObject> getPointcut();
	
	
	public OWLClassExpression getAdvice();
	
	
}
