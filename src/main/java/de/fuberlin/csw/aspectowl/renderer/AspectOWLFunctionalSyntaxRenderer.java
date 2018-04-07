package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import org.semanticweb.owlapi.io.AbstractOWLRenderer;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.io.OWLRendererIOException;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;

public class AspectOWLFunctionalSyntaxRenderer extends AbstractOWLRenderer {

    private OWLOntologyAspectManager am;

    public AspectOWLFunctionalSyntaxRenderer(OWLOntologyAspectManager am) {
        this.am = am;
    }

    @Override
    public void render(@Nonnull OWLOntology ontology, @Nonnull Writer writer) throws OWLRendererException {
        try {
            AspectFunctionalSyntaxObjectRenderer ren = new AspectFunctionalSyntaxObjectRenderer(ontology, writer, am);
            ontology.accept(ren);
            writer.flush();
        } catch (IOException e) {
            throw new OWLRendererIOException(e);
        }

    }
}
