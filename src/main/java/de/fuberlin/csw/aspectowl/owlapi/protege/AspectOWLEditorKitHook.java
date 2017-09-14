/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import org.eclipse.core.internal.registry.osgi.Activator;
import org.eclipse.core.internal.registry.osgi.OSGIUtils;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fuberlin.csw.aspectowl.owlapi.model.impl.AspectOWLEntityFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;

import java.lang.reflect.Array;
import java.util.Hashtable;

/**
 * @author ralph
 */
public class AspectOWLEditorKitHook extends EditorKitHook implements WeavingHook {

	private static final Logger log = LoggerFactory.getLogger(AspectOWLEditorKitHook.class);
	
	
	/**
	 * 
	 */
	public AspectOWLEditorKitHook() {
		// TODO Auto-generated constructor stub
	}

	/* Sneaks in our preprocessor for aspect-oriented ontologies
	 * @see org.protege.editor.core.plugin.ProtegePluginInstance#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		
		log.info("Initializing Aspect-Oriented OWL plug-in.");

		PluginUtilities.getInstance().getApplicationContext().registerService(WeavingHook.class, new AspectOWLEditorKitHook(), new Hashtable<>());

//		OWLEditorKit kit = (OWLEditorKit)getEditorKit();
//
		OWLModelManager mm = ((OWLEditorKit)getEditorKit()).getOWLModelManager();
//
////		mm.addListener(event -> {
////			switch (event.getType()) {
////			}
////		});
//
//		mm.setOWLEntityFactory(new AspectOWLEntityFactory(mm));
//
		OWLOntologyManager om = mm.getOWLOntologyManager();
//
//		om.addOntologyChangeListener(changes -> changes.forEach(change -> {
//
//		}));

		PriorityCollection<OWLParserFactory> parsers = om.getOntologyParsers();
		parsers.add(new AspectOrientedOWLFunctionalSyntaxParserFactory());
		
		mm.addIOListener(new AspectOrientedOntologyPreSaveChecker(om));

		
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.core.Disposable#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void weave(WovenClass wovenClass) {
		System.out.format("Hello, woven class %s.\n", wovenClass.getClassName());
	}
}
