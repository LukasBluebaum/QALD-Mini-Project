package qa;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.qa.annotation.index.IndexDBO_classes;
import org.aksw.qa.annotation.index.IndexDBO_properties;
import org.aksw.qa.annotation.spotter.Spotlight;
import org.aksw.qa.commons.datastructure.Entity;

import jsonbuilder.AnswerContainer;
import utils.Question;

public class QuestionProcessor {
	
	private static final String LANGUAGE_TAG = "en"; 
	
	private static final List<String> BOOLQUESTION = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
		
	private Question q;
	
	private NLPParser nlp;
	
	SparqlQueryBuilder builder; 
	
	/**
	 * Processes the question (finds keywords, entities, properties, classes) then calls the correct SparqlQuery function,
	 * depending on the question type. (mainly the first word of the question)
	 * @param question The current question.
	 * @return Answers to the question.
	 * @throws UnsupportedEncodingException
	 */
	public AnswerContainer processQuestion(String question) throws UnsupportedEncodingException {
		 q = new Question(question);
		 q.questionType = "SELECT";

		 AnswerContainer container = new AnswerContainer();
		 
		 nlp = new NLPParser();
		 nlp.annotateQuestion(q);
		 
		 retrieveNamedEntities(question);
		 findProperties();
		 findClasses();
		 differentiateEntities();
			 
		 builder = new SparqlQueryBuilder(q);
		 Set<String> result = null;
		 
		 String[] tokens = question.split(" ");
		 
		 String starting = tokens[0].toUpperCase();
		 
		 do {
			 switch(starting) {
				 case "WHO":	result = builder.sparqlWho();		 
					 break;
				 case "HOW":	result = builder.sparqlHow();
					 break;
				 case "WHERE":	result = builder.sparqlWhere();
				 	 break;
				 case "WHAT":	result = builder.sparqlWhat();	
					 break;
				 case "WHICH": result = builder.listSparql();
					 break;
				 case "WHEN": 	result = builder.sparqlWhen();
				 System.out.println(result);
				 	 break;
				 default:
					 if(starting.equals("LIST") || starting.equals("NAME") ||  starting.equals("SHOW") || starting.equals("GIVE")) {
						 result = builder.listSparql();
					 }
					 if(BOOLQUESTION.contains(starting)) {
						 		q.questionType = "ASK";
						 		result = builder.simpleASK("","");
					 }	else {
						 result = builder.simpleSparql("", "");
					 }
				 break;
			 }
			 
			 builder.incrementIndex();
		 } while((result == null ||result.isEmpty() ) && q.entityList != null && q.entityList.size()>1 && builder.getEntityIndex()<q.entityList.size());

		 container.setAnswers(result);
		 container.setSparqlQuery(builder.getLastUsedQuery());
		 return container;
	}
		
	/**
	 * Retrieves the named entities through the Spotlight webservice.
	 * @param question The current question.
	 */
	private void retrieveNamedEntities(String question) {
		Spotlight spotlight = new Spotlight();
		q.entityList = (ArrayList<Entity>) spotlight.getEntities(question).get(LANGUAGE_TAG);
	}
	
	/**
	 * Matches the found keywords to properties using the IndexDBO classes from the qa.annotations library.
	 */
	private void findProperties() {
		Map<String, List<String>> properties = new LinkedHashMap<>();
		
 		IndexDBO_properties index = new IndexDBO_properties();
 		for(String verb: q.verbs) {
 			properties.put(verb, index.search(verb));
 		}
 		
 		for(String adjective: q.adjectives) {
 			properties.put(adjective, index.search(adjective));
 		}
 		 
 		for(String noun: q.nouns) {
 			/*if(!noun.equals(q.subject))*/ properties.put(noun, index.search(noun));
 		}
 		
 		for(String compound: q.compoundWords) {
 			String[] com = compound.split(" ");
 			if(!com[0].equals(q.subject)) properties.put(com[0], index.search(com[0]));
 			if(!com[1].equals(q.subject)) properties.put(com[1], index.search(com[1]));
 		}
 		
 		
 	
 		for(String keyword: properties.keySet()) {
 			if(!properties.get(keyword).isEmpty() ) {
	 			String s = SparqlQueryBuilder.rank(  properties.get(keyword)); 			
	 			properties.get(keyword).remove(s);
	            properties.get(keyword).add(0, s);		
 			}
 		}
 		System.out.println("Properties:" + properties);
 		q.properties = properties;
 	}
	
	/**
	 * Matches the found nouns to classes using the IndexDBO_classes from the qa.annotations library.
	 */
	private void findClasses() {
		ArrayList<String> classes = new ArrayList<String>();
		
 		IndexDBO_classes index = new IndexDBO_classes();
 		
		for(String noun: q.nouns) {
			System.out.println(noun);
 			classes.addAll(index.search(nlp.getLemma(noun)));			
 		}
 		
// 		for(String compound: q.compoundWords) {
// 			String[] com = compound.split(" ");
// 			classes.addAll(index.search(com[0]));
// 			classes.addAll(index.search(com[1]));
// 		}
 		System.out.println("Classes:" + classes);
 		q.classes = classes;
 	}
	
	/**
	 * Removes entities from the entity list that can also be matched to a class.
	 * @throws UnsupportedEncodingException
	 */
	private void differentiateEntities() throws UnsupportedEncodingException {
		IndexDBO_classes index = new IndexDBO_classes();
 	
		ArrayList<Entity> newEntityList = new ArrayList<Entity>();
		if(q.entityList == null) return;
 		for(Entity e: q.entityList) {
 			String entity = URLDecoder.decode(e.getUris().get(0).toString(), "UTF-8");
 			entity = entity.substring(entity.lastIndexOf("/")+1);
 			if(index.search(entity.toLowerCase()).size() == 0) {
 				System.out.println(index.search(entity) + "---" + entity);
 				newEntityList.add(e); 				
 			}
 		}

 		q.entityList = newEntityList; 	
 		System.out.println("Entities:" + q.entityList);

	}
}
