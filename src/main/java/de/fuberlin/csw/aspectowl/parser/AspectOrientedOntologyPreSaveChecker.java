/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ralph
 */
public class AspectOrientedOntologyPreSaveChecker extends IOListener {
	
	private static final Logger log = LoggerFactory.getLogger(AspectOrientedOntologyPreSaveChecker.class);

	private OWLOntologyManager om;
	
	/**
	 * 
	 */
	public AspectOrientedOntologyPreSaveChecker(OWLOntologyManager om) {
		this.om = om;
	}
	
	/**
	 * Checks whether there are aspects in the current ontology and invokes the
	 * translation to aspect syntax.
	 * 
	 * This gets called BEFORE the format selection dialog is displayes, which
	 * is a bit unfortunate, since we only need this of saving in OFN format.
	 * 
	 * Then again, we could ask the user if he/she wants to save in the aofn
	 * format if aspects are present.
	 */
	@Override
	public void beforeSave(IOListenerEvent event) {
		log.info("About to save ontology " + event.getOntologyID());
		
		OWLOntology ontologyToSave = om.getOntology(event.getOntologyID());
		
		// search ontology for occurence of aspects and intercept normal saving
	}

	@Override
	public void afterSave(IOListenerEvent event) {
		// do nothing
	}

	@Override
	public void beforeLoad(IOListenerEvent event) {
		// do nothing
	}

	@Override
	public void afterLoad(IOListenerEvent event) {
		// do nothing
	}

}
