/**
 * 
 */
package de.fuberlin.csw.aspectowl.owlapi.model.impl;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.annotation.EntityCreationMetadataPreferences;
import org.protege.editor.owl.model.annotation.EntityCreationMetadataPreferencesManager;
import org.protege.editor.owl.model.annotation.EntityCreationMetadataProvider;
import org.protege.editor.owl.model.annotation.PreferencesBasedEntityCreationMetadataProviderFactory;
import org.protege.editor.owl.model.entity.*;
import org.protege.editor.owl.model.user.OrcidPreferencesManager;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;
import org.protege.editor.owl.model.user.UserPreferences;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * @author ralph
 */
public class AspectOWLEntityFactory extends CustomOWLEntityFactory {


	private Logger logger = LoggerFactory.getLogger(AspectOWLEntityFactory.class);

	private static AutoIDGenerator autoIDGenerator; // only a single generator between instances

	private LabelDescriptor labelDescriptor;


	/**
	 * @param mngr
	 */
	public AspectOWLEntityFactory(OWLModelManager mngr) {
		super(mngr);
	}

	public static <T extends OWLEntity> T getOWLEntity(OWLDataFactory factory, Class<T> type, IRI iri) {
		if (OWLClass.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLClass(iri));
		}
		else if (OWLObjectProperty.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLObjectProperty(iri));
		}
		else if (OWLDataProperty.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLDataProperty(iri));
		}
		else if (OWLNamedIndividual.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLNamedIndividual(iri));
		}
		else if (OWLAnnotationProperty.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLAnnotationProperty(iri));
		}
		else if (OWLDatatype.class.isAssignableFrom(type)){
			return type.cast(factory.getOWLDatatype(iri));
		}
		throw new RuntimeException("Missing branch for entity type: " + type.getSimpleName());
	}





}
