package de.fuberlin.csw.aspectowl.protege.editor.core.ui.view.ontology;

import de.fuberlin.csw.aspectowl.owlapi.protege.AspectOWLEditorKitHook;
import de.fuberlin.csw.aspectowl.renderer.AspectOWLFunctionalSyntaxRenderer;
import org.protege.editor.owl.ui.view.ontology.AbstractOntologyRenderingViewComponent;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.Writer;

public class OWLAspectFunctionalSyntaxRenderingViewComponent extends AbstractOntologyRenderingViewComponent {
    @Override
    protected void renderOntology(OWLOntology ontology, Writer writer) throws Exception {
        AspectOWLFunctionalSyntaxRenderer ren = new AspectOWLFunctionalSyntaxRenderer(AspectOWLEditorKitHook.getAspectManager(getOWLEditorKit()));
        ren.render(ontology, writer);
    }
}
