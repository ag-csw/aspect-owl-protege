/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserFactoryImpl;

/**
 * @author ralph
 */
public class AspectOrientedOWLFunctionalSyntaxParserFactory extends OWLParserFactoryImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2617848554167933720L;

	/**
	 * @param format
	 */
	public AspectOrientedOWLFunctionalSyntaxParserFactory() {
		super(new AspectOrientedFunctionalSyntaxDocumentFormatFactory());
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLParserFactory#createParser()
	 */
	@Override
	public OWLParser createParser() {
		return new AspectOrientedOWLFunctionalSyntaxOWLParser();
	}
	
}