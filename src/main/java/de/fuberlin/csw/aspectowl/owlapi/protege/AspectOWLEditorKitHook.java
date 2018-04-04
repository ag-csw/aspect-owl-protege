/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAxiomInstance;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedFunctionalSyntaxDocumentFormat;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;
import de.fuberlin.csw.aspectowl.protege.editor.core.ui.AspectButton;
import de.fuberlin.csw.aspectowl.protege.editor.core.ui.OWLAspectIcon;
import de.fuberlin.csw.aspectowl.protege.views.AspectAssertionPanel;
import de.fuberlin.csw.aspectowl.renderer.AspectOWLFunctionalSyntaxStorerFactory;
import javassist.*;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.renderer.OWLClassIcon;
import org.protege.editor.owl.ui.renderer.OWLIconProviderImpl;
import org.protege.editor.owl.ui.renderer.context.DefinedClassChecker;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.*;

/**
 * @author ralph
 */
public class AspectOWLEditorKitHook extends EditorKitHook implements WeavingHook {

	private static final Logger log = LoggerFactory.getLogger(AspectOWLEditorKitHook.class);

	private final HashSet<String> frameSectionRowClassesForAspectButtons = new HashSet<>();
	private final HashSet<String> propertyCharacteristicsViewComponentClassesForAspectButtons = new HashSet<>();
	private final HashSet<String> axiomTypeClasses = new HashSet<>();

	private static OWLEditorKit editorKit;

	private static final OWLOntologyAspectManager am = OWLOntologyAspectManager.instance();

	private static DefinedClassChecker definedClassChecker;

	private static final Icon primitiveAspectClassIcon = new OWLAspectIcon(OWLClassIcon.Type.PRIMITIVE);

	private static final Icon definedAspectClassIcon = new OWLAspectIcon(OWLClassIcon.Type.DEFINED);


	/**
	 * 
	 */
	public AspectOWLEditorKitHook() {
		// Add classes that need to be woven here. Used to be more than one, hence the sets. Right now it's only one for
		// each type, but could change again. TODO remove sets for single woven classes, make them simple member instead.
		frameSectionRowClassesForAspectButtons.add("org.protege.editor.owl.ui.framelist.OWLFrameList");
		propertyCharacteristicsViewComponentClassesForAspectButtons.add("org.protege.editor.owl.ui.view.objectproperty.OWLObjectPropertyCharacteristicsViewComponent");
//		axiomTypeClasses.add("org.semanticweb.owlapi.model.AxiomType$1");
		axiomTypeClasses.add("org.semanticweb.owlapi.model.AxiomType$1");
		axiomTypeClasses.add("org.semanticweb.owlapi.model.AxiomType$2");
	}

	@Override
	protected void setup(EditorKit editorKit) {
		AspectOWLEditorKitHook.editorKit = ((OWLEditorKit)editorKit);

		if (definedClassChecker == null) {
			definedClassChecker = cls -> {
				for (OWLOntology ont : ((OWLEditorKit) editorKit).getOWLModelManager().getActiveOntologies()) {
					if (isDefined(cls, ont)) {
						return true;
					}
				}
				return false;
			};
		}

		super.setup(editorKit);
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

		mm.addOntologyChangeListener(am);

		OWLOntologyManager om = mm.getOWLOntologyManager();

		om.getOntologyStorers().add(new AspectOWLFunctionalSyntaxStorerFactory());

		mm.addIOListener(new AspectOrientedOntologyPreSaveChecker(om));

		OWLIconProviderImpl.class.getClass();

	}

