package de.fuberlin.csw.aspectowl.utilTest;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import com.google.common.collect.Iterators;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import de.fuberlin.csw.aspectowl.util.AspectOWLUtils;

public class AspectOWLUtilsTest {
	
	private HashMap<String,HashMap<String,IRI>> setup = new HashMap<String,HashMap<String,IRI>>();

	@Before
	public void setUp() throws Exception
	{
		// should be of the form: NAME, IRI, FILE_RESOURCE
		String[][] setupContent = {
				{"example", "http://csw.inf.fu-berlin.de/aood/example", "/AspectsExample.owl"},
				{"aspect",  "http://www.corporate-semantic-web.de/ontologies/aspect_owl", "/aspectOWL.owl"},
				{"time",    "http://www.w3.org/2006/time", "/time.owl"}
		};
		
		for (String[] setupElement : setupContent)
		{
			HashMap<String,IRI> structuredSetup = new HashMap<String,IRI>();
			structuredSetup.put("ontology", IRI.create(setupElement[1]));
			structuredSetup.put("document", IRI.create(new File(getClass().getResource(setupElement[2]).getPath())));
			this.setup.put(setupElement[0], structuredSetup);
		}
	}

	@Test
	public void sparqlQueryConstructTest()
	{
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		
		HashMap<String,IRI> exampleIRIs = this.setup.get("example");
		HashMap<String,IRI> aspectIRIs  = this.setup.get("aspect");
		HashMap<String,IRI> timeIRIs    = this.setup.get("time");
		
		om.addIRIMapper(new SimpleIRIMapper(exampleIRIs.get("ontology"), exampleIRIs.get("document")));
		om.addIRIMapper(new SimpleIRIMapper(aspectIRIs.get("ontology"),  aspectIRIs.get("document")));
		om.addIRIMapper(new SimpleIRIMapper(timeIRIs.get("ontology"),    timeIRIs.get("document")));
		
		String QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX aspect: <http://www.corporate-semantic-web.de/ontologies/aspect_owl#>\n" + 
				"\n" + 
				"CONSTRUCT {?s ?p ?o}\n" + 
				"WHERE {\n" + 
				"	?s ?p ?o .\n" + 
				"	?o rdfs:subClassOf+ aspect:Aspect .\n" + 
				"}";
		
		Query query = QueryFactory.create(QUERY);
		
		try {
			OWLOntology onto = om.loadOntology(exampleIRIs.get("ontology"));
			OntModel jenaModel = AspectOWLUtils.owlOntologyToJenaModel(onto, true);
			QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);
			
			Model result = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM );
			result = qexec.execConstruct(result);
			
			OWLOntology module = AspectOWLUtils.jenaModelToOWLOntology(result);
			
			assertTrue(onto.getAxiomCount() >= module.getAxiomCount());
			
			Set<OWLAxiom> axioms = module.getAxioms();
			for (OWLAxiom axiom : axioms)
			{
				/*
				boolean containsAxiom = onto.containsAxiomIgnoreAnnotations(axiom, true);
				System.out.println((containsAxiom ? "PASSED" : "FAILED") + " to find axiom " + axiom);
				assert containsAxiom;
				*/
				assertTrue(onto.containsAxiomIgnoreAnnotations(axiom, true));
			}
		}
		catch (Exception e) {
			fail( e.toString() );
		}
		
	}
	
	@Test
	public void sparqlQueryAskTest()
	{
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		
		HashMap<String,IRI> timeIRIs = this.setup.get("time");
		om.addIRIMapper(new SimpleIRIMapper(timeIRIs.get("ontology"), timeIRIs.get("document")));
		
		OWLOntology ontology = null;
		OntModel jenaModel = null;
		try {
			ontology  = om.loadOntology(timeIRIs.get("ontology"));
			jenaModel = AspectOWLUtils.owlOntologyToJenaModel(ontology, true);
		}
		catch (Exception e) {
			fail( e.toString() );
		}
		
		// every line represents a query of the form: QUERY_STRING, EXPECTATION
		String[][] queries = {
			{ "ASK {}", "true" },
			{ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX time: <http://www.w3.org/2006/time#>"
					+ "ASK { time:TemporalEntity rdfs:subClassOf time:Interval }", "false" },
			{ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX time: <http://www.w3.org/2006/time#>"
					+ "ASK { time:Interval rdfs:subClassOf time:TemporalEntity }", "true" }
		};
		
		for (String[] queryLine : queries)
		{
			String  queryString = queryLine[0];
			boolean expectation = Boolean.parseBoolean(queryLine[1]);
			
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);
			assertEquals( qexec.execAsk(), expectation);
			qexec.close();
		}
		
	}
	
	@Test
	public void sparqlQuerySelectTest()
	{
		OWLOntologyManager om = OWLManager.createOWLOntologyManager();
		
		HashMap<String,IRI> aspectIRIs = this.setup.get("aspect");
		om.addIRIMapper(new SimpleIRIMapper(aspectIRIs.get("ontology"), aspectIRIs.get("document")));
		
		OWLOntology ontology = null;
		OntModel jenaModel = null;
		try {
			ontology  = om.loadOntology(aspectIRIs.get("ontology"));
			jenaModel = AspectOWLUtils.owlOntologyToJenaModel(ontology, true);
		}
		catch (Exception e) {
			fail( e.toString() );
		}
		
		// every line represents a query of the form: QUERY_STRING, EVALUATION_MODE, EXPECTATION
		Object[][] queries = {
			{ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "SELECT ?subject ?object"
					+ "WHERE { ?subject rdfs:subClassOf ?object }", "amount", 16 },
			{ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
					+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
					+ "SELECT ?subject ?object"
					+ "WHERE { ?subject rdfs:subClassOf ?object ."
					+ "        ?object rdfs:subClassOf ?object"
					+ "}", "amount", 0 },
			{ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX asp: <http://www.corporate-semantic-web.de/ontologies/aspect_owl#>"
					+ "SELECT ?x"
					+ "WHERE { ?x rdf:type asp:PointCut }", "direct", null },
			{ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX asp: <http://www.corporate-semantic-web.de/ontologies/aspect_owl#>"
					+ "SELECT ?x"
					+ "WHERE { ?x rdf:type asp:ReasoningComplexity }", "direct", "hashCode" }
		};
		
		for (Object[] queryLine : queries)
		{
			String queryString = String.valueOf(queryLine[0]);
			String testingMode = String.valueOf(queryLine[1]);
			Object expectation = queryLine[2];
			
			Query query = QueryFactory.create(queryString);
			QueryExecution qexec = QueryExecutionFactory.create(query, jenaModel);
			ResultSet results = qexec.execSelect();
			//ResultSetFormatter.out(System.out, results, query);
			
			switch ( testingMode )
			{
				case "amount":
					assertEquals(Iterators.size(results), expectation);
					break;
				case "direct":
					try {
						Model resultModel = results.getResourceModel();
						OWLOntology module = AspectOWLUtils.jenaModelToOWLOntology(resultModel);
						Set<OWLAxiom> axioms = module.getAxioms();
						
						if (expectation == "hashCode")
						{
							String ax = axioms.toString();
							assertEquals(ax.hashCode(), -2088790216);
						}
					}
					catch (OWLOntologyCreationException e) {
						fail( e.toString() );
					}
					break;
				default:
					assertTrue(false);
			}
			
			qexec.close();
		}
		
	}

}
