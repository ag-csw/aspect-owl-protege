package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.owlapi.model.*;
import de.fuberlin.csw.aspectowl.owlapi.vocab.AspectOWLVocabulary;
import org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxObjectRenderer;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.vocab.OWLXMLVocabulary;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


public class AspectFunctionalSyntaxObjectRenderer extends FunctionalSyntaxObjectRenderer implements OWLAspectVisitor, OWLAspectAxiomVisitor {

    private OWLOntologyAspectManager am;
    private Writer writer;

    /**
     * @param ontology the ontology
     * @param writer
     */
    public AspectFunctionalSyntaxObjectRenderer(@Nonnull OWLOntology ontology, Writer writer) {
        super(ontology, writer);
        this.writer = writer;
        am = OWLOntologyAspectManager.instance();
    }

    @Override
    protected void writeAxiomStart(@Nonnull OWLXMLVocabulary v, @Nonnull OWLAxiom axiom) {
        super.writeAxiomStart(v, axiom);
        writeAspects(axiom);
    }

    protected void writeAspects(@Nonnull OWLAxiom ax) {
        for (OWLAspect aspect: getSortedAspects(ax)) {
            aspect.accept(this);
            writeSpace();
        }
    }

    @Nonnull
    private List<OWLAspect> getSortedAspects(OWLAxiom ax) {
        return CollectionFactory.sortOptionally(am.getAssertedAspects(ont, ax));
    }

    @Override
    public void visit(@Nonnull OWLAspect aspect) {
        write(AspectOWLVocabulary.ASPECT);
        writeOpenBracket();
        for (OWLAnnotation anno : CollectionFactory.getCopyOnRequestSet(aspect.getAnnotations())) {
            anno.accept(this);
            writeSpace();
        }

        aspect.getAdvice().accept(this);
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
