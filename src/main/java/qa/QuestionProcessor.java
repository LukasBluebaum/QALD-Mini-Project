package qa;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
	
	private NLPParser nlp;

	
	public String processQuestion(String question) throws UnsupportedEncodingException {
		 q = new Question(question);
		 q.questionType = "SELECT";
		 
		 nlp =  new NLPParser();
		 nlp.annotateQuestion(q);
		 
		 retrieveNamedEntities(question);
		 findProperties();
		 findClasses();
		 differentiateEntities();
			 
		 SparqlQueryBuilder builder = new SparqlQueryBuilder(q);
		 String result = null;
		 
		 String[] tokens = question.split(" ");
		 
		 String starting = tokens[0].toUpperCase();
		 switch(starting) {
			 case "WHO":	result = builder.sparqlWho();		 
				 break;
			 case "HOW":	result = builder.sparqlHow();
				 break;		 
			 case "WHERE":	result = builder.sparqlWhere();			 
			 	 break;
			 case "WHAT":	result = builder.sparqlWhat();	
				 break;
			 case "WHICH":
				 break;
			 case "WHEN": 	result = builder.sparqlWhen();
			 	 break;
			 default:
				 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) {
					 result = builder.listSparql();
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
 		
 		for(String adjective: q.adjectives) {
 			properties.put(adjective, index.search(adjective));
 		}
 		
 		//TODO: 
 		for(String noun: q.nouns) {
 			/*if(!noun.equals(q.subject))*/ properties.put(noun, index.search(noun));
 		}
 		
 		for(String compound: q.compoundWords) {
 			String[] com = compound.split(" ");
 			if(!com[0].equals(q.subject)) properties.put(com[0], index.search(com[0]));
 			if(!com[1].equals(q.subject)) properties.put(com[1], index.search(com[1]));
 		}
 		System.out.println("Properties:" + properties);
 		q.properties = properties;
 	}
	
	private void findClasses() {
		ArrayList<String> classes = new ArrayList<String>();
		
 		IndexDBO_classes index = new IndexDBO_classes();
 		
		for(String noun: q.nouns) {
 			classes .addAll(index.search(nlp.getLemma(noun)));			
 		}
 		
 		for(String compound: q.compoundWords) {
 			String[] com = compound.split(" ");
 			classes.addAll(index.search(com[0]));
 			classes.addAll(index.search(com[1]));
 		}
 		System.out.println(classes);
 		q.classes = classes;
 	}
	
	private void differentiateEntities() throws UnsupportedEncodingException {
		IndexDBO_classes index = new IndexDBO_classes();
 	
		ArrayList<Entity> newEntityList = new ArrayList<Entity>();
		if(q.entityList == null) return;
 		for(Entity e: q.entityList) {
 			String entity = URLDecoder.decode(e.getUris().get(0).toString(), "UTF-8");
 			entity = entity.substring(entity.lastIndexOf("/")+1);
 			if(index.search(entity).size() == 0) newEntityList.add(e);
 		}

 		q.entityList = newEntityList; 		
	}
}
