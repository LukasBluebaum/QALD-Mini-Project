package qa;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.aksw.qa.annotation.index.IndexDBO_properties;
import org.aksw.qa.annotation.spotter.Spotlight;
import org.aksw.qa.commons.datastructure.Entity;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;

public class QuestionProcessor {
	
	private static final String LANGUAGE_TAG = "en"; 
	
	private static final List<String> boolQuestion = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
		
	private static ArrayList<String> compoundWords;
	
	private static ArrayList<String> verbs;
	
	private static ArrayList<String> nouns;
	
	public String processQuestion(String question) throws UnsupportedEncodingException {
		 ArrayList<Entity> entityList = retrieveNamedEntities(question);
		 getKeywords(question);
		 Map<String, List<String>> properties = findProperties();
		 
		 SparqlQueryBuilder builder = new SparqlQueryBuilder(properties,entityList);
		 String result = null;
		 
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
			 case "WHEN": result = builder.sparqlWhen();
			 	 break;
			 default:
				 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) {
					 
				 }
				 if(boolQuestion.contains(starting)) {
					 
				 }			 
			 break;
		 }		
		 return result;
	}
		
	private ArrayList<Entity> retrieveNamedEntities(String question) {
		Spotlight spotlight = new Spotlight();
		return (ArrayList<Entity>) spotlight.getEntities(question).get(LANGUAGE_TAG);
	}
	
	private Map<String, List<String>> findProperties() {
		Map<String, List<String>> properties = new LinkedHashMap<>();
		
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
 		return properties;
 	}
	
	private void getKeywords(String question) {
 		//PrintWriter out = new PrintWriter(System.out);
 		
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
        System.out.println("");
 	}
	
	private ArrayList<String> getCompounds(List<CoreMap> sentences) {
 		ArrayList<String> compoundWords = new ArrayList<String>();

        
        for (CoreMap sentence : sentences) {
            SemanticGraph basicDeps = sentence.get(BasicDependenciesAnnotation.class);
            Collection<TypedDependency> typedDeps = basicDeps.typedDependencies();
         
            System.out.println("typedDeps: "+ typedDeps);
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
	
	private ArrayList<String> getWords(List<CoreMap> sentences, String tag) {
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
 	
 	private ArrayList<String> removeNouns(ArrayList<String> oldNouns) {
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
 	

 	private ArrayList<String> removeVerbs(ArrayList<String> oldVerbs) {
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
}
