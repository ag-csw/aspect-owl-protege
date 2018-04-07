/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
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

	private OWLOntologyAspectManager am;

	/**
	 */
	public AspectOrientedOWLFunctionalSyntaxParserFactory(OWLOntologyAspectManager am) {
		super(new AspectOrientedFunctionalSyntaxDocumentFormatFactory());
		this.am = am;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLParserFactory#createParser()
	 */
	@Override
	public OWLParser createParser() {
		return new AspectOrientedOWLFunctionalSyntaxOWLParser(am);
	}
	
}
