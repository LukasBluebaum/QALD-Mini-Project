package qa;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.aksw.qa.annotation.index.IndexDBO_classes;
import org.aksw.qa.annotation.index.IndexDBO_properties;
import org.aksw.qa.annotation.spotter.Spotlight;
import org.aksw.qa.commons.datastructure.Entity;

import utils.Question;

public class QuestionProcessor {
	
	private static final String LANGUAGE_TAG = "en"; 
	
	private static final List<String> boolQuestion = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
		
	private Question q;

	
	public String processQuestion(String question) throws UnsupportedEncodingException {
		 q = new Question(question);
		 q.questionType = "SELECT";
		 
		 NLPParser nlp =  new NLPParser();
		 nlp.annotateQuestion(q);
		 
		 retrieveNamedEntities(question);
		 findProperties();
		 findClasses();
			 
		 SparqlQueryBuilder builder = new SparqlQueryBuilder(q);
		 String result = null;
		 
		 String[] tokens = question.split(" ");
		 
		 String starting = tokens[0].toUpperCase();
		 switch(starting) {
			 case "WHO":	result = builder.sparqlWho();		 
				 break;
			 case "HOW":	result = builder.sparqlHow();
				 break;		 
			 case "WHERE":	 		
			 	 break;
			 case "WHAT":
				 break;
			 case "WHICH":
				 break;
			 case "WHEN": //result = builder.sparqlWhen();
			 	 break;
			 default:
				 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) {
					 
				 }
				 if(boolQuestion.contains(starting)) {
					 		q.questionType = "ASK";
					 		result = builder.simpleASK("","");
				 }			 
			 break;
		 }		
		 return result;
	}
		
	private void retrieveNamedEntities(String question) {
		Spotlight spotlight = new Spotlight();
		q.entityList = (ArrayList<Entity>) spotlight.getEntities(question).get(LANGUAGE_TAG);
	}
	
	private void findProperties() {
		Map<String, List<String>> properties = new LinkedHashMap<>();
		
 		IndexDBO_properties index = new IndexDBO_properties();
 		for(String verb: q.verbs) {
 			properties.put(verb, index.search(verb));
 		}
 		
 		//TODO: 
 		for(String noun: q.nouns) {
 			if(!noun.equals(q.subject)) properties.put(noun, index.search(noun));
 		}
 		
 		for(String compound: q.compoundWords) {
 			String[] com = compound.split(" ");
 			if(!com[0].equals(q.subject)) properties.put(com[0], index.search(com[0]));
 			if(!com[1].equals(q.subject)) properties.put(com[1], index.search(com[1]));
 		}
 		System.out.println(properties);
 		q.properties = properties;
 	}
	
	private void findClasses() {
		ArrayList<String> classes = new ArrayList<String>();
		
 		IndexDBO_classes index = new IndexDBO_classes();
 		
 		for(String noun: q.nouns) {
 			classes.addAll(index.search(noun));
 			
 		}
 		
 		for(String compound: q.compoundWords) {
 			String[] com = compound.split(" ");
 			classes.addAll(index.search(com[0]));
 			classes.addAll(index.search(com[1]));
 		}
 		System.out.println(classes);
 		q.classes = classes;
 	}
}
