package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Files;



public class PropertiesProvider {
	
	public static void main(String[] args) throws IOException {
		getDbpediaProperties();
	}
	
	private static final String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
			   + "PREFIX dbp:  <http://dbpedia.org/property/> \n"
			   + "PREFIX dbo:  <http://dbpedia.org/ontology/> \n"
			   + "PREFIX    : <http://dbpedia.org/resource/>  \n"
			   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			   + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	private static final String service = "http://dbpedia.org/sparql";
	
	public static void getDbpediaProperties() throws IOException {
		BufferedReader reader = null;
		String line;
		String doc = "";
		try {
    	 		reader = new BufferedReader(new FileReader("dbpedia_3Eng_property.ttl"));
            
    	 		while((line=reader.readLine()) != null) { 
    	 			// only consider lines with valid articles
    	 			doc = doc + line + "\n";
    	 		}
    	 		
		} catch ( IOException  e) {
			e.printStackTrace();
		} finally{
			try {
			
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
		    ArrayList<Relation> properties = null;		   
			File fout = new File("dbpedia_3Eng_property.ttl");
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			bw.write(doc);	
			String property;
			ObjectMapper mapper = new ObjectMapper();
			try 
			{  String json = Files.toString(new File("properties.json"), Charsets.UTF_8);
			properties =   mapper.readValue(json , new TypeReference<ArrayList<Relation>>(){});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(Relation p : properties) {
				if(p.getLabel() != null) {
				 property = "<" + p.getLabel() +"> " +  "<http://www.w3.org/2000/01/rdf-schema#label> "+  "\"";
				 System.out.println(p.getLabel());
				 int begin = p.getLabel().lastIndexOf("/");
				 int end = p.getLabel().length();
				 String name = p.getLabel().substring(begin+1,end);
			     String[] text = name.split("(?=\\p{Upper})");
			  
				  int i = 0;
					for(String t: text) {
						if(i != text.length-1) {
							property = property + t.toLowerCase() + " ";
						} else {
							property = property + t.toLowerCase() + "\".\n";
						}
						i++;
					}
					
				
				if(!doc.contains(property))
				 bw.write(property);
				// System.out.println(property);
			}}
			
			String q = PREFIX + "select distinct ?x { ?x  a rdf:Property. ?x rdfs:label ?z.  FILTER(STRSTARTS(str(?x),str(dbp:)) ) FILTER regex(str(?z), \"^[a-zA-Z]+$\") FILTER(lang(?z) = 'en') }";
			System.out.println(q);
			QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
			ResultSet rs = qe.execSelect();
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution();
				property = s.toString();
				System.out.println(property);
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
				
				if(!doc.contains(property))
					 bw.write(property);
					 System.out.println(property);
				}
			
			
			bw.close();
			
	}
				
				//System.out.println(s);			
			
				
}		

	

