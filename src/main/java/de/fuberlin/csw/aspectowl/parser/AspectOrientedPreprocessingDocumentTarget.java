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
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.io.StringDocumentSource;
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

	private static final String aspectIRI = "http://www.corporate-semantic-web.de/ontologies/aspect/owl";
	private static final String aspectIriAsRegex = "http:\\/\\/www\\.corporate-semantic-web\\.de\\/ontologies\\/aspect\\/owl";
	
	private static final Pattern prefixPattern = Pattern.compile("(?m)^Prefix(\\s*?)\\((\\s*)?(.*?:)=\\<" + aspectIriAsRegex + "#\\>(\\s*)?\\)$");
	
	private OWLOntologyDocumentTarget originalTarget;
	private String processedContent;
	
	private MyOutputStream myOutputStream;
	
	/**
	 * 
	 */
	public AspectOrientedPreprocessingDocumentTarget(OWLOntologyDocumentTarget originalTarget) {
		this.originalTarget = originalTarget;
		this.myOutputStream = new MyOutputStream(this);
	}
	
	/*
	 * Triggered similar to the (first) call for preprocessing
	 */
	private void init() {
		
		String buf = this.originalTarget.toString();
		
		Pattern importPattern = Pattern.compile("(?m)^Import(\\s*?)\\((\\s*?)\\<" + aspectIriAsRegex + "\\>(\\s*?)\\)$");
		
		Matcher importMatcher = importPattern.matcher(buf);
		
		// add the import statement if not already available
		if (! importMatcher.find())
		{
			String ontologyStartRegex = "Ontology(\\s*)?\\((\\s*)?(\\<.*\\>?)(\\s\\<.*?\\>)?";
			String importSyntax = "Import ( <" + aspectIRI + "> )";
			
			Pattern ontologyStartPattern = Pattern.compile(ontologyStartRegex);
			Matcher ontologyStartMatcher = ontologyStartPattern.matcher(buf);
			
			if (ontologyStartMatcher.find())
			{
				buf = buf.replaceAll(ontologyStartRegex, ontologyStartMatcher.group(0) + " " + importSyntax);
			}
		}
		
		Matcher prefixMatcher = prefixPattern.matcher(buf);
		
		if (prefixMatcher.find())
		{
			Matcher aspectPrefixMatcher = prefixPattern.matcher(prefixMatcher.group(0));
			
			if (aspectPrefixMatcher.matches())
			{
				String aspectPrefix = aspectPrefixMatcher.group(3);
				buf = buf.replaceAll(aspectPrefix + "hasAspect", "<" + aspectIRI + "/hasAspect>");
			}
		}
		
		this.processedContent = buf;
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isWriterAvailable()
	 */
	@Override
	public boolean isWriterAvailable() {
		System.out.println("isWriterAvailable");
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getWriter()
	 */
	@Override
	public Writer getWriter() throws IOException {
		System.out.println("getWriter");
		
		StringWriter stringWriter = new StringWriter();
		
		synchronized (this) {
			if (this.processedContent == null)
				init();
		}
		
		stringWriter.write(processedContent);
		
		return stringWriter;
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
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		
		synchronized (this) {
			if (this.processedContent == null)
				init();
		}
		
		buf.write(this.processedContent.getBytes(StandardCharsets.UTF_8));
		
		return buf;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isDocumentIRIAvailable()
	 */
	@Override
	public boolean isDocumentIRIAvailable() {
		System.out.println("isDocumentIRIAvailable");
		return this.originalTarget.isDocumentIRIAvailable();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getDocumentIRI()
	 */
	@Override
	public IRI getDocumentIRI() {
		System.out.println("getDocumentIRI");
		return this.originalTarget.getDocumentIRI();
	}

	private void processContent() throws IOException {

		String originalContent = this.myOutputStream.toString();
		
		StringDocumentSource orig = new StringDocumentSource(originalContent);
		AspectOrientedPreprocessingDocumentSource ds = new AspectOrientedPreprocessingDocumentSource(orig);
		Reader processReader = ds.getReader();
		
		String processedContent = IOUtils.toString(processReader);
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
