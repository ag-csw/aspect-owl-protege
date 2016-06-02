/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import java.io.Writer;

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.AbstractOWLStorer;

/**
 * @author ralph
 */
public class AspectOrientedOWLFunctionalSyntaxOWLStorer extends AbstractOWLStorer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3653614869728991886L;

	public AspectOrientedOWLFunctionalSyntaxOWLStorer() {
		System.out.println("created AspectOrientedOWLFunctionalSyntaxOWLStorer");
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.model.OWLStorer#canStoreOntology(org.semanticweb.owlapi.model.OWLDocumentFormat)
	 */
	@Override
	public boolean canStoreOntology(OWLDocumentFormat ontologyFormat) {
		return ontologyFormat instanceof AspectOrientedFunctionalSyntaxDocumentFormat;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.util.AbstractOWLStorer#storeOntology(org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.io.OWLOntologyDocumentTarget, org.semanticweb.owlapi.model.OWLDocumentFormat)
	 */
	@Override
	public void storeOntology(OWLOntology ontology, OWLOntologyDocumentTarget originalDocumentTarget, OWLDocumentFormat format)
			throws OWLOntologyStorageException {
		
		super.storeOntology(ontology, new AspectOrientedPreprocessingDocumentTarget(originalDocumentTarget), format);
	}
	
	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.util.AbstractOWLStorer#storeOntology(org.semanticweb.owlapi.model.OWLOntology, java.io.Writer, org.semanticweb.owlapi.model.OWLDocumentFormat)
	 */
	@Override
	protected void storeOntology(OWLOntology ontology, Writer writer, OWLDocumentFormat format)
			throws OWLOntologyStorageException {
		
		OWLFunctionalSyntaxRenderer renderer = new OWLFunctionalSyntaxRenderer();
		renderer.render(ontology, writer);
		
		
	}

}
