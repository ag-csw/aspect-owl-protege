/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.*;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;

/**
 *
 * @author ralph
 */
public class OWLAspectImplDelegate {

	private OWLAspect aspect;

	private HashSet<OWLObjectProperty> accessibilityRelations = new HashSet<>();

	private HashSet<OWLAxiom> assertedJoinPoints = new HashSet<>();

	/**
	 * @param aspect
	 */
	public OWLAspectImplDelegate(OWLAspect aspect) {
		this.aspect = aspect;
	}

	private static final long serialVersionUID = 4829969668138075822L;

	/**
	 * @see de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect#getPointcut()
	 */
	public Set<OWLAxiom> getPointcut() {
		return assertedJoinPoints;
	}

	public Set<OWLObjectProperty> getAccessibilityRelations() {
		return Sets.union(accessibilityRelations, aspect.getObjectPropertiesInSignature());
	}

}
