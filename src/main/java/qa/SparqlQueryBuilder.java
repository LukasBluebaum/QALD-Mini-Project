package qa;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.qa.commons.datastructure.Entity;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

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
	
	public SparqlQueryBuilder(Question q) {
//		this.properties = q.properties;
//		this.classes = q.classes;
//		this.entityList = q.entityList;	
//		this.q = q;
	}
	
	public String sparqlWhere() throws UnsupportedEncodingException {
		if(entityList.size() == 1) {
			for(String keyword: properties.keySet()) {
	 			for(String property : (ArrayList<String>) properties.get(keyword))
		 			if(property != null) {
		 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
		 				String query = "SELECT DISTINCT ?answer WHERE{<" + property + "> rdfs:range dbo:Place ." +"<" + entity + "> <" + property + "> ?answer . }";
		 				String result = executeQuery(query);
		 				System.out.println(property + "\n");
		 				if(result != null) return result;
		 			}
	 		}
			return simpleSparql("","");
		} else {
			
		}
		return null;
 	}
	
	public String sparqlWhat() throws UnsupportedEncodingException {
		String compare = null;
		for(Comparison comp: Comparison.values()) {
			if(q.question.contains(comp.toString().toLowerCase()))
				compare = comp.toString();			
		}
		System.out.println(compare);
		if(compare != null && compare.toLowerCase().equals(q.superlative)) {
			
			if(entityList != null) {
				System.out.println("----");
				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
				String property = null;

				String classUsed = null;
				for( String cls: classes) {
					property = executeQuery("SELECT DISTINCT ?answer WHERE{ ?x rdf:type <" + cls + ">. ?x ?answer <" + entity + ">. }");
					if(property != null) {
						classUsed = cls;
						break;
					}
				}
				
				String[] properties = property.split(" "); 
	 			for(String p: properties) {
	 				String query = "SELECT ?answer WHERE{?answer rdf:type <" + classUsed + ">. ?answer  <" + p + "> <" +  entity +"> . ?answer  <" + Comparison.valueOf(compare).getURI(0) +"> ?x . } "
	 						+ "ORDER BY " +  Comparison.valueOf(compare).getOrder() + "(?x) LIMIT 1 OFFSET 0";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
			} else {
				for(String cls: classes) {
	 				String query = "SELECT ?answer WHERE{?answer rdf:type <" + cls + ">. ?answer  <" + Comparison.valueOf(compare).getURI(0) +"> ?x . } "
	 						+ "ORDER BY " +  Comparison.valueOf(compare).getOrder() + "(?x) LIMIT 1 OFFSET 0";
	 				String result = executeQuery(query);
	 				if(result != null) return result;
				}
			}
 		}
		
		return simpleSparql("","");
 	}
	
	public String listSparql() throws UnsupportedEncodingException {
		if(classes.size() > 0) {
			String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
			String result = null;
		
			for( String cls: classes) {
				result = executeQuery("SELECT DISTINCT ?answer WHERE{ ?answer rdf:type <" + cls + ">. ?answer ?x <" + entity + ">. }");
				if(result != null) return result;
			}
		}
		return simpleSparql("","");
	}
	
	public String sparqlWhen() throws UnsupportedEncodingException {
 		String result = simpleSparql(""," FILTER ( (datatype(?answer) = xsd:date) || (datatype(?answer) = xsd:gYear))");
 		if(result != null) return result;
 		return lastOptionWhen();
 	}
	
	public String sparqlWho() throws UnsupportedEncodingException {
		String result = null;
		if(entityList.size() == 1) {
			if(q.question.contains("most")) {
				result = determinerWho("DESC");
			} else if(q.question.contains("least")) {
				result = determinerWho("ASC");
			}
		} else {
			result = simpleSparql("?answer a foaf:Person. ","");
		}
 		
 		if(result != null) return result;
 		return null;
 	}
	
	private String determinerWho(String order) throws UnsupportedEncodingException {
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword)) {
	 			if(property != null) {	 				
	 				String cls = URLDecoder.decode(classes.get(0), "UTF-8");
	 				String query = "SELECT ?answer WHERE{ ?answer <" + property + "> ?y . "
	 						+ "?y " + "rdf:type <" + cls + "> . } GROUP BY ?answer ORDER BY" + order + "(COUNT(?y)) LIMIT 1 OFFSET 0";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");	 				
	 				if(result != null) return result;
	 			}
 			}	
 		}
		return null;
	}
	
	private String simpleSparql(String begin, String filter) throws UnsupportedEncodingException {
		
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				System.out.println(property);
	 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String query = "SELECT ?answer WHERE{" + begin +"<" + entity + "> <" + property + "> ?answer ."+ filter + " }";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
		return simpleSparqlBack(begin, filter);
	}
	
	private String simpleSparqlBack(String begin, String filter) throws UnsupportedEncodingException {
		
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				System.out.println(property);
	 				String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String query = "SELECT ?answer WHERE{ ?answer <" + property + "> " + "<" + entity + "> ."+ filter + " }";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
		return null;
	}
	
	public String simpleASK(String begin, String filter) throws UnsupportedEncodingException {
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				String entity1 = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String entity2 = URLDecoder.decode(entityList.get(1).getUris().get(0).toString(), "UTF-8");
	 				String query = "ASK WHERE{" + begin +"<" + entity1 + "> <" + property + "> <" + entity2 + "> . "+ filter + " }";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result.equals("true")) return result;
	 			}
 		}
		return "false";
	}
	
	private String lastOptionWhen() {
 		String query = "SELECT ?answer WHERE{<" + entityList.get(0).getUris().get(0) + "> ?property ?answer ." + " FILTER ( datatype(?answer) = xsd:date )}";
 		return executeQuery(query);
 	}
	
	public String executeQuery(String q) {		
		String result= "";
		q = PREFIX + q;
		System.out.println(q + "\n");
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
		qe.setTimeout(30000);

		if(this.q.questionType.equals("ASK")) {
			 return String.valueOf(qe.execAsk());			
		} else {
			ResultSet rs = qe.execSelect();
			
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution();
				
				RDFNode node = s.get("answer");
				String st = null;
				if(node.isLiteral())
					 st = node.asLiteral().toString();
				else
					 st = node.asResource().toString();
				
				if(st.contains("wiki") || st.contains("rdf-")) continue;
				result = result + st + " ";			
			}
		}
			qe.close();
		return (result.equals("")) ? null : result;		
	}
	

	public String sparqlHow() throws UnsupportedEncodingException {
		
		String[] tokens = q.question.split(" ");
		
		if(tokens[1].equals("many") || tokens[1].equals("much")) {
				
		}	else {
			
			String compare = null;
			for(Comparison comp: Comparison.values()) {
				if(q.question.contains(comp.toString().toLowerCase()))
					compare = comp.toString();			
			}
			System.out.println(compare);
			if(compare.toLowerCase().equals(q.superlative)) {
				
					System.out.println("----");
					String entity = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
					String property = null;
					
					String classUsed = null;
					for( String cls: classes) {
						property = executeQuery("SELECT DISTINCT ?answer WHERE{ ?x rdf:type <" + cls + ">. ?x ?answer <" + entity + ">. }");
						if(property != null) {
							classUsed = cls;
							break;
						}
					}
					
					String[] properties = property.split(" "); 
		 			for(String p: properties) {
		 				String query = "SELECT ?answer WHERE{ ?x rdf:type <" + classUsed + ">. ?x  <" + p + "> <" +  entity +"> . ?x  <" + Comparison.valueOf(compare).getURI(0) +"> ?answer . } "
		 						+ "ORDER BY " +  Comparison.valueOf(compare).getOrder() + "(?answer) LIMIT 1 OFFSET 0";
		 				String result = executeQuery(query);
		 				System.out.println(property + "\n");
		 				if(result != null) return result;
		 			}
	 		}
			return null;
		}
			
		
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
