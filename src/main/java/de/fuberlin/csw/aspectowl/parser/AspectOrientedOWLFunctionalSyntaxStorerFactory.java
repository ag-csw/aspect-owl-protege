/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import org.semanticweb.owlapi.model.OWLStorer;
import org.semanticweb.owlapi.util.OWLStorerFactoryImpl;

/**
 * @author ralph
 */
public class AspectOrientedOWLFunctionalSyntaxStorerFactory extends OWLStorerFactoryImpl {

	/**
	 * 
	 */
	public AspectOrientedOWLFunctionalSyntaxStorerFactory() {
		super(new AspectOrientedFunctionalSyntaxDocumentFormatFactory());
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLStorerFactory#createStorer()
	 */
	@Override
	public OWLStorer createStorer() {
		return new AspectOrientedOWLFunctionalSyntaxOWLStorer();
	}

}