	/* (non-Javadoc)
	 * @see org.protege.editor.core.Disposable#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		((OWLEditorKit)getEditorKit()).getModelManager().removeOntologyChangeListener(am);
	}

	@Override
	public void weave(WovenClass wovenClass) {

		String className = wovenClass.getClassName();

		System.out.format("Weaving class: %s,\tClassloader: %s\n", className, wovenClass.getBundleWiring().getClassLoader().getClass().getName());

		// add aspect buttons to frame section rows
		if (frameSectionRowClassesForAspectButtons.contains(className)) {

			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));


			try {
				CtClass ctClass = pool.getCtClass(className);

//				CtMethod ctMethod = ctClass.getMethod("getAdditionalButtons", "()Ljava/util/List;"); // throws NotFoundException if method does not exist
				CtMethod ctMethod = ctClass.getMethod("getButtons", "(Ljava/lang/Object;)Ljava/util/List;"); // throws NotFoundException if method does not exist

				CtClass declaringClass = ctMethod.getDeclaringClass();

				if (declaringClass != ctClass) {
					ctMethod = CtNewMethod.copy(ctMethod, ctClass, null);
					ctClass.addMethod(ctMethod);
				}

				ctMethod.insertAfter("if (value instanceof org.protege.editor.owl.ui.frame.OWLFrameSectionRow) return de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.getButtonsWithAspectButton($_, (org.protege.editor.owl.ui.frame.OWLFrameSectionRow)value);");

				byte[] bytes = ctClass.toBytecode();
				ctClass.detach();
				wovenClass.setBytes(bytes);

				wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

			} catch (Throwable t) {
//				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
		} else if (className.equals("org.protege.editor.owl.model.io.OntologyLoader")) {

			// Each time an ontology is loaded, Protege creates a new OWLOntologyManager. This ontology manager is used
			// for the loading process. After the ontology (and potential imports) are loaded, the ontologies are copied
			// to the main ontology manager (the one stored in the single OWLModelManager instance). Then, the loading
			// OWLOntologyManger is discarded. Anyway, we need to add our ParserFactory to each loading ontology manager.

			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));


			try {
				CtClass ctClass = pool.getCtClass(className);

				CtMethod ctMethod = ctClass.getMethod("loadOntologyInternal", "(Ljava/net/URI;)Ljava/util/Optional;"); // throws NotFoundException if method does not exist

				CtClass declaringClass = ctMethod.getDeclaringClass();

				if (declaringClass != ctClass) {
					ctMethod = CtNewMethod.copy(ctMethod, ctClass, null);
					ctClass.addMethod(ctMethod);
				}

				ctMethod.insertAt(90,"de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.addAspectOWLParser(loadingManager);");

				byte[] bytes = ctClass.toBytecode();
				ctClass.detach();
				wovenClass.setBytes(bytes);

				wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

			} catch (Throwable t) {
//				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
		} else if (className.equals("org.protege.editor.owl.ui.OntologyFormatPanel")) {

			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));


			try {
				CtClass ctClass = pool.getCtClass(className);

				CtConstructor ctConstructor = ctClass.getConstructor("()V");
				ctConstructor.insertAt(39, "de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.addOntologyFormat(formats);");

				CtMethod ctMethod = ctClass.getMethod("isFormatOk", "(Lorg/protege/editor/owl/OWLEditorKit;Lorg/semanticweb/owlapi/model/OWLDocumentFormat;)Z"); // throws NotFoundException if method does not exist
				ctMethod.insertBefore("if (!(de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.alternativeFormatIfAspectOriented(format))) return false;");

				byte[] bytes = ctClass.toBytecode();
				ctClass.detach();
				wovenClass.setBytes(bytes);

				wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

			} catch (Throwable t) {
//				System.out.format("Weaving failed for class %s: %s.\n", className, t.getMessage());
			}
		} else if (className.equals("org.protege.editor.owl.ui.renderer.OWLIconProviderImpl")) {

			// Mark classes that are aspects using a differently colored icon

			ClassPool pool = ClassPool.getDefault();
			pool.appendSystemPath();
			pool.appendClassPath(new ClassClassPath(AspectOWLEditorKitHook.class));

			pool.insertClassPath(new ByteArrayClassPath(wovenClass.getClassName(), wovenClass.getBytes()));

			try {
				CtClass ctClass = pool.getCtClass(className);

				CtMethod ctMethod = ctClass.getMethod("visit", "(Lorg/semanticweb/owlapi/model/OWLClass;)V"); // throws NotFoundException if method does not exist

				ctMethod.insertBefore("icon = de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook.getIcon(owlClass); if (icon != null) return;");

				byte[] bytes = ctClass.toBytecode();
				ctClass.detach();
				wovenClass.setBytes(bytes);

				wovenClass.getDynamicImports().add("de.fuberlin.csw.aspectowl.owlapi.protege");

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
//		} else if (className.equals("org.semanticweb.owlapi.model.AxiomType$1")) {
//			try {
//				BundleWiring bundleWiring = wovenClass.getBundleWiring();
//				byte[] axiomTypesClassBytes = IOUtils.toByteArray(bundleWiring.getClassLoader().getResourceAsStream("org/semanticweb/owlapi/model/AxiomType.class"));
//				AspectOWLPostClassLoadingWeavingHelper.reweaveAxiomTypeClass(this, "org.semanticweb.owlapi.model.AxiomType", bundleWiring, axiomTypesClassBytes);
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
//		} else if (OWLObject.class.isAssignableFrom(wovenClass.getDefinedClass())) {
//			System.out.println("OWLObject subclass: " + className);
//			// do proxying
//		}

	}

//	private static final OWLAspect testAspect = new OWLNamedAspectImpl(IRI.create("http://www.example.org/aspectowl/FunnyAspect"));

	/**
	 * @param original
	 * @return
	 */
	@SuppressWarnings("unused")
	public static List<MListButton> getButtonsWithAspectButton(List<MListButton> original, OWLFrameSectionRow frameSectionRow) {

		for(MListButton button : original) {
			if (button instanceof AspectButton) {
				// sometimes classes get woven multiple times, make sure not to add another aspect button
				return original;
			}
		}

        List<MListButton> additionalButtons = new ArrayList<>(original); // original may be an immutable list, so we need to create a mutable clone

		OWLAxiom axiom = frameSectionRow.getAxiom();
		OWLOntology ontology = frameSectionRow.getOntology();

		AspectButton button = new AspectButton(axiom, ontology);
		button.setActionListener(e -> {

			//			if (!am.hasAssertedAspects(axiom)) {
//                am.addAspect(testAspect, axiom); // for testing
//            } else {
//            	am.removeAssertedAspect(axiom, testAspect);
//			}

			AspectAssertionPanel aspectAssertionPanel = new AspectAssertionPanel(editorKit);
			aspectAssertionPanel.setAxiom(new OWLAxiomInstance(axiom, ontology));
			new UIHelper(editorKit).showDialog("Aspects for " + axiom.getAxiomType().toString() + " axiom", aspectAssertionPanel, JOptionPane.CLOSED_OPTION);
			aspectAssertionPanel.dispose();


//			editorKit.getModelManager().fireEvent(EventType.ACTIVE_ONTOLOGY_CHANGED);
		});
		additionalButtons.add(button);

		return additionalButtons;
	}

