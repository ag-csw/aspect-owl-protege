package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.parser.AspectOrientedFunctionalSyntaxDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLStorer;
import org.semanticweb.owlapi.util.OWLStorerFactoryImpl;

import javax.annotation.Nonnull;

public class AspectOWLFunctionalSyntaxStorerFactory extends OWLStorerFactoryImpl {

    private static final long serialVersionUID = 4225557529363900763L;

    public AspectOWLFunctionalSyntaxStorerFactory() {
        super(new AspectOrientedFunctionalSyntaxDocumentFormatFactory());
    }

    @Nonnull
    @Override
    public OWLStorer createStorer() {
        return new AspectOWLFunctionalSyntaxStorer();
    }
}
