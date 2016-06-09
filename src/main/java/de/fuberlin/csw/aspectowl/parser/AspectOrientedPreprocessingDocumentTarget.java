/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.io.OWLOntologyDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * @author ralph
 */
public class AspectOrientedPreprocessingDocumentTarget implements OWLOntologyDocumentTarget {

	private static final String aspectIRI = "http://www.corporate-semantic-web.de/ontologies/aspect_owl";
	private static final String aspectIriAsRegex = "http:\\/\\/www\\.corporate-semantic-web\\.de\\/ontologies\\/aspect_owl";
	
	private static final Pattern prefixPattern = Pattern.compile("(?m)^Prefix(\\s*?)\\((\\s*)?(.*?:)=\\<" + aspectIriAsRegex + "#\\>(\\s*)?\\)$");
	
	private OWLOntologyDocumentTarget originalTarget;
	
	private String originalContent;
	private String processedContent;
	
	/**
	 * 
	 */
	public AspectOrientedPreprocessingDocumentTarget(OWLOntology ontology, OWLOntologyDocumentTarget originalTarget)
	{
		this.originalContent = ontology.toString();
		this.originalTarget  = originalTarget;
	}
	
	/*
	 * Triggered similar to the (first) call for preprocessing
	 */
	private void init() {

		String buf = this.originalContent;
		
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
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getWriter()
	 */
	@Override
	public Writer getWriter() throws IOException {
		
		synchronized (this) {
			if (this.processedContent == null)
				init();
		}
		
		String path = null;
		
		try {
			Field pathField = FileOutputStream.class.getDeclaredField("path");
			pathField.setAccessible(true);
			path = (String) pathField.get(this.originalTarget.getOutputStream());
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		OutputStream ostream = new FileOutputStream(path);
		ostream.write(this.processedContent.getBytes(StandardCharsets.UTF_8));
		
		return new OutputStreamWriter(ostream, StandardCharsets.UTF_8);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#isOutputStreamAvailable()
	 */
	@Override
	public boolean isOutputStreamAvailable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		
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
		return this.originalTarget.isDocumentIRIAvailable();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentTarget#getDocumentIRI()
	 */
	@Override
	public IRI getDocumentIRI() {
		return this.originalTarget.getDocumentIRI();
	}
	
}
