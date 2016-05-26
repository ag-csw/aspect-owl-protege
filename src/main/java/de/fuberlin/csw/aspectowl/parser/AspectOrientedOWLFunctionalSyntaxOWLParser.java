/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import java.io.IOException;
import java.io.Reader;

import org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParser;
import org.semanticweb.owlapi.io.AbstractOWLParser;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ralph
 */
public class AspectOrientedOWLFunctionalSyntaxOWLParser extends AbstractOWLParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3478225770820865746L;

	private static final Logger log = LoggerFactory.getLogger(AspectOrientedOWLFunctionalSyntaxOWLParser.class);
	
	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLParser#parse(org.semanticweb.owlapi.io.OWLOntologyDocumentSource, org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration)
	 */
	@Override
	public OWLDocumentFormat parse(OWLOntologyDocumentSource originalDocumentSource, OWLOntology ontology,
			OWLOntologyLoaderConfiguration configuration) throws IOException {

		
		AspectOrientedPreprocessingDocumentSource wrapperDocumentSource = new AspectOrientedPreprocessingDocumentSource(originalDocumentSource);
		
		OWLFunctionalSyntaxOWLParser functionalSyntaxParser = new OWLFunctionalSyntaxOWLParser();
		return functionalSyntaxParser.parse(wrapperDocumentSource, ontology, configuration);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLParser#getSupportedFormat()
	 */
	@Override
	public OWLDocumentFormatFactory getSupportedFormat() {
		return new AspectOrientedFunctionalSyntaxDocumentFormatFactory();
	}

	
}