	@SuppressWarnings("unused")
	public static void addAspectOWLParser(OWLOntologyManager man) {
		man.getOntologyParsers().add(new AspectOrientedOWLFunctionalSyntaxParserFactory());
	}

	@SuppressWarnings("unused")
	public static void addOntologyFormat(List<OWLDocumentFormat> formats) {
		for (OWLDocumentFormat format : formats) {
			if (format instanceof AspectOrientedFunctionalSyntaxDocumentFormat) {
				return;
			}
		}
		formats.add(new AspectOrientedFunctionalSyntaxDocumentFormat());
	}

	@SuppressWarnings("unused")
	public static boolean alternativeFormatIfAspectOriented(OWLDocumentFormat format) {
		if (format instanceof AspectOrientedFunctionalSyntaxDocumentFormat
				|| !(am.hasAspects(editorKit.getModelManager().getActiveOntology()))) {
			return true;
		}

		int userSaysOk = JOptionPane.showConfirmDialog(editorKit.getOWLWorkspace(),
				String.format("The ontology contains aspects. The '%s' format loses all information about aspects.  We highly recommend to use the 'OWL Functional Syntax with Aspect-Oriented Extensions' format.  Continue anyway (you will lose information)?", format),
				"Warning",
				JOptionPane.YES_NO_OPTION);
		return userSaysOk == JOptionPane.YES_OPTION;
	}

	public static Icon getIcon(OWLClass owlClass) {
		if (am.isAspectInOntology(owlClass, editorKit.getModelManager().getActiveOntologies())) {
			if(definedClassChecker.isDefinedClass(owlClass)) {
				return definedAspectClassIcon;
			}
			else {
				return primitiveAspectClassIcon;
			}
		}
		return null;
	}

	private static boolean isDefined(OWLClass owlClass, OWLOntology ontology) {
		if (EntitySearcher.isDefined(owlClass, ontology)) {
			return true;
		}
		Set<OWLDisjointUnionAxiom> axioms = ontology.getDisjointUnionAxioms(owlClass);
		return !axioms.isEmpty();
	}

}
