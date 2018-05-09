import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

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

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class QA {
	
//	who=http://dbpedia.org/ontology/Person
//		where=http://dbpedia.org/ontology/location
//		in=http://dbpedia.org/ontology/location,http://dbpedia.org/ontology/locatedInArea
//		when=http://dbpedia.org/ontology/date,http://dbpedia.org/property/date
//		find=http://dbpedia.org/ontology/location
//		long=http://dbpedia.org/ontology/length
//		longer=http://dbpedia.org/ontology/length
//		longest=http://dbpedia.org/ontology/length
//		old=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		older=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		oldest=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		tall=http://dbpedia.org/ontology/height
//		taller=http://dbpedia.org/ontology/height
//		tallest=http://dbpedia.org/ontology/height
//		high=http://dbpedia.org/ontology/elevation
//		higher=http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/higher
//		highest=http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/highest
//		small=http://dbpedia.org/ontology/areaTotal
//		smaller=http://dbpedia.org/ontology/areaTotal
//		smallest=http://dbpedia.org/ontology/areaTotal
//		large=http://dbpedia.org/ontology/areaTotal
//		larger=http://dbpedia.org/ontology/areaTotal
//		largest=http://dbpedia.org/ontology/areaTotal
//		big=http://dbpedia.org/ontology/areaTotal
//		bigger=http://dbpedia.org/ontology/areaTotal
//		biggest=http://dbpedia.org/ontology/areaTotal
	

	private static String question = "When was Olof Palme shot?";
	private static  Map<String, List<String>> properties = new LinkedHashMap<>();
	private static final String PREFIX = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
									   + "PREFIX dbp:  <http://dbpedia.org/property/> \n"
									   + "PREFIX dbo:  <http://dbpedia.org/ontology/> \n"
									   + "PREFIX    : <http://dbpedia.org/resource/>  \n"
									   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
									   + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n";
	
	
	private static  List<String> boolQuestion = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
	private static final String service = "http://dbpedia.org/sparql";
	private static String query;
	
	private static String result;
	
	private static ArrayList<String> compoundWords;
	
	private static  ArrayList<Entity> entityList;
	
	private static ArrayList<String> verbs;
	
	private static ArrayList<String> nouns;
	
	 public static void main(String[] args) throws IOException {
		
		 
		 getKeywords(question);
	//	/* if(args[0] != null)
	//		 question = args[0];
	//	 */
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
		 
//		IndexDBO_properties index = new IndexDBO_properties();
//		System.out.println(index.search("active"));
	//	 
		 //String sparql = "SELECT DISTINCT ?pred WHERE { ?pred a rdf:Property} ORDER BY ?pred";
		 
		Spotlight fox = new Spotlight();
		entityList = (ArrayList<Entity>) fox.getEntities(question).get("en");
		 
		 for(Entity e : entityList) {
			 System.out.println(e);
		 }
	//	 for (Map.Entry<String, List<Entity>> entry : entities.entrySet())		 {		
	//			entityList.add((Entity) entry.getValue());
	//			
	//	 }
	//		     System.out.println(entry.getKey() + "/" + entry.getValue().get(0).getPosTypesAndCategories());
				 
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
		 case "WHEN": findProperty();
		 				result = sparqlWhen();
		 				System.out.println(result);
		 			 
			 
			 
			 break;
		 default:
			 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) 
			 {
				 
			 }
			 if(boolQuestion.contains(starting))
			 {
				 
			 }
			 
			 break;
		 		
		 
		 
		 }
		 
	}

 	private static String sparqlWhen() throws UnsupportedEncodingException {
 		for(String verb: properties.keySet()) {
 			ArrayList<String> s = (ArrayList<String>) properties.get(verb);
 			for(String sp : s)
	 			if(sp != null) {
	 				String entitiy = java.net.URLDecoder.decode(entityList.get(0).getUris().get(0).toString(), "UTF-8");
	 				query = "SELECT ?date WHERE{<" + entitiy + "> <" + sp + "> ?date ." + " FILTER ( datatype(?date) = xsd:date )}";
	 				//result = query(query);
	 				System.out.println(sp);
	 				if(result != null) return result;
	 			}
 		}
 		return lastOptionWhen();
 	}
 	
 	
 	private static String lastOptionWhen() {
 		query = "SELECT ?date WHERE{<" + entityList.get(0).getUris().get(0) + "> ?property ?date ." + " FILTER ( datatype(?date) = xsd:date )}";
 		return result = query(query);
 	}

	private static void findProperty() {
 		IndexDBO_properties index = new IndexDBO_properties();
 		for(String verb: verbs) {
 			properties.put(verb, index.search(verb));
 		}
 		
 		for(String noun: nouns) {
 			properties.put(noun, index.search(noun));
 		}
 		
 		for(String compound: compoundWords) {
 			String[] com = compound.split(" ");
 			properties.put(com[0], index.search(com[0]));
 			properties.put(com[1], index.search(com[1]));
 		}
 	}

	public static void getKeywords(String question) {
 		PrintWriter out = new PrintWriter(System.out);
 		
 		Properties props = new Properties();
       // props.setProperty("ssplit.eolonly","true");
        props.setProperty("annotators","tokenize, ssplit, pos, depparse");
 		 //props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String content = question;
        System.out.println(content);

        Annotation annotation = new Annotation(content);
        pipeline.annotate(annotation);

        //pipeline.prettyPrint(annotation, out);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        compoundWords = getCompounds(sentences);
        verbs = removeVerbs(getWords(sentences, "V"));
        nouns = removeNouns(getWords(sentences, "N"));
        
 
        System.out.println("Compounds:");
        for(String compound: compoundWords) {
        	System.out.println(compound);
        }
        
        System.out.println("\nVerbs:");
        for(String verb: verbs) {
        	System.out.println(verb);
        }
        
        System.out.println("\nNouns:");
        for(String noun: nouns) {
        	System.out.println(noun);
        }
 	}
 	
 	
 	public static ArrayList<String> getCompounds(List<CoreMap> sentences) {
 		ArrayList<String> compoundWords = new ArrayList<String>();

        
        for (CoreMap sentence : sentences) {
            SemanticGraph basicDeps = sentence.get(BasicDependenciesAnnotation.class);
            Collection<TypedDependency> typedDeps = basicDeps.typedDependencies();
         
            System.out.println("typedDeps ==>  "+ typedDeps);
            Iterator<TypedDependency> t = typedDeps.iterator();
            while(t.hasNext()) {
            	TypedDependency s = t.next();
            	String c = s.reln().toString();
            	//TODO: check for compounds that are longer than two words
            	if(c.equals("compound") || c.equals("amod")) {
            		String dep = s.dep().toString();
            		String gov = s.gov().toString();
            		compoundWords.add(dep.substring(0, dep.lastIndexOf("/")) + " " + gov.substring(0, gov.lastIndexOf("/")));
            	}
            }
        }
     
        return compoundWords;
 	}
 	
 	public static ArrayList<String> getWords(List<CoreMap> sentences, String tag) {
 		ArrayList<String> words = new ArrayList<String>();
 		
 		for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for(CoreLabel t: tokens) {
            	if(t.tag().startsWith(tag)){
            		String word = t.toString();
            		words.add(word.substring(0, word.lastIndexOf("-")));
            	}
            }
        }
        	
 		return words;
 	}
 	
 	public static ArrayList<String> removeNouns(ArrayList<String> oldNouns) {
 		ArrayList<String> nouns = new ArrayList<String>();
 		
 		boolean found = false;
 		for(String noun: oldNouns) {
 			for(String compound: compoundWords) {
 				String[] com = compound.split(" ");
 				for(String c: com) {
 					if(c.equals(noun)) {
 						found = true;
 					}
 				}
 			}
 			if(!found) {
 				nouns.add(noun);
 			}
 			found = false;
 		}
 		
 		return nouns;
 		
 	}
 	

 	public static ArrayList<String> removeVerbs(ArrayList<String> oldVerbs) {
 		ArrayList<String> verbs = new ArrayList<String>();
 		
 		boolean found = false;
 		for(String verb: oldVerbs) {
 			for(String test: boolQuestion) {
 				if(verb.toUpperCase().equals(test)) {
 					found = true;
 				}
 			}
 			if(!found) {
 				verbs.add(verb);
 			}
 			found = false;
 		}
 		
 		return verbs;
 		
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
		
		
		
		public static String query(String q) {
			
			String result= "";
			q = PREFIX + q;
			System.out.println(q);
			QueryExecution qe = QueryExecutionFactory.sparqlService(service, q);
			ResultSet rs = qe.execSelect();
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution();
				result = result + s + " ";			
			}
			return (result.equals("")) ? null : result;		
	}
	
	
}	