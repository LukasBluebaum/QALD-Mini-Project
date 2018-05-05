import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aksw.qa.annotation.index.IndexDBO_classes;
import org.aksw.qa.annotation.index.IndexDBO_properties;
import org.aksw.qa.annotation.spotter.ASpotter;
import org.aksw.qa.annotation.spotter.Fox;
import org.aksw.qa.annotation.spotter.Spotlight;
import org.aksw.qa.commons.datastructure.Entity;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class QA {
	
	
	private static String question = "Katie Holmes";
	private static  Map<String, List<Entity>> entities = new LinkedHashMap<>();
	private static final String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
									   + "PREFIX dbp:  <http://dbpedia.org/property/> \n"
									   + "PREFIX dbo:  <http://dbpedia.org/ontology/> \n"
									   + "PREFIX    : <http://dbpedia.org/resource/>  \n"
									   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
	
	
	private static  List<String> boolquestion = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE");
	private static final String service = "http://dbpedia.org/sparql";
	
 public static void main(String[] args) throws IOException {
	
	/* if(args[0] != null)
		 question = args[0];
	 */
//		 Spotlight fox = new Spotlight();
//		// spotlight.setConfidence(0.35);
//		
//		 entities = fox.getEntities(question);
//		 for (Map.Entry<String, List<Entity>> entry : entities.entrySet())
//		 {		
//			
//		     System.out.println(entry.getKey() + "/" + entry.getValue().get(0).getPosTypesAndCategories());
//		 }
//		 System.out.println("Test");
	 
	IndexDBO_properties index = new IndexDBO_properties();
	System.out.println(index.search("born"));
	 
	 String sparql = "SELECT DISTINCT ?pred WHERE { ?pred a rdf:Property} ORDER BY ?pred";
	 Spotlight fox = new Spotlight();
	 
			 
	// query2(sparql);
	 String[] tokens = question.split(" ");
	 String starting = tokens[0].toUpperCase();
	 switch(starting) {
	 case "WHO":		 
		 break;
	 case "HOW":		 
		 break;		 
	 case "WHERE":	 		
	 	 break;
	 case "WHY":
		 break;
	 case "WHAT":
		 break;
	 case "WHICH":
		 break;
	 case "WHEN":
		 break;
	 default:
		 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) 
		 {
			 
		 }
		 if(boolquestion.contains(starting))
		 {
			 
		 }
		 
		 break;
	 		
	 
	 
	 }
	 
}



	public static void query2(String q) throws IOException {
		
		File fout = new File("out.ttl");
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		q = PREFIX + q;
		System.out.println(q);
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
		ResultSet rs = qe.execSelect();
		while(rs.hasNext()) {
			QuerySolution s = rs.nextSolution();
			String property = s.toString();
			int begin = property.indexOf("<");
			int end = property.indexOf(">");
			property = property.substring(begin,end+1);
			begin = property.lastIndexOf("/");
			end = property.length();
			String name = property.substring(begin+1,end-1);
			String[] text = name.split("(?=\\p{Upper})");
			
			property = property + " <http://www.w3.org/2000/01/rdf-schema#label>" + " \"";
			
			int i = 0;
			for(String t: text) {
				if(i != text.length-1) {
					property = property + t.toLowerCase() + " ";
				} else {
					property = property + t.toLowerCase() + "\".\n";
				}
				i++;
				
			}
			bw.write(property);
			System.out.println(property);
			//System.out.println(s);
			
			
		}
		bw.close();
	}
		
		
		
		public static void query(String q) {
			
			
			q = PREFIX + q;
			System.out.println(q);
			QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
			ResultSet rs = qe.execSelect();
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution();
				System.out.println(s);
				
			}
			
		
	}
	
	
}	