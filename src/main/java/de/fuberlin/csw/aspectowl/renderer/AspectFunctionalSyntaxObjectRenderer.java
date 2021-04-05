package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspectAssertionAxiom;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspectAxiomVisitor;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.owlapi.vocab.AspectOWLVocabulary;
import org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxObjectRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;


public class AspectFunctionalSyntaxObjectRenderer extends FunctionalSyntaxObjectRenderer implements OWLAspectAxiomVisitor {

    private OWLOntologyAspectManager am;
    private Writer writer;

    /**
     * @param ontology the ontology
     * @param writer
     */
    public AspectFunctionalSyntaxObjectRenderer(@Nonnull OWLOntology ontology, Writer writer, OWLOntologyAspectManager am) {
        super(ontology, writer);
        this.writer = writer;
        this.am = am;
    }

    @Override
    protected void writeAxiomStart(@Nonnull OWLXMLVocabulary v, @Nonnull OWLAxiom axiom) {
        super.writeAxiomStart(v, axiom); // writes axiom name and annotations (and a whitespace at the end)
        writeAspects(axiom);
    }

    protected void writeAspects(@Nonnull OWLAxiom ax) {
        am.getAspectAssertionAxioms(ont, ax).forEach(this::writeAspect);
    }

    private void writeAspect(@Nonnull OWLAspectAssertionAxiom aspectAssertionAxiom) {
        write(AspectOWLVocabulary.ASPECT);
        writeOpenBracket();

        CollectionFactory.sortOptionally(CollectionFactory.getCopyOnRequestSet(aspectAssertionAxiom.getAnnotations())).forEach(anno -> {
            anno.accept(this);
            writeSpace();
        });

        CollectionFactory.sortOptionally(am.getAspectAssertionAxioms(ont, aspectAssertionAxiom)).forEach(nestedAspectAssertionAxiom -> {
            writeAspect(nestedAspectAssertionAxiom);
            writeSpace();
        });

        aspectAssertionAxiom.getAspect().accept(this);

        writeCloseBracket();
        writeSpace();
    }

    @Override
    public void visit(@Nonnull OWLAspectAssertionAxiom axiom) {
    }

    private void write(@Nonnull AspectOWLVocabulary v) {
        try {
            writer.write(v.getShortForm());
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

}
