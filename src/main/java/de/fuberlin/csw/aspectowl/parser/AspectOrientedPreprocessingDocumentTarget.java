/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLStorerFactory;
import org.semanticweb.owlapi.util.PriorityCollection;

/**
 * @author ralph
 */
public class AspectOrientedPreprocessingDocumentTarget implements OWLOntologyDocumentTarget {

	private OWLOntologyDocumentTarget originalTarget;
	private MyOutputStream myOutputStream;
	
	/**
	 * 
	 */
	public AspectOrientedPreprocessingDocumentTarget(OWLOntologyDocumentTarget originalTarget) {
		this.originalTarget = originalTarget;
		myOutputStream = new MyOutputStream(this);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isWriterAvailable()
	 */
	@Override
	public boolean isWriterAvailable() {
		System.out.println("isWriterAvailable");
		return false;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getWriter()
	 */
	@Override
	public Writer getWriter() throws IOException {
		System.out.println("getWriter");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isOutputStreamAvailable()
	 */
	@Override
	public boolean isOutputStreamAvailable() {
		System.out.println("isOutputStreamAvailable");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		System.out.println("getOutputStream");
		return null;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isDocumentIRIAvailable()
	 */
	@Override
	public boolean isDocumentIRIAvailable() {
		System.out.println("isDocumentIRIAvailable");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getDocumentIRI()
	 */
	@Override
	public IRI getDocumentIRI() {
		System.out.println("getDocumentIRI");
		return null;
	}

	private void processContent() throws IOException {

		String originalContent = myOutputStream.toString();
		
		// ersetzungen
		String processedContent = ""; // = ...
		
		new OutputStreamWriter(originalTarget.getOutputStream()).write(processedContent);
	}
	
	private class MyOutputStream extends ByteArrayOutputStream {
		
		private AspectOrientedPreprocessingDocumentTarget aspectOrientedPreprocessingDocumentTarget;
		
		/**
		 * 
		 */
		public MyOutputStream(AspectOrientedPreprocessingDocumentTarget aspectOrientedPreprocessingDocumentTarget) {
			this.aspectOrientedPreprocessingDocumentTarget = aspectOrientedPreprocessingDocumentTarget;
		}
		
		/**
		 * @see java.io.ByteArrayOutputStream#close()
		 */
		@Override
		public void close() throws IOException {
			aspectOrientedPreprocessingDocumentTarget.processContent();
			super.close();
		}
	}

	public static void main(String[] args) {
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		PriorityCollection<OWLParserFactory> parsers = om.getOntologyParsers();
		parsers.add(new AspectOrientedOWLFunctionalSyntaxParserFactory());
		
		PriorityCollection<OWLStorerFactory> storers = om.getOntologyStorers();
		storers.add(new AspectOrientedOWLFunctionalSyntaxStorerFactory());
		
		try {
			OWLOntology onto = om.loadOntologyFromOntologyDocument(new File("/Users/ralph/Documents/Development/Workspaces/workspace_eclipse/de.fuberlin.csw.aood.protege.aood_plugin/target/classes/input.owl"));
			om.saveOntology(onto, new FileOutputStream("bla.owl"));
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
