/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import java.util.*;

import com.google.common.collect.Sets;
import org.semanticweb.owlapi.model.*;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import org.semanticweb.owlapi.util.CollectionFactory;

import javax.annotation.Nonnull;

/**
 *
 * @author ralph
 */
public class OWLAspectImplDelegate {

	private OWLAspect aspect;

	private HashSet<OWLObjectProperty> accessibilityRelations = new HashSet<>();

	private HashSet<OWLAxiom> assertedJoinPoints = new HashSet<>();

	@Nonnull
	private final List<OWLAnnotation> annotations;

	/**
	 * @param aspect
	 * @param annotations
	 */
	public OWLAspectImplDelegate(OWLAspect aspect, Set<OWLAnnotation> annotations) {
		this.aspect = aspect;
		this.annotations = CollectionFactory.sortOptionally((Set<OWLAnnotation>) annotations);
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

	@Nonnull
	public Set<OWLAnnotation> getAnnotations() {
		return CollectionFactory
				.getCopyOnRequestSetFromImmutableCollection(annotations);
	}

}
