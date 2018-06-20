package qa;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.qa.commons.datastructure.Entity;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import jsonbuilder.AnswerContainer;
import utils.Comparison;
import utils.Question;

public class SparqlQueryBuilder {
	
	private static final String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			   + "PREFIX dbp:  <http://dbpedia.org/property/> \n"
			   + "PREFIX dbo:  <http://dbpedia.org/ontology/> \n"
			   + "PREFIX    : <http://dbpedia.org/resource/>  \n"
			   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			   + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
			   + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	
	private static final String service = "http://dbpedia.org/sparql";
	
	private Map<String, List<String>> properties;
	
	private ArrayList<String> classes;
	
	private ArrayList<Entity> entityList;
		
	private Question q;

	private String lastUsedQuery = null;
	
	public SparqlQueryBuilder(Question q) {
		this.properties = q.properties;
		this.classes = q.classes;
		this.entityList = q.entityList;	
		this.q = q;
	}
	
	public Set<String> sparqlWhere() throws UnsupportedEncodingException {
		if(entityList != null) {
			if(entityList.size() == 1 ) {
				for(String keyword: properties.keySet()) {
		 			for(String property : properties.get(keyword))
			 			if(property != null) {
			 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
			 				String query = "SELECT DISTINCT ?answer WHERE{<" + property + "> rdfs:range dbo:Place ." +"<" + entity + "> <" + property + "> ?answer . }";
			 				Set<String> result = executeQuery(query);
			 				System.out.println(property + "\n");
			 				if(result != null) return result;
			 			}
		 		}
				return simpleSparql("","");
			} else {
				
			}
		}
		return null;
 	}
	
	public Set<String> sparqlWhat() throws UnsupportedEncodingException {
		String compare = null;
		Set<String> result = null;
		for(Comparison comp: Comparison.values()) {
			if(q.question.contains(comp.toString().toLowerCase()))
				compare = comp.toString();			
		}	
		if(compare != null && compare.toLowerCase().equals(q.superlative)) {
				result = superlSparql(compare);
				if(result != null) return result;		
		
 		}
		
		return simpleSparql("","");
 	}
	
	private Set<String> superlSparql(String superlative) throws UnsupportedEncodingException {
		if(entityList != null && entityList.size() > 0 && entityList.get(0).getUris().size() > 0) {
			System.out.println("----");
			String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
			Set<String> property = null;

			String classUsed = null;
			for( String cls: classes) {
				property = executeQuery("SELECT DISTINCT ?answer WHERE{ ?x rdf:type <" + cls + ">. ?x ?answer <" + entity + ">. }");
				if(property != null) {
					classUsed = cls;
					break;
				}
			}
 			for(String p: property) {
 				String query = "SELECT ?answer WHERE{?answer rdf:type <" + classUsed + ">. ?answer  <" + p + "> <" +  entity +"> . ?answer  <" + Comparison.valueOf(superlative).getURI(0) +"> ?x . } "
 						+ "ORDER BY " +  Comparison.valueOf(superlative).getOrder() + "(?x) LIMIT 1 OFFSET 0";
 				Set<String> result = executeQuery(query);
 				System.out.println(property + "\n");
 				if(result != null) return result;
 			}
		} else {
			for(String cls: classes) {
 				String query = "SELECT ?answer WHERE{?answer rdf:type <" + cls + ">. ?answer  <" + Comparison.valueOf(superlative).getURI(0) +"> ?x . } "
 						+ "ORDER BY " +  Comparison.valueOf(superlative).getOrder() + "(?x) LIMIT 1 OFFSET 0";
 				Set<String> result = executeQuery(query);
 				if(result != null) return result;
			}
		}
		return null;
	}

	public Set<String> listSparql() throws UnsupportedEncodingException {
		Set<String> result = null;
		if(entityList == null) return null;
		if(classes != null && classes.size() > 0) {
			String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
			if(q.comparative != null) {
				Comparison comp = Comparison.valueOf(q.comparative.toUpperCase());
				String uri 		= comp.getURI(0);
				String order 	= comp.getOrder();
				for( String cls: classes) {				
					result = executeQuery("SELECT ?aswer WHERE{ ?aswer rdf:type <" + cls + ">. <" + entity +  "> <" +uri + "> ?x. "
							+  " ?answer <" + uri + "> ?y.  FILTER (?x " + (order.equals("ASC") ? ">" : "<" ) + " ?y) } ");
				}
			}
			result = simpleSparql("","");
			if(result != null) return result;
			
			for( String cls: classes) {
				result = executeQuery("SELECT DISTINCT ?answer WHERE{ ?answer rdf:type <" + cls + ">. ?answer ?x <" + entity + ">. }");
				if(result != null) return result;
			}
		}
		return null;
	}
	
