package de.fuberlin.csw.aspectowl.renderer;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.parser.AspectOrientedFunctionalSyntaxDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLDocumentFormatFactory;
import org.semanticweb.owlapi.model.OWLStorer;
import org.semanticweb.owlapi.util.OWLStorerFactoryImpl;

import javax.annotation.Nonnull;

public class AspectOWLFunctionalSyntaxStorerFactory extends OWLStorerFactoryImpl {

    private static final long serialVersionUID = 4225557529363900763L;

    private OWLOntologyAspectManager am;

    public AspectOWLFunctionalSyntaxStorerFactory(OWLOntologyAspectManager am) {
        super(new AspectOrientedFunctionalSyntaxDocumentFormatFactory());
        this.am = am;
    }

    @Nonnull
    @Override
    public OWLStorer createStorer() {
        return new AspectOWLFunctionalSyntaxStorer(am);
    }
}
