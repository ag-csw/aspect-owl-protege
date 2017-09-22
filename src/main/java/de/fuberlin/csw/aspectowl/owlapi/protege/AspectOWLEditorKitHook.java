/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;
import de.fuberlin.csw.aspectowl.protege.views.AspectAnnotatedAxiomsFrameSection;
import javassist.*;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.list.MListAddButton;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.*;
import org.protege.editor.owl.ui.frame.annotationproperty.*;
import org.protege.editor.owl.ui.frame.cls.*;
import org.protege.editor.owl.ui.frame.dataproperty.*;
import org.protege.editor.owl.ui.frame.datatype.*;
import org.protege.editor.owl.ui.frame.individual.*;
import org.protege.editor.owl.ui.frame.objectproperty.*;
import org.protege.editor.owl.ui.frame.property.*;
import org.protege.editor.owl.ui.framelist.ExplainButton;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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

		ClassPool.getDefault();
		NotFoundException.class.getClass();
		CannotCompileException.class.getClass();
		CtClass.class.getClass();
		CtMethod.class.getClass();
		ByteArrayClassPath.class.getClass();

		PluginUtilities.getInstance().getApplicationContext().registerService(WeavingHook.class, this, new Hashtable<>());

//		pool.appendClassPath(new ClassClassPath(AbstractOWLFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(AspectAnnotatedAxiomsFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDisjointDataPropertiesFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDifferentIndividualsAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLAnnotationFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDatatypeDefinitionFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLEquivalentObjectPropertiesAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLClassAssertionAxiomTypeFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDataPropertyRangeFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLSubObjectPropertyAxiomSuperPropertyFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLNegativeDataPropertyAssertionFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDisjointUnionAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(SWRLRulesFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(AbstractPropertyDomainFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDataPropertyDomainFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLObjectPropertyDomainFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLAnnotationPropertyRangeFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLSameIndividualsAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDataPropertyAssertionAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(InferredAxiomsFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLObjectPropertyAssertionAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(AxiomListFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLSubDataPropertyAxiomSuperPropertyFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLInverseObjectPropertiesAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLSubAnnotationPropertyFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(InheritedAnonymousClassesFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLKeySection.class));
//		pool.appendClassPath(new ClassClassPath(OWLAnnotationPropertyDomainFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLNegativeObjectPropertyAssertionFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLObjectPropertyRangeFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLGeneralClassAxiomsFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLClassGeneralClassAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(AbstractOWLClassAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDisjointClassesAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLSubClassAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLEquivalentClassesAxiomFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLClassAssertionAxiomMembersSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLDisjointObjectPropertiesFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLEquivalentDataPropertiesFrameSection.class));
//		pool.appendClassPath(new ClassClassPath(OWLPropertyChainAxiomFrameSection.class));



//		Class clazz = AbstractOWLFrameSection.class;
//		ClassLoader classLoader = clazz.getClassLoader();

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

		ClassPool pool = ClassPool.getDefault();
		pool.appendSystemPath();
		pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

		pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));

		String className = wovenClass.getClassName();

		try {
			CtClass ctClass = pool.getCtClass(className);

			CtMethod ctMethod = ctClass.getDeclaredMethod("getAdditionalButtons"); // throws NotFoundException if not defined
			ctMethod.insertAfter("return de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.getButtonsWithAspectButton($_);");

			System.out.printf("    Hello, woven class %s.\n", className);

			byte[] bytes = ctClass.toBytecode();
			ctClass.detach();
			wovenClass.setBytes(bytes);

			wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

		} catch (Throwable t) {
			System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
		}

	}

	public static List<MListButton> getButtonsWithAspectButton(List<MListButton> original) {
		List<MListButton> additionalButtons = new ArrayList<>(original); // original may be an immutable list, so we need to create a mutable clone
		additionalButtons.add(new ExplainButton(e -> System.out.println("I do nothing.")));

		return additionalButtons;
	}
}
