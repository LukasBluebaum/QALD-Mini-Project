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
			   + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	private static final String service = "http://dbpedia.org/sparql";
	
	private Map<String, List<String>> properties;
	
	private ArrayList<Entity> entityList;
	
	public SparqlQueryBuilder(Map<String, List<String>> properties, ArrayList<Entity> entityList) {
		this.properties = properties;
		this.entityList = entityList;
	}
	
	public String sparqlWhen() throws UnsupportedEncodingException {
 		for(String keyword: properties.keySet()) {
 			for(String property : (ArrayList<String>) properties.get(keyword))
	 			if(property != null) {
	 				String entitiy = URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				String query = "SELECT ?date WHERE{<" + entitiy + "> <" + property + "> ?date ." + " FILTER ( datatype(?date) = xsd:date )}";
	 				String result = executeQuery(query);
	 				System.out.println(property + "\n");
	 				if(result != null) return result;
	 			}
 		}
 		return lastOptionWhen();
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
}
