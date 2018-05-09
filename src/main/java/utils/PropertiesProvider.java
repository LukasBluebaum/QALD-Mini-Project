package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class PropertiesProvider {
	
	private static final String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			   + "PREFIX dbp:  <http://dbpedia.org/property/> \n"
			   + "PREFIX dbo:  <http://dbpedia.org/ontology/> \n"
			   + "PREFIX    : <http://dbpedia.org/resource/>  \n"
			   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			   + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	private static final String service = "http://dbpedia.org/sparql";
	
	public void getDbpediaProperties(String q) throws IOException {
			
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
}
