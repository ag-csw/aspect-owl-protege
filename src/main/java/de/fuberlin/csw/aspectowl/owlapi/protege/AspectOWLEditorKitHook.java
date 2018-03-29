/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.protege;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAxiomInstance;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLNamedAspectImpl;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOntologyPreSaveChecker;
import de.fuberlin.csw.aspectowl.protege.editor.core.ui.AspectButton;
import de.fuberlin.csw.aspectowl.protege.views.AspectAssertionPanel;
import javassist.*;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.PriorityCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
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
	private final HashSet<String> axiomTypeClasses = new HashSet<>();

	private static OWLEditorKit editorKit;

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

		mm.addOntologyChangeListener(OWLOntologyAspectManager.instance());

		OWLOntologyManager om = mm.getOWLOntologyManager();

		mm.addIOListener(new AspectOrientedOntologyPreSaveChecker(om));
	}

	/* (non-Javadoc)
	 * @see org.protege.editor.core.Disposable#dispose()
	 */
	@Override
	public void dispose() throws Exception {
		((OWLEditorKit)getEditorKit()).getModelManager().removeOntologyChangeListener(OWLOntologyAspectManager.instance());
	}

	@Override
	public void weave(WovenClass wovenClass) {

		String className = wovenClass.getClassName();

//		System.out.format("Weaving class: %s,\tClassloader: %s\n", className, wovenClass.getBundleWiring().getClassLoader().getClass().getName());

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

				ctMethod.insertAt(90,"loadingManager.getOntologyParsers().add(new de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory());");

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
            OWLOntologyAspectManager am = OWLOntologyAspectManager.instance();

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



}
