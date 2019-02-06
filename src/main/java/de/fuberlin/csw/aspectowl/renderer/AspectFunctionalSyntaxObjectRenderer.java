package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.owlapi.model.*;
import de.fuberlin.csw.aspectowl.owlapi.vocab.AspectOWLVocabulary;
import org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxObjectRenderer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


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
        for (OWLAspect aspect: getSortedAspects(ax)) {
            writeAspect(aspect);
        }
    }

    @Nonnull
    private List<OWLAspect> getSortedAspects(OWLAxiom ax) {
        return CollectionFactory.sortOptionally(am.getAssertedAspects(ont, ax));
    }

    private void writeAspect(@Nonnull OWLAspect aspect) {
        write(AspectOWLVocabulary.ASPECT);
        writeOpenBracket();

        for (OWLAnnotation anno : CollectionFactory.getCopyOnRequestSet(aspect.getAnnotations())) {
            anno.accept(this);
            writeSpace();
        }
        
        for (OWLAspect nestedAspect : aspect.getAspects()) {
        	nestedAspect.accept(this);
        	writeSpace();
        }

        aspect.accept(this);

        writeCloseBracket();
        writeSpace();
    }

    @Override
    public void visit(OWLAspectAssertionAxiom axiom) {

    }

    private void write(@Nonnull AspectOWLVocabulary v) {
        try {
            writer.write(v.getShortForm());
        } catch (IOException e) {
            throw new OWLRuntimeException(e);
        }
    }

}
