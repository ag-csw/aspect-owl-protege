/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAspectImplDelegate;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLNamedAspectImpl;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;
import de.fuberlin.csw.aspectowl.protege.editor.core.ui.AspectButton;
import javassist.*;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

/**
 * @author ralph
 */
public class AspectOWLEditorKitHook extends EditorKitHook implements WeavingHook {

	private static final Logger log = LoggerFactory.getLogger(AspectOWLEditorKitHook.class);

	private final HashSet<String> frameSectionRowClassesForAspectButtons = new HashSet<>();
	private final HashSet<String> propertyCharacteristicsViewComponentClassesForAspectButtons = new HashSet<>();

	/**
	 * 
	 */
	public AspectOWLEditorKitHook() {
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLDifferentIndividualAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyDomainFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.InferredAxiomsFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLObjectPropertyAssertionAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.SWRLRuleFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLClassAssertionAxiomMembersSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDefinitionFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.AxiomListFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.InheritedAnonymousClassesFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.OWLAnnotationsFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyRangeFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.dataproperty.OWLEquivalentDataPropertiesFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.dataproperty.OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLInverseObjectPropertiesAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLKeyAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointUnionAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLDataPropertyAssertionAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLSubAnnotationPropertyFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeDataPropertyAssertionFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDisjointDataPropertiesFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyRangeFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLDisjointObjectPropertiesAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyRangeFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLPropertyChainAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.OWLGeneralClassAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeObjectPropertyAssertionFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointClassesAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSectionRow");
//		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.objectproperty.OWLEquivalentObjectPropertiesAxiomFrameSectionRow");
		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow");

		propertyCharacteristicsViewComponentClassesForAspectButtons.add("org.protege.editor.owl.ui.view.objectproperty.OWLObjectPropertyCharacteristicsViewComponent");
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

		OWLModelManager mm = ((OWLEditorKit)getEditorKit()).getOWLModelManager();

		OWLOntologyManager om = mm.getOWLOntologyManager();

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

		String className = wovenClass.getClassName();

		// add aspect buttons to frame section rows
		if (frameSectionRowClassesForAspectButtons.contains(className)) {

			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));


			try {
				CtClass ctClass = pool.getCtClass(className);

				CtMethod ctMethod = ctClass.getMethod("getAdditionalButtons", "()Ljava/util/List;"); // throws NotFoundException if method does not exist

				CtClass declaringClass = ctMethod.getDeclaringClass();

				if (declaringClass != ctClass) {
					ctMethod = CtNewMethod.copy(ctMethod, ctClass, null);
					ctClass.addMethod(ctMethod);
				}

				ctMethod.insertAfter("return de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.getButtonsWithAspectButton($_);");

				byte[] bytes = ctClass.toBytecode();
				ctClass.detach();
				wovenClass.setBytes(bytes);

				wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

				System.out.printf("    Hello, woven class %s.\n", className);

			} catch (Throwable t) {
//				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
		} else if (className.equals("org.protege.editor.owl.ui.view.dataproperty.OWLDataPropertyCharacteristicsViewComponent")) {
			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));


			try {
				CtClass ctClass = pool.getCtClass(className);

				CtMethod ctMethod = ctClass.getDeclaredMethod("initialiseView");

//				ctMethod.insertAfter();

			} catch (Throwable t) {
//				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
//		} else if (OWLObject.class.isAssignableFrom(wovenClass.getDefinedClass())) {
//			System.out.println("OWLObject subclass: " + className);
//			// do proxying
		}

	}

	/**
	 * @param original
	 * @return
	 */
	public static List<MListButton> getButtonsWithAspectButton(List<MListButton> original) {
		for(MListButton button : original) {
			if (button instanceof AspectButton) {
				// sometimes classes get woven multiple times, make sure not to add another aspect button
				return original;
			}
		}

        List<MListButton> additionalButtons = new ArrayList<>(original); // original may be an immutable list, so we need to create a mutable clone

        AspectButton button = new AspectButton();
		button.setActionListener(e -> {
            OWLOntologyAspectManager am = OWLOntologyAspectManager.instance();
			OWLAxiom selectedAxiom = ((OWLFrameSectionRow<Object, OWLAxiom, Object>)button.getRowObject()).getAxiom();
            if (am.getAssertedAspects(selectedAxiom).count() == 0) {
                am.addAspect(new OWLNamedAspectImpl(IRI.create("http://www.example.org/aspectowl/FunnyAspect")), selectedAxiom);
            }
		});
		additionalButtons.add(button);

		return additionalButtons;
	}

}
