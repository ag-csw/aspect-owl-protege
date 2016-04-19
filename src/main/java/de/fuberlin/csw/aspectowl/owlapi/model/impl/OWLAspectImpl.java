/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectImplWithoutEntityAndAnonCaching;

/**
 * @author ralph
 */
public class OWLAspectImpl extends OWLObjectImplWithoutEntityAndAnonCaching implements OWLAspect {

	private static final long serialVersionUID = 4829969668138075822L;

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLObject#accept(org.semanticweb.owlapi.model.OWLObjectVisitor)
	 */
	@Override
	public void accept(OWLObjectVisitor visitor) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLObject#accept(org.semanticweb.owlapi.model.OWLObjectVisitorEx)
	 */
	@Override
	public <O> O accept(OWLObjectVisitorEx<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.HasAnnotations#getAnnotations()
	 */
	@Override
	public Set<OWLAnnotation> getAnnotations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.cs.owl.owlapi.HasIncrementalSignatureGenerationSupport#addSignatureEntitiesToSet(java.util.Set)
	 */
	@Override
	public void addSignatureEntitiesToSet(Set<OWLEntity> entities) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.cs.owl.owlapi.HasIncrementalSignatureGenerationSupport#addAnonymousIndividualsToSet(java.util.Set)
	 */
	@Override
	public void addAnonymousIndividualsToSet(Set<OWLAnonymousIndividual> anons) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect#getPointcut()
	 */
	@Override
	public Set<OWLObject> getPointcut() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.cs.owl.owlapi.OWLObjectImplWithoutEntityAndAnonCaching#index()
	 */
	@Override
	protected int index() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see uk.ac.manchester.cs.owl.owlapi.OWLObjectImplWithoutEntityAndAnonCaching#compareObjectOfSameType(org.semanticweb.owlapi.model.OWLObject)
	 */
	@Override
	protected int compareObjectOfSameType(OWLObject object) {
		// TODO Auto-generated method stub
		return 0;
	}



}