	public Set<String> sparqlWhen() throws UnsupportedEncodingException {
		Set<String> result = simpleSparql(""," FILTER ( (datatype(?answer) = xsd:date) || (datatype(?answer) = xsd:gYear))");
 		if(result != null) return result;
 		return lastOptionWhen();
 	}
	
	public Set<String> sparqlWho() throws UnsupportedEncodingException {
		Set<String> result = null;
		String compare = null;		
		for(Comparison comp: Comparison.values()) {
			if(q.question.contains(comp.toString().toLowerCase()))
				compare = comp.toString();			
		}	
		if(compare != null && compare.toLowerCase().equals(q.superlative)) {
				result = superlSparql(compare);
				if(result != null) return result;		
		
 		}
		//if(classes.size() >= 1) {
			if(q.question.contains("most")) {
				result = determinerWho("DESC");
			} else if(q.question.contains("least")) {
				result = determinerWho("ASC");
			}
		 else {
			result = simpleSparql("?answer a foaf:Person. ","");
		}
 		
 		return result;
 	}
	
	private Set<String> determinerWho(String order) throws UnsupportedEncodingException {
		for(String keyword: properties.keySet()) {
 			for(String property : properties.get(keyword)) {
	 			if(property != null) {	 				
	 				String cls = URLDecoder.decode(classes.get(0), "UTF-8");
	 				String query = "SELECT ?answer WHERE{ ?answer <" + property + "> ?y . "
	 						+ "?y " + "rdf:type <" + cls + "> . } GROUP BY ?answer ORDER BY" + order + "(COUNT(?y)) LIMIT 1 OFFSET 0";
	 				Set<String> result = executeQuery(query);
	 				System.out.println(property + "\n");	 				
	 				if(result != null) return result;
	 			}
 			}	
 		}
		return null;
	}

	private Set<String> simpleSparql(String begin, String filter) throws UnsupportedEncodingException {
		return simpleSparql(begin, filter, new AnswerContainer());
	}
	
