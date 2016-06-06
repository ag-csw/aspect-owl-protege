package de.fuberlin.csw.aspectowl.protege;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.protege.editor.core.ui.action.ProtegeAction;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLStorerFactory;
import org.semanticweb.owlapi.util.PriorityCollection;

import de.fuberlin.csw.aspectowl.parser.AspectOrientedFunctionalSyntaxDocumentFormat;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxParserFactory;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedOWLFunctionalSyntaxStorerFactory;

public class SaveAspectOrientedOntology extends ProtegeAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4833133243808118467L;

	private String fileName  = "ontology.aofn";
	private String directory = "";
	
	@Override
	public void initialise() throws Exception {
		
	}

	@Override
	public void dispose() throws Exception {
		
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		JFrame parentFrame = new JFrame();
		
		JFileChooser fc = new JFileChooser();
		
		if (! this.directory.equals(""))
		{
			fc.setCurrentDirectory( new File(this.directory) );
		}
		
		fc.setFileFilter( new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".aofn");
				}
			}

			@Override
			public String getDescription() {
				return "Aspect-Oriented Functional Notation (*.aofn)";
			}
			
		});
		
		fc.setSelectedFile( new File(this.fileName) );
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		fc.setFileHidingEnabled(true);
		fc.setMultiSelectionEnabled(false);
		fc.setControlButtonsAreShown(true);
		fc.setDragEnabled(true);
		
		int userSelection = fc.showSaveDialog(parentFrame);
		
		if (userSelection == JFileChooser.APPROVE_OPTION)
		{
			
			File fileToSave = fc.getSelectedFile();
			
			this.fileName  = fileToSave.getName();
			this.directory = fileToSave.getPath();
			
			//System.out.println("[S] : try to save as " + fileToSave.toString());
			
			if (fileToSave.exists())
			{
				int dialogResult = JOptionPane.showConfirmDialog(parentFrame,
						"The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_OPTION);
				
				if (dialogResult == JOptionPane.YES_OPTION) {
					saveActiveAspectOntology(fileToSave);
				}
				else {
					actionPerformed(event);
				}
			}
			else {
				saveActiveAspectOntology(fileToSave);
			}
			
		}
		
	}
	
	/**
	 * 
	 * @param fileToSave
	 */
	private void saveActiveAspectOntology(File fileToSave)
	{
		
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		
		PriorityCollection<OWLParserFactory> parsers = om.getOntologyParsers();
		parsers.add( new AspectOrientedOWLFunctionalSyntaxParserFactory() );
		
		PriorityCollection<OWLStorerFactory> storers = om.getOntologyStorers();
		storers.add( new AspectOrientedOWLFunctionalSyntaxStorerFactory() );
		
		OWLEditorKit editorKit = (OWLEditorKit) getEditorKit();
		
		OWLOntology activeOntology = editorKit.getModelManager().getActiveOntology();
		
		try {
			om.saveOntology(activeOntology, new AspectOrientedFunctionalSyntaxDocumentFormat(),
					new FileOutputStream( fileToSave ));
		}
		catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
