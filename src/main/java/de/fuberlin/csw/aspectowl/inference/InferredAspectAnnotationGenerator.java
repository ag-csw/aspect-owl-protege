/*******************************************************************************
 * Copyright (c) 2015 Freie Universitaet Berlin, Department of
 * Computer Science. All rights reserved.
 *
 * This file is part of the Corporate Smart Content Project.
 *
 * This work has been partially supported by the InnoProfile-Transfer
 * Corporate Smart Content project funded by the German Federal Ministry
 * of Education and Research (BMBF) and the BMBF Innovation Initiative
 * for the New German Laender - Entrepreneurial Regions.
 *
 * <http://sce.corporate-smart-content.de/>
 *
 * Copyright (c) 2013-2016,
 *
 * Freie Universitaet Berlin
 * Institut für Informatik
 * Corporate Semantic Web group
 * Koenigin-Luise-Strasse 24-26
 * 14195 Berlin
 * <http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/>
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA or see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.fuberlin.csw.aspectowl.inference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.protege.editor.core.util.ProtegeDirectories;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.inference.OWLReasonerManager;
import org.protege.editor.owl.model.inference.VacuousAxiomVisitor;
import org.semanticweb.owl.explanation.api.Explanation;
import org.semanticweb.owl.explanation.api.ExplanationGenerator;
import org.semanticweb.owl.explanation.api.ExplanationManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;

import de.fuberlin.csw.aspectowl.util.AnnotatedAxiomsDuplicateFilter;
import de.fuberlin.csw.aspectowl.util.AspectOWLUtils;
import de.fuberlin.csw.aspectowl.util.DefaultOWLAxiomVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyImpl;

/**
 * This class handles the propagation of aspects of asserted axioms to inferred
 * axioms using explanations. If there is a set of axioms that explain an
 * entailment and any of these axioms belong to one or several aspects, then the
 * entailed axiom also belongs to these aspects. If one set of explaining axioms
 * can be found where none of the axioms belong to any aspect, then the
 * entailment is aspect-free as well.
 * 
 * @author ralph
 * 
 */
public class InferredAspectAnnotationGenerator extends DefaultOWLAxiomVisitorAdapter {
	
	private static final Logger log = LoggerFactory.getLogger(InferredAspectAnnotationGenerator.class);

	private OWLWorkspace workspace;
	
	private ExplanationGenerator<OWLAxiom> explanationGenerator;
	
	private Set<OWLAnnotationProperty> aspectProperties;
	
	private OWLOntologyManager tempOM;
	private OWLOntology inferredAspectsOnto;
	
	private File cacheDir;
	
	private static InferredAspectAnnotationGenerator instance;

	/**
	 * Returns the singleton instance of the {@link InferredAspectAnnotationGenerator} class.
	 * @param workspace The OWL workspace.
	 * @return The single instance of the {@link InferredAspectAnnotationGenerator}.
	 */
	public static InferredAspectAnnotationGenerator getInstance(OWLWorkspace workspace) {
		if (instance == null) {
			instance = new InferredAspectAnnotationGenerator(workspace);
		}
		return instance;
	}
	
	/**
	 * Constructor for the {@link InferredAspectAnnotationGenerator}.
	 * @param workspace The OWL Workspace.
	 */
	public InferredAspectAnnotationGenerator(OWLWorkspace workspace) {
		this.workspace = workspace;
		cacheDir = new File(ProtegeDirectories.getDataDirectory(), "aspect-cache");
		cacheDir.mkdirs();
	}
	
