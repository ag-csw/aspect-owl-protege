package de.fuberlin.csw.aspectowl.protege.editorkit;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.owl.OWLEditorKitFactory;

import java.net.URI;

/**
 * Created by ralph on 22.05.17.
 */
public class AspectOWLEditorKitFactory extends OWLEditorKitFactory {

    public static final String ID = "de.fuberlin.csw.aspectowl.protege.editorkit.AspectOWLEditorKitFactory";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public EditorKit createEditorKit() {
        return new AspectOWLEditorKit(this);
    }

    @Override
    public boolean canLoad(URI uri) {
        return super.canLoad(uri) || uri.toString().endsWith(".aofn");
    }
}
