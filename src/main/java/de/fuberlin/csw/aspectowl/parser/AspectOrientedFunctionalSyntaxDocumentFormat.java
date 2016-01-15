/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import org.semanticweb.owlapi.formats.PrefixDocumentFormatImpl;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * @author ralph
 */
public class AspectOrientedFunctionalSyntaxDocumentFormat extends PrefixDocumentFormatImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2753197048778914910L;

	/**
	 * @see org.semanticweb.owlapi.formats.PrefixDocumentFormatImpl#getKey()
	 */
	@Override
	public String getKey() {
		return "OWL Functional Syntax with Aspect-Oriented Extensions";
	}
	
}
