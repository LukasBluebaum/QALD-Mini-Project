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
	
	private String question;
	
	public SparqlQueryBuilder(Map<String, List<String>> properties,ArrayList<String> classes, ArrayList<Entity> entityList) {
		this.properties = properties;
		this.classes = classes;
		this.entityList = entityList;
		
	}
	
	public String sparqlWhen() throws UnsupportedEncodingException {
 		String result = simpleSparql(""," FILTER ( (datatype(?answer) = xsd:date) || (datatype(?answer) = xsd:gYear))");
 		if(result != null) return result;
 		return lastOptionWhen();
 	}
	
	public String sparqlWho() throws UnsupportedEncodingException {
		String result = null;
		System.out.println(classes.size());
		if(classes.size() > 0) {
			if(question.contains("most")) {
				result = mostSparql();
			}
		} else {
			result = simpleSparql("?answer a foaf:Person. ","");
		}
 		
 		if(result != null) return result;
 		return null;
 	}
	
	private String mostSparql() throws UnsupportedEncodingException {
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				String cls = URLDecoder.decode(classes.get(0), "UTF-8");
	 				String query = "SELECT ?answer WHERE{ ?answer <" + property + "> ?y . "
	 						+ "?y " + "rdf:type <" + cls + "> . } GROUP BY ?answer ORDER BY DESC(COUNT(?y)) LIMIT 1 OFFSET 0";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");	 				
	 				if(result != null) return result;
	 			}
 		}
		return null;
	}
	

	private String simpleSparql(String begin, String filter) throws UnsupportedEncodingException {
		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				String entitiy = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String query = "SELECT ?answer WHERE{" + begin +"<" + entitiy + "> <" + property + "> ?answer ."+ filter + " }";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
		return null;
	}
	
	
	private String lastOptionWhen() {
 		String query = "SELECT ?date WHERE{<" + entityList.get(0).getUris().get(0) + "> ?property ?date ." + " FILTER ( datatype(?date) = xsd:date )}";
 		return executeQuery(query);
 	}
	
	private String executeQuery(String q) {		
		String result= "";
		q = PREFIX + q;
		System.out.println(q + "\n");
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
		ResultSet rs = qe.execSelect();
		while(rs.hasNext()) {
			QuerySolution s = rs.nextSolution();
			result = result + s + " ";			
		}
		return (result.equals("")) ? null : result;		
	}
	
	public void setQuestion(String question) {
		this.question = question;
	}
}
