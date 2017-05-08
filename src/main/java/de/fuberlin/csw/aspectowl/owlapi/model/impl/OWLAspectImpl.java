/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObject;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationImpl;

/**
 * @author ralph
 */
public class OWLAspectImpl extends OWLAnnotationImpl implements OWLAspect {

	/**
	 * @param property
	 * @param value
	 * @param annotations
	 */
	public OWLAspectImpl(OWLAnnotationProperty aspectProperty, OWLAnnotationValue value,
			Set<? extends OWLAnnotation> annotations) {
		super(aspectProperty, value, annotations);
	}

	private static final long serialVersionUID = 4829969668138075822L;

	/**
	 * @see de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect#getPointcut()
	 */
	@Override
	public Set<OWLObject> getPointcut() {
		return null;
	}

	/**
	 * @see de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect#getAdvice()
	 */
	@Override
	public OWLClassExpression getAdvice() {
		OWLAnnotationValue value = getValue();
		if (value.getClass().isAssignableFrom(OWLClassExpression.class)) {
			return (OWLClassExpression)value;
		}
		throw new IllegalStateException("Advice is not a class expression. This should have been prevented at creation time.");
	}
	



}
