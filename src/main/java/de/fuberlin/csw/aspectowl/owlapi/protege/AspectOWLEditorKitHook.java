/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;
import de.fuberlin.csw.aspectowl.protege.editor.core.ui.AspectButton;
import de.fuberlin.csw.aspectowl.protege.views.AspectAnnotatedAxiomsFrameSection;
import javassist.*;
import javassist.bytecode.Descriptor;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.*;
import org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyDomainFrameSection;
import org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyRangeFrameSection;
import org.protege.editor.owl.ui.frame.annotationproperty.OWLSubAnnotationPropertyFrameSection;
import org.protege.editor.owl.ui.frame.cls.*;
import org.protege.editor.owl.ui.frame.dataproperty.*;
import org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDefinitionFrameSection;
import org.protege.editor.owl.ui.frame.individual.*;
import org.protege.editor.owl.ui.frame.objectproperty.*;
import org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author ralph
 */
public class AspectOWLEditorKitHook extends EditorKitHook implements WeavingHook {

	private static final Logger log = LoggerFactory.getLogger(AspectOWLEditorKitHook.class);

	private final HashSet<String> aspectButtonClasses = new HashSet<>();

	/**
	 * 
	 */
	public AspectOWLEditorKitHook() {
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLDifferentIndividualAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyDomainFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.InferredAxiomsFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLObjectPropertyAssertionAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.SWRLRuleFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLClassAssertionAxiomMembersSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDefinitionFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.AxiomListFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.InheritedAnonymousClassesFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.OWLAnnotationsFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyRangeFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLEquivalentDataPropertiesFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLInverseObjectPropertiesAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLKeyAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointUnionAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLDataPropertyAssertionAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLSubAnnotationPropertyFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeDataPropertyAssertionFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDisjointDataPropertiesFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyRangeFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLDisjointObjectPropertiesAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyRangeFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLPropertyChainAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.OWLGeneralClassAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeObjectPropertyAssertionFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointClassesAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSectionRow");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLEquivalentObjectPropertiesAxiomFrameSectionRow");
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

		if (aspectButtonClasses.contains(className)) {

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
				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
		}

	}

	/**
	 * @param original
	 * @return
	 */
	public static List<MListButton> getButtonsWithAspectButton(List<MListButton> original) {
		List<MListButton> additionalButtons = new ArrayList<>(original); // original may be an immutable list, so we need to create a mutable clone
		AspectButton button = new AspectButton();
		button.setActionListener(e -> {
//			OWLAxiom selectedAxiom = ((OWLFrameSectionRow)button.getRowObject()).getAxiom();
//			log.info(selectedAxiom.toString());
			log.info(button.getRowObject().getClass().getName());
		});
		additionalButtons.add(button);

		return additionalButtons;
	}

}
