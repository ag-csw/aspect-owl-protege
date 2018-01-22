package de.fuberlin.csw.aspectowl.protege.views;

import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspectAssertionAxiom;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.entity.AnnotationPropertyComparator;
import org.protege.editor.owl.ui.OWLClassExpressionComparator;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.renderer.OWLAnnotationCellRenderer2;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AspectAssertionsList extends MList {

    private static AxiomType<OWLAspectAssertionAxiom> OWL_AXIOM_ASSERTION_AXIOM_TYPE;

    static {
        Class axiomTypeClass = AxiomType.class;
        try {
            Field axiomTypeField = axiomTypeClass.getField("OWL_AXIOM_ASSERTION_AXIOM_TYPE");
            OWL_AXIOM_ASSERTION_AXIOM_TYPE = (AxiomType<OWLAspectAssertionAxiom>) axiomTypeField.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static final String HEADER_TEXT = "Aspects";


    private OWLEditorKit editorKit;

    private OWLClassDescriptionEditor editor;

    private OWLAxiom root;


    private MListSectionHeader header = new MListSectionHeader() {

        public String getName() {
            return HEADER_TEXT;
        }

        public boolean canAdd() {
            return true;
        }
    };

//    private OWLOntologyChangeListener ontChangeListener = changes -> handleOntologyChanges(changes);

    private MouseListener mouseListener = new MouseAdapter(){
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                handleEdit();
            }
        }
    };



    public AspectAssertionsList(OWLEditorKit eKit) {
        this.editorKit = eKit;
        setCellRenderer(new OWLAnnotationCellRenderer2(eKit));
        addMouseListener(mouseListener);
//        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }

    public void setRootObject(OWLAxiom root){
        this.root = root;
        refill(root);
    }

    protected void refill(OWLAxiom root) {
        List<Object> data = new ArrayList<>();

        data.add(header);

        if (root != null){
            List<OWLAspect> aspects = new ArrayList<>(OWLOntologyAspectManager.instance().getAssertedAspects(root).collect(Collectors.toList()));
            Comparator<OWLObject> owlObjectComparator = editorKit.getOWLModelManager().getOWLObjectComparator();
            OWLClassExpressionComparator aspectComparator =
                    new OWLClassExpressionComparator(editorKit.getOWLModelManager());
            aspects.sort((a1, a2) -> {
                int propComp = aspectComparator.compare(a1, a2);
                if(propComp != 0) {
                    return propComp;
                }
                return owlObjectComparator.compare(a1, a2);
            });
            for (OWLAspect aspect : aspects){
                data.add(new AspectAssertionsList.AspectAssertionsListItem(aspect));
            }
        }

        setListData(data.toArray());
        revalidate();
    }


    public OWLAxiom getRoot(){
        return root;
    }


    protected void refresh() {
        setRootObject(root);
    }

    public void dispose() {
//        editorKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        if (editor != null) {
            editor.dispose();
            editor = null;
        }
    }

    public class AspectAssertionsListItem implements MListItem {

        private OWLAspect aspect ;

        public AspectAssertionsListItem(OWLAspect aspect) {
            this.aspect = aspect;
        }


        public OWLAspect getAnnotation() {
            return aspect;
        }


        public boolean isEditable() {
            return true;
        }


        public void handleEdit() {
            // don't need to check the section as only the direct imports can be added
            if (editor == null){
                editor = editorKit.getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, OWL_AXIOM_ASSERTION_AXIOM_TYPE);
            }
            editor.setEditedObject(aspect);
            UIHelper uiHelper = new UIHelper(editorKit);
            int ret = uiHelper.showValidatingDialog("Ontology Annotation", editor.getEditorComponent(), null);

            if (ret == JOptionPane.OK_OPTION) {
                OWLClassExpression newAnnotation = editor.getEditedObject();
                if (newAnnotation != null && !newAnnotation.equals(aspect)){
//                    List<OWLOntologyChange> changes = getReplaceChanges(aspect, newAnnotation);
//                    editorKit.getModelManager().applyChanges(changes);
                }
            }
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
//            List<OWLOntologyChange> changes = getDeleteChanges(aspect);
//            editorKit.getModelManager().applyChanges(changes);
            return true;
        }


        public String getTooltip() {
            return "";
        }
    }
}
