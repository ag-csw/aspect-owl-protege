/**
 * 
 */
package de.fuberlin.csw.aspectowl.protege.views;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.hierarchy.OWLAnnotationPropertyHierarchyProvider;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.selector.OWLAnnotationPropertySelectorPanel;

/**
 * @author ralph
 * @TODO Modify the hierarchyprovider such that it only returns subproperties
 * of the hasAspect property.
 */
public class AspectSelectorDialog extends OWLAnnotationEditor {

	/**
	 * @param owlEditorKit
	 */
	public AspectSelectorDialog(OWLEditorKit owlEditorKit) {
		super(owlEditorKit);
	}
	
	/**
	 * Fills the annotation property selector only with aspect annotation
	 * properties
	 */
	@Override
	protected OWLAnnotationPropertySelectorPanel createAnnotationPropertySelector() {
        final OWLModelManager mngr = owlEditorKit.getOWLModelManager();
        final OWLAnnotationPropertyHierarchyProvider hp =
                mngr.getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider();
        
        
        return new OWLAnnotationPropertySelectorPanel(owlEditorKit, true, hp);
	}
	
}