	private Set<String> simpleSparql(String begin, String filter, AnswerContainer container) throws UnsupportedEncodingException {
		if(entityList == null || entityList.size() < 1) return null;
		for(String keyword: properties.keySet()) {
 			for(String property : properties.get(keyword))
	 			if(property != null) {
	 				System.out.println(property);
	 					 				
	 				String entity = new String(entityList.get(0).getUris().get(0).toString().getBytes(), "UTF-8");
	 				String query = "SELECT ?answer WHERE{" + begin +"<" + entity + "> <" + property + "> ?answer ."+ filter + " }";
 				
	 				Set<String> result = executeQuery(query);

					container.setSparqlQuery(query);
					container.setAnswers(result);

	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
		return simpleSparqlBack(begin, filter);
	}
	
	private Set<String> simpleSparqlBack(String begin, String filter) throws UnsupportedEncodingException {
		
		for(String keyword: properties.keySet()) {
 			for(String property : properties.get(keyword))
	 			if(property != null) {
	 				System.out.println(property);
	 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String query = "SELECT ?answer WHERE{ ?answer <" + property + "> " + "<" + entity + "> ."+ filter + " }";
	 				Set<String> result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
		return null;
	}
	
	public Set<String> simpleASK(String begin, String filter) throws UnsupportedEncodingException {
		if(entityList == null ) return null;
		if(entityList.size() == 1) {
			for(String keyword: properties.keySet()) {
	 			for(String property : properties.get(keyword))
		 			if(property != null) {
		 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");		 				
		 				String query = "ASK WHERE{" + begin +"<" + entity + "> <" + property + "> ?x . "+ filter + " }";
		 				Set<String> result = executeQuery(query);
		 				System.out.println(property + "\n");
		 				if(result.contains(("true"))) return result;
		 			}
	 		}
		} else if (entityList.size() == 2) {
			for(String keyword: properties.keySet()) {
	 			for(String property : properties.get(keyword))
		 			if(property != null) {
		 				String entity1 = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
		 				String entity2 = URLDecoder.decode(entityList.get(1).getUris().get(0).toString(), "UTF-8");
		 				String query = "ASK WHERE{" + begin +"<" + entity1 + "> <" + property + "> <" + entity2 + "> . "+ filter + " }";
		 				Set<String> result = executeQuery(query);
		 				System.out.println(property + "\n");
		 				if(result.contains(("true"))) return result;
		 			}
	 		}
		}
		return new HashSet<String>(Arrays.asList("false"));

	}
	
	private Set<String> lastOptionWhen()  {
		if(entityList != null) {
 		String query = "SELECT ?answer WHERE{<" + entityList.get(0).getUris().get(0) + "> ?property ?answer ." + " FILTER ( datatype(?answer) = xsd:date )} LIMIT 1";		
 		return executeQuery(query);
		}
		return null;
 	}
	
	public Set<String> executeQuery(String q)  {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LinkedHashSet<String> result =  new LinkedHashSet<String>();
		q = PREFIX + q;
		lastUsedQuery = q;
		System.out.println(q + "\n");
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);	
		qe.setTimeout(30000);

		if(this.q.questionType.equals("ASK")) {
			result.add( String.valueOf(qe.execAsk()));
			return result;			
		} else {
			ResultSet rs = qe.execSelect();			
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution();
				
				RDFNode node = s.get("answer");
				String st = null;
				if(node.isLiteral()) {
					st = node.asLiteral().toString();
					st = st.indexOf("^^") != -1 ?  st.substring(0,st.indexOf("^^")) : st;
					result.add(st);
					break;
				}									
				else {
					 st = node.asResource().toString();
				}
				if(st.contains("wiki") || st.contains("rdf-")) continue;
				result.add(st);			
			}
		}
		qe.close();
		return result.size() > 0 ? result : null;		
	}
	

	public Set<String> sparqlHow() throws UnsupportedEncodingException {
		
		String[] tokens = q.question.split(" ");
		
		if(tokens[1].equals("much")) {
			return simpleSparql("","");			
		} else if (tokens[1].equals("many")) {
			for(String keyword: properties.keySet()) {
	 			for(String property : properties.get(keyword))
		 			if(property != null) {
		 				System.out.println(property);		 				
		 				String entity = new String(entityList.get(0).getUris().get(0).toString().getBytes(), "UTF-8");		 						 				
		 				String query = "SELECT (COUNT(DISTINCT ?a) AS ?answer) WHERE{<" + entity + "> <" + property + "> ?a . } ";		 					 				
		 				Set<String> result = executeQuery(query);

		 				System.out.println(property + "\n");
		 				if(result != null && result.iterator().hasNext() && Integer.parseInt(result.iterator().next()) != 0) return result; 		 				
		 			}
	 		}
		} else {
			
			String compare = null;
			for(Comparison comp: Comparison.values()) {
				if(q.question.contains(comp.toString().toLowerCase()))
					compare = comp.toString();			
			}
			System.out.println(compare);
			if(compare.toLowerCase().equals(q.superlative)) {
				
					System.out.println("----");
					String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
					Set<String> property = null;
					
					String classUsed = null;
					for( String cls: classes) {
						property = executeQuery("SELECT DISTINCT ?answer WHERE{ ?x rdf:type <" + cls + ">. ?x ?answer <" + entity + ">. }");
						if(property != null) {
							System.out.println(property);
							classUsed = cls;
							break;
						}
					}
					
		 			for(String p: property) {
		 				String query = "SELECT ?answer WHERE{ ?x rdf:type <" + classUsed + ">. ?x  <" + p + "> <" +  entity +"> . ?x  <" + Comparison.valueOf(compare).getURI(0) +"> ?answer . } "
		 						+ "ORDER BY " +  Comparison.valueOf(compare).getOrder() + "(?answer) LIMIT 1 OFFSET 0";
		 				Set<String> result = executeQuery(query);
		 				System.out.println(property + "\n");
		 				if(result != null) return result;
		 			}
	 		} else {
	 			String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 			String query = "SELECT ?answer WHERE{  <"+ entity +"> <" + Comparison.valueOf(compare).getURI(0) +"> ?answer . } ";
	 			Set<String> result = executeQuery(query); 				
 				if(result != null) return result;;
	 		}
			return null;
		}	
		return null;
	}

	public String getLastUsedQuery() {
		return lastUsedQuery;
	}
	
}
