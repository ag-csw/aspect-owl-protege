package de.fuberlin.csw.aspectowl.protege;

import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.protege.editor.core.ui.list.MList;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.semanticweb.owlapi.model.OWLAnnotation;

public class AddAspectActionListener implements ActionListener{
	
	private java.util.List<OWLAnnotation> items;
	private OWLEditorKit eKit;
	private MList mList;
	
	public AddAspectActionListener(OWLEditorKit eKit, MList mList, java.util.List<OWLAnnotation> items){
		this.items = items; 
		this.eKit = eKit;
		this.mList = mList;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		UIHelper uiHelper = new UIHelper(eKit);

		OWLAnnotationEditor annotationEditor = new OWLAnnotationEditor(
				eKit);
		int ret = uiHelper.showValidatingDialog("Select aspect to apply",
				annotationEditor.getEditorComponent(), null);

		if (ret == JOptionPane.OK_OPTION) {
			OWLAnnotation selectedAspectAnnotation = annotationEditor.getEditedObject();
		
			items.add((selectedAspectAnnotation));         
			mList.setListData(items.toArray());
		}
	}

}