	/**
	 * 
	 * @throws OWLOntologyCreationException
	 */
	public OWLOntology inferAspects() throws OWLOntologyCreationException {
		
		OWLModelManager modelManager = workspace.getOWLModelManager();

        tempOM = OWLManager.createOWLOntologyManager();
        
        inferredAspectsOnto = tempOM.createOntology(IRI.create("http://www.corporate-semantic-web.de/ontologies/aspects" + System.currentTimeMillis()));
				
		OWLReasonerManager reasonerManager = modelManager.getOWLReasonerManager();
		OWLReasoner reasoner = reasonerManager.getCurrentReasoner();
		OWLOntology rootOnto = reasoner.getRootOntology();
		
        String hash = null;
		try {
			hash = getHash(rootOnto);
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		OWLOntology cachedResult = null;
		try {
			cachedResult = getFromCache(hash);
		} catch (NoSuchAlgorithmException | ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		if (cachedResult != null) {
			return cachedResult;
		}
		
		
        aspectProperties = AspectOWLUtils.getAllAspectAnnotationProperties(rootOnto);
		
        OWLOntology inferredOnt = tempOM.createOntology(IRI.create("http://another.com/ontology" + System.currentTimeMillis()));
        InferredOntologyGenerator ontGen = new InferredOntologyGenerator(reasoner);
        ontGen.fillOntology(tempOM.getOWLDataFactory(), inferredOnt);
        
        AnnotatedAxiomsDuplicateFilter.filter(inferredOnt);
		
		Set<OWLAxiom> inferredAxioms = inferredOnt.getAxioms();
		
//		int tasks = inferredAxioms.size() + 1; // every axiom + explanation generator initialization

//		ProgressMonitor monitor = new ProgressMonitor(workspace, "Inferring aspects", "Initializing explanation generator", 0, tasks);

		explanationGenerator = ExplanationManager.createExplanationGeneratorFactory(modelManager.getOWLReasonerManager().getCurrentReasonerFactory().getReasonerFactory()).createExplanationGenerator(rootOnto);
		
//		int completed = 0;
		
		for (OWLAxiom axiom : inferredAxioms) {

//			monitor.setProgress(++completed);
//			monitor.setNote("Looking at axiom " + completed + " of " + tasks);

			// Skip asserted axioms that made their way into the inferred ontology
			boolean skip = false;
            for (OWLOntology actOnt : modelManager.getActiveOntologies()) {
                if (actOnt.containsAxiomIgnoreAnnotations(axiom)) {
                	skip = true;
                	break;
                }
            }
            
            if (skip)
            	continue;
            
			if (!(VacuousAxiomVisitor.isVacuousAxiom(axiom) || VacuousAxiomVisitor.involvesInverseSquared(axiom))) {
				axiom.accept(this);
			}
		}
		
//		monitor.close();
		
		// clean up
		tempOM = null;
		aspectProperties = null;
		explanationGenerator = null;
		
		try {
			storeInCache(hash, inferredAspectsOnto);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return inferredAspectsOnto;

	}
	
	
	@Override
	protected void handleDefault(OWLAxiom axiom) {
		
		try {
			Set<Explanation<OWLAxiom>> explanations = explanationGenerator.getExplanations(axiom);
			
			for (Explanation<OWLAxiom> explanation : explanations) {
				
				// Some reasoners include the inferred axiom itself in the set
				// of explanations. We do not want that, so we skip it here.
				if (explanation.getSize() == 1 && explanation.getAxioms().iterator().next().equals(axiom))
					continue;
				
				// Handle case were there are several logical paths leading to a
				// consequence where one of the paths is generally valid
				// (contains no axioms with aspects)
				HashSet<OWLAnnotation> tracker = new HashSet<OWLAnnotation>();
				
				for (OWLAxiom explanationAxiom : explanation.getAxioms()) {
					
					HashSet<OWLAnnotation> allAspectAnnotations = new HashSet<OWLAnnotation>();
					for (OWLAnnotationProperty aspectAnnotationProperty : aspectProperties) {
						Set<OWLAnnotation> annotations = explanationAxiom.getAnnotations(aspectAnnotationProperty);
						allAspectAnnotations.addAll(annotations);
					}
					
					
					if (!allAspectAnnotations.isEmpty()) {
						tracker.addAll(allAspectAnnotations);

						Set<OWLAxiom> existingAxioms = inferredAspectsOnto.getAxiomsIgnoreAnnotations(axiom);
						if (!existingAxioms.isEmpty()) {
							axiom = existingAxioms.iterator().next();
							log.debug("Will add annotation to axiom: " + axiom);
							tempOM.removeAxiom(inferredAspectsOnto, axiom);
						}
						OWLAxiom inferredAxiomWithAspects = axiom.getAnnotatedAxiom(allAspectAnnotations);
						tempOM.addAxiom(inferredAspectsOnto, inferredAxiomWithAspects);
						
						// for testing. TODO remove the following two lines
//						om.removeAxiom(infOnto, axiom);
//						om.addAxiom(infOnto, inferredAxiomWithAspects);

						log.debug("Added axiom: " + inferredAxiomWithAspects);
					}
				}
				if (tracker.isEmpty()) {
					// We have found an explanation path that is generally 
					// valid without being restricted to any particular
					// aspect. We're done for this axiom.
					log.debug("Found a generally (aspect-less) valid explanation path for " + axiom + ". Aborting."); 
					tempOM.removeAxiom(inferredAspectsOnto, axiom);
					return;
				}
			}
		} catch (Exception e) {
			// The explanation generator throws an exception in case of an
			// unsupported axiom type. So it's quite normal to end up here.
			log.debug(e.getMessage() + ": " + axiom);
		}
	}
	
	private OWLOntology getFromCache(String hash) throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        
        
        File[] dir = cacheDir.listFiles();
        for (File file : dir) {
			String name = file.getName();
			if (name.startsWith(hash)) {
				ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
				ConcurrentOWLOntologyImpl result = (ConcurrentOWLOntologyImpl)(in.readObject());
				in.close();
				return result;
			}
		}
        
        return null;
	}
	
	/**
	 * @param hash
	 * @param inferredAspectsOnto
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void storeInCache(String hash, OWLOntology inferredAspectsOnto) throws FileNotFoundException, IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(cacheDir, hash)));
		out.writeObject(inferredAspectsOnto);
		out.close();
	}


	
	private String getHash(OWLOntology rootOnto) throws NoSuchAlgorithmException {
        TreeSet<OWLAxiom> axioms = new TreeSet<OWLAxiom>();
		for (OWLAxiom axiom : rootOnto.getAxioms(Imports.INCLUDED)) {
			axioms.add(axiom);
		}
        
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(axioms.toString().getBytes());
        
        byte[] mdbytes = md.digest();
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        
        return sb.toString();
	}
}
