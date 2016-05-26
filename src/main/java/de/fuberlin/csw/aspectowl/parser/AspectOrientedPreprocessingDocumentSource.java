/**
 * 
 */
package de.fuberlin.csw.aspectowl.parser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.semanticweb.owlapi.io.FileDocumentSource;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLRuntimeException;

/**
 * @author ralph
 */
public class AspectOrientedPreprocessingDocumentSource implements OWLOntologyDocumentSource {

	private static final Pattern prefixPattern = Pattern.compile("(?m)^Prefix\\((.*?):=\\<http://www\\.corporate-semantic-web\\.de\\/ontologies\\/aspect\\/owl#\\>\\)$");
	private static final Pattern importPattern = Pattern.compile("(?m)^Import(<http://www.corporate-semantic-web.de/ontologies/aspect/owl>)$");
	
	private final OWLOntologyDocumentSource originalSource;
	private String processedContent;
	
	/**
	 * 
	 */
	public AspectOrientedPreprocessingDocumentSource(OWLOntologyDocumentSource originalSource) {
		this.originalSource = originalSource;
	}

	
	/*
	 * Lazily called on first invocation of getReader or getInputStream
	 */
	private void init() {
		try {
			// Read the whole thing into memory. We need to go through the lines several times anyway.
			String buf = IOUtils.toString(originalSource.getReader());
			
			Matcher importMatcher = importPattern.matcher(buf);
			boolean hasImport = importMatcher.find();
			
			Matcher prefixMatcher = prefixPattern.matcher(buf);
			boolean hasPrefixMapping = prefixMatcher.find();
			String prefix = hasPrefixMapping ? prefixMatcher.group(1) : null;
			
			boolean hasDeclarationAspects = buf.matches("Import(<http://www.corporate-semantic-web.de/ontologies/aspect/owl>)");
			
			// hier den Ersatz einfügen
			
//			BufferedReader in = new BufferedReader(new StringReader(buf));
//			
//			String line = null;
//			String lastLine = "";
//			
//			while ((line = in.readLine()) != null) {
//				if (line.isEmpty() && lastLine.startsWith(lastLine)) {
//					
//				}
//			}
//			
//			content.replaceAll("^(\\w+\\()Aspect\\((.*?)\\)(.*)", "\\1");
			
			processedContent = buf;
		} catch (IOException e) {
			throw new OWLRuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#isReaderAvailable()
	 */
	@Override
	public boolean isReaderAvailable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#getReader()
	 */
	@Override
	public Reader getReader() {
		synchronized (this) {
			if (processedContent == null)
				init();
		}

		return new StringReader(processedContent);
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#isInputStreamAvailable()
	 */
	@Override
	public boolean isInputStreamAvailable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		synchronized (this) {
			if (processedContent == null)
				init();
		}

		return new ByteArrayInputStream(processedContent.getBytes(StandardCharsets.UTF_8));
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#getDocumentIRI()
	 */
	@Override
	public IRI getDocumentIRI() {
		return originalSource.getDocumentIRI();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#getFormat()
	 */
	@Override
	public OWLDocumentFormat getFormat() {
		return originalSource.getFormat();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#isFormatKnown()
	 */
	@Override
	public boolean isFormatKnown() {
		return originalSource.isFormatKnown();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#getMIMEType()
	 */
	@Override
	public String getMIMEType() {
		return originalSource.getMIMEType();
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owlapi.io.OWLOntologyDocumentSource#isMIMETypeKnown()
	 */
	@Override
	public boolean isMIMETypeKnown() {
		return originalSource.isMIMETypeKnown();
	}
	
	public static void main(String[] args) {
		FileDocumentSource orig = new FileDocumentSource(new File("/Users/ralph/Documents/Diss/Ontologien/AspectOWL/owlapi_parser/minimal_example.aofn"));
		AspectOrientedPreprocessingDocumentSource ds = new AspectOrientedPreprocessingDocumentSource(orig);
		
		ds.getReader();
	}

}
