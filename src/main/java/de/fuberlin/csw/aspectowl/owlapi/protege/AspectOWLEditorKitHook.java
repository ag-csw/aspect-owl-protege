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
		aspectButtonClasses.add("de.fuberlin.csw.aspectowl.protege.views.AspectAnnotatedAxiomsFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDisjointDataPropertiesFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLDifferentIndividualsAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.OWLAnnotationFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.datatype.OWLDatatypeDefinitionFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLEquivalentObjectPropertiesAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLClassAssertionAxiomTypeFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyRangeFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLSubObjectPropertyAxiomSuperPropertyFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeDataPropertyAssertionFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointUnionAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.SWRLRulesFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.property.AbstractPropertyDomainFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLDataPropertyDomainFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyDomainFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyRangeFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLSameIndividualsAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLDataPropertyAssertionAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.InferredAxiomsFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLObjectPropertyAssertionAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.AxiomListFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLSubDataPropertyAxiomSuperPropertyFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLInverseObjectPropertiesAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLSubAnnotationPropertyFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.InheritedAnonymousClassesFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLKeySection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.annotationproperty.OWLAnnotationPropertyDomainFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.individual.OWLNegativeObjectPropertyAssertionFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLObjectPropertyRangeFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.OWLGeneralClassAxiomsFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLClassGeneralClassAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.AbstractOWLClassAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLDisjointClassesAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.cls.OWLClassAssertionAxiomMembersSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLDisjointObjectPropertiesFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.dataproperty.OWLEquivalentDataPropertiesFrameSection");
		aspectButtonClasses.add("org.protege.editor.owl.ui.frame.objectproperty.OWLPropertyChainAxiomFrameSection");
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
			OWLAxiom selectedAxiom = ((OWLFrameSectionRow)button.getRowObject()).getAxiom();
			log.info(selectedAxiom.toString());
		});
		additionalButtons.add(button);

		return additionalButtons;
	}

}
