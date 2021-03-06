package de.fuberlin.csw.aspectowl.owlapi.model;

import org.semanticweb.owlapi.change.OWLOntologyChangeData;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

public class AddPointcut extends OWLPointcutChange {
	
	private static final long serialVersionUID = -2754681684243858566L;

	public AddPointcut(@Nonnull OWLOntology ontology, @Nonnull OWLPointcut pointcut) {
        super(ontology, pointcut);
    }

    @Override
    public boolean isAxiomChange() {
        return false;
    }

    @Override
    public boolean isAddAxiom() {
        return false;
    }

    @Nonnull
    @Override
    public OWLAxiom getAxiom() {
        return null;
    }

    @Override
    public boolean isImportChange() {
        return false;
    }

    @Nonnull
    @Override
    public OWLOntologyChangeData getChangeData() {
        return null; //TODO
    }

    @Nonnull
    @Override
    public Set<OWLEntity> getSignature() {
        return null; //TODO
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AddPointcut)) {
            return false;
        }
        AddPointcut other = (AddPointcut) obj;
        return other.getOntology().equals(getOntology()) && other.getPointcut()
                .equals(getPointcut());
    }

    @Override
    public int hashCode() {
        return 17 + Objects.hash(super.hashCode(), getPointcut());
    }

    @Nonnull
    @Override
    public String toString() {
        return "AddPointcut(" + getPointcut() + " OntologyID(" + getOntology()
                .getOntologyID() + "))";
    }

    @Override
    public void accept(@Nonnull OWLOntologyChangeVisitor visitor) {
        if (visitor instanceof AspectOWLOntologyChangeVisitor) {
            ((AspectOWLOntologyChangeVisitor)visitor).visit(this);
        }
    }

    @Override
    public <O> O accept(@Nonnull OWLOntologyChangeVisitorEx<O> visitor) {
        if (visitor instanceof AspectOWLOntologyChangeVisitorEx) {
            return (O)((AspectOWLOntologyChangeVisitorEx) visitor).visit(this);
        }
        return null;
    }

    @Override
    public OWLOntologyChange reverseChange() {
        return new RemovePointcut(getOntology(), getPointcut());
    }
}
