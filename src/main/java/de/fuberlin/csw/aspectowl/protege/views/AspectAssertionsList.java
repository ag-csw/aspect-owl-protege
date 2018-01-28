package de.fuberlin.csw.aspectowl.protege.views;

import de.fuberlin.csw.aspectowl.owlapi.model.AspectContainer;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspect;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLAspectAssertionAxiom;
import de.fuberlin.csw.aspectowl.owlapi.model.OWLOntologyAspectManager;
import de.fuberlin.csw.aspectowl.owlapi.model.impl.OWLAxiomInstance;
import org.protege.editor.core.ui.list.MList;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.OWLClassExpressionComparator;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.editor.OWLClassDescriptionEditor;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AspectAssertionsList extends MList {

    private static AxiomType<OWLAspectAssertionAxiom> OWL_AXIOM_ASSERTION_AXIOM_TYPE;

    private final static OWLOntologyAspectManager aspectManager = OWLOntologyAspectManager.instance();

//    TODO weaving does not work (yet)
//    static {
//        Class axiomTypeClass = AxiomType.class;
//        try {
//            Field axiomTypeField = axiomTypeClass.getField("OWL_AXIOM_ASSERTION_AXIOM_TYPE");
//            OWL_AXIOM_ASSERTION_AXIOM_TYPE = (AxiomType<OWLAspectAssertionAxiom>) axiomTypeField.get(null);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }

    static {
        // sneak in our aspectAssertionAxiom assertion axiom type (need to use brute force and a hammer, since the AxiomType class
        // is final and its constructor is private).
        Class<AxiomType> axiomTypeClass = AxiomType.class;
        try {
            Constructor constr = axiomTypeClass.getDeclaredConstructor(Class.class, Integer.TYPE, String.class, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            constr.setAccessible(true);
            OWL_AXIOM_ASSERTION_AXIOM_TYPE = (AxiomType<OWLAspectAssertionAxiom>)constr.newInstance(OWLAspectAssertionAxiom.class, 38, "AspectAssertion", true, true, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static final String HEADER_TEXT = "Aspects";


    private OWLEditorKit editorKit;

    private OWLClassDescriptionEditor editor;

    private OWLAxiomInstance root;

    private OWLAxiom newAxiom;



    private MListSectionHeader header = new MListSectionHeader() {

        public String getName() {
            return HEADER_TEXT;
        }

        public boolean canAdd() {
            return true;
        }
    };

    private OWLOntologyChangeListener ontChangeListener = changes -> handleOntologyChanges(changes);

    private MouseListener mouseListener = new MouseAdapter(){
        public void mouseReleased(MouseEvent e) {
            if (e.getClickCount() == 2) {
                handleEdit();
            }
        }
    };



    public AspectAssertionsList(OWLEditorKit eKit) {
        this.editorKit = eKit;
        setCellRenderer(new AspectAssertionListItemRenderer(eKit));
        addMouseListener(mouseListener);
        eKit.getOWLModelManager().addOntologyChangeListener(ontChangeListener);
    }

    public void setRootObject(OWLAxiomInstance root){
        this.root = root;
        refill(root);
    }

    protected void refill(OWLAxiomInstance root) {
        List<Object> data = new ArrayList<>();

        data.add(header);

        if (root != null){
            List<OWLAspectAssertionAxiom> aspects = new ArrayList<>(aspectManager.getAspectAssertionAxioms(root.getOntology(), root.getAxiom()));
            Comparator<OWLObject> owlObjectComparator = editorKit.getOWLModelManager().getOWLObjectComparator();
            OWLClassExpressionComparator aspectComparator =
                    new OWLClassExpressionComparator(editorKit.getOWLModelManager());
            aspects.sort((a1, a2) -> {
                int propComp = aspectComparator.compare(a1.getAspect(), a2.getAspect());
                if(propComp != 0) {
                    return propComp;
                }
                return owlObjectComparator.compare(a1, a2);
            });
            for (OWLAspectAssertionAxiom aspect : aspects){
                data.add(new AspectAssertionsList.AspectAssertionsListItem(aspect));
            }
        }

        setListData(data.toArray());
        revalidate();
    }


    public OWLAxiomInstance getRoot(){
        return root;
    }


    protected void refresh() {
        setRootObject(root);
    }


    protected void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {

//        // this is complicated by the fact that adding an aspect to an axiom produces a new axiom
//        if (newAxiom != null){
//            for (OWLOntologyChange change : changes){
//                if (change instanceof OWLAxiomChange){
//                    if (change.getAxiom().equalsIgnoreAnnotations(getRoot())){
//                        // @@TODO should check that ontology contains the new axiom
//                        setRootObject(newAxiom);
//                        newAxiom = null;
//                        return;
//                    }
//                }
//            }
//        }
    }


    public void dispose() {
        editorKit.getOWLModelManager().removeOntologyChangeListener(ontChangeListener);
        if (editor != null) {
            editor.dispose();
            editor = null;
        }
    }

    public class AspectAssertionsListItem implements MListItem {

        private OWLOntology ontology;

        private OWLAspectAssertionAxiom aspectAssertionAxiom;

        public AspectAssertionsListItem(OWLAspectAssertionAxiom aspectAssertionAxiom) {
            this.aspectAssertionAxiom = aspectAssertionAxiom;
        }


        public OWLAspectAssertionAxiom getAnnotation() {
            return aspectAssertionAxiom;
        }


        public boolean isEditable() {
            return true;
        }


        public void handleEdit() {
            // don't need to check the section as only the direct imports can be added
            if (editor == null){
                editor = editorKit.getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, OWL_AXIOM_ASSERTION_AXIOM_TYPE);
            }
            OWLAspect originalAspect = aspectAssertionAxiom.getAspect();
            editor.setEditedObject(originalAspect);
            UIHelper uiHelper = new UIHelper(editorKit);
            int ret = uiHelper.showValidatingDialog("Axiom Aspect", editor.getEditorComponent(), null);

            if (ret == JOptionPane.OK_OPTION) {
                OWLAspect newAspect = (OWLAspect) editor.getEditedObject();
                if (newAspect != null && !newAspect.equals(originalAspect)){
                    List<OWLOntologyChange> changes = getReplaceChanges(aspectAssertionAxiom.getAspect(), newAspect);
                    editorKit.getModelManager().applyChanges(changes);
                }
            }
        }


        public boolean isDeleteable() {
            return true;
        }


        public boolean handleDelete() {
            List<OWLOntologyChange> changes = getDeleteChanges(aspectAssertionAxiom);
            editorKit.getModelManager().applyChanges(changes);
            return true;
        }


        public String getTooltip() {
            return "";
        }
    }

    protected List<OWLOntologyChange> getReplaceChanges(OWLAspect oldAspect, OWLAspect newAspect) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        final OWLAxiom ax = getRoot().getAxiom();
        final OWLOntology ont = getRoot().getOntology();
        Set<OWLAspect> aspects = new HashSet<>(aspectManager.getAssertedAspects(ont, ax));
        aspects.remove(oldAspect);
        aspects.add(newAspect);

        newAxiom = ax.getAxiomWithoutAnnotations().getAnnotatedAxiom(aspects);

        changes.add(new RemoveAxiom(ont, ax));
        changes.add(new AddAxiom(ont, newAxiom));
        return changes;
    }


    protected List<OWLOntologyChange> getDeleteChanges(OWLAspectAssertionAxiom oldAspectAssertionAxiom) {
        List<OWLOntologyChange> changes = new ArrayList<>();
        final OWLAxiom ax = getRoot();
        final OWLOntology ont = getRoot().getOntology();

        Set<OWLAnnotation> annotations = new HashSet<>(ax.getAnnotations());
        annotations.remove(oldAnnotation);

        newAxiom = ax.getAxiomWithoutAnnotations().getAnnotatedAxiom(annotations);

        changes.add(new RemoveAxiom(ont, ax));
        changes.add(new AddAxiom(ont, newAxiom));
        return changes;
    }
    private class AspectAssertionListItemRenderer implements ListCellRenderer {

        private OWLCellRenderer ren;


        public AspectAssertionListItemRenderer(OWLEditorKit editorKit) {
            ren = new OWLCellRenderer(editorKit);
        }


        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value instanceof AspectAssertionsListItem) {
                AspectAssertionsListItem item = ((AspectAssertionsListItem) value);
                ren.setOntology(item.ontology);
                ren.setHighlightKeywords(true);
                ren.setWrap(false);
                return ren.getListCellRendererComponent(list, item.aspectAssertionAxiom.getAxiom(), index, isSelected, cellHasFocus);
            }
            else {
                return ren.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        }
    }

}
