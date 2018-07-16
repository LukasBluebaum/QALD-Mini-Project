package qa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.BasicDependenciesAnnotation;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.CoreMap;
import utils.Question;

public class NLPParser {
	private static final  StanfordCoreNLP PIPELINE;
	
	/**
	 * Pre specified verbs, that do not help to find properties.
	 */
	private static final List<String> VERBS = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
	
	private String comparative;
	
	private String superlative;
	
	private String subject;
	
	private ArrayList<String> compoundWords;
	
	private ArrayList<String> verbs;
	
	private ArrayList<String> adjectives;
	
	private ArrayList<String> nouns;
		
	static {
		Properties props = new Properties();
	    props.setProperty("annotators","tokenize, ssplit, pos, depparse, lemma");
	    PIPELINE = new StanfordCoreNLP(props);
	}

	/**
	 * Sets all keywords for the question nouns, verbs etc. .
	 * @param question The current question.
	 */
 	public void annotateQuestion(Question question) {
 		getKeywords(question.question);
 		question.compoundWords = this.compoundWords;
 		question.nouns = this.nouns;
 		question.verbs = this.verbs;
 		question.adjectives = this.adjectives;
 		question.comparative = this.comparative;
 		question.superlative = this.superlative;
 		question.subject = this.subject;
 	}
   	
 	/**
 	 * Retrieves all important keywords including nouns, verbs, adjectives, superlatives and comparatives.
 	 * @param question The current question.
 	 */
	public void getKeywords(String question) {

        System.out.println(question);

        Annotation annotation = new Annotation(question);
        PIPELINE.annotate(annotation);
      
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        compoundWords = getCompounds(sentences);
        verbs = removeVerbs(getWords(sentences, "V"));
        nouns = getWords(sentences, "N");
        adjectives = getWords(sentences, "JJ");
        
        
        List<String> comparativeList = getWords(sentences,"JJR");
        comparative = comparativeList.size() == 1 ? comparativeList.get(0) : null ;
        
        List<String> superlativeList = getWords(sentences,"JJS");
        superlative = superlativeList.size() == 1 ? superlativeList.get(0) : null ;
        
 
        System.out.println("Compounds:");
        for(String compound: compoundWords) {
        	System.out.println(compound);
        }
        
        System.out.println("\nVerbs:");
        for(String verb: verbs) {
        	System.out.println(verb);
        }
        
        System.out.println("\nAdjectives:");
        for(String adjective: adjectives) {
        	System.out.println(adjective);
        }
        
        System.out.println("\nNouns:");
        for(String noun: nouns) {
        	System.out.println(noun);
        }
        System.out.println("");
        
        System.out.println("Comparative:  " + comparative + "\n");
        System.out.println("Superlative:  " + superlative + "\n");
 	}
	
	/**
	 * Retrieves the compound words for the current question, checks the compound and amod dependencies.
	 * @param question Current question
	 * @return List of compoun words inside the current question.
	 */
	private ArrayList<String> getCompounds(List<CoreMap> question) {
 		ArrayList<String> compoundWords = new ArrayList<String>();
     
        for (CoreMap sentence : question) {
            SemanticGraph basicDeps = sentence.get(BasicDependenciesAnnotation.class);
            Collection<TypedDependency> typedDeps = basicDeps.typedDependencies();
         
            System.out.println("typedDeps: "+ typedDeps);
            Iterator<TypedDependency> dependencyIterator = typedDeps.iterator();
            while(dependencyIterator.hasNext()) {
            	TypedDependency dependency = dependencyIterator.next();
            	String depString = dependency.reln().toString();
            	if(depString.equals("compound") || depString.equals("amod")) {
            		String dep = dependency.dep().toString();
            		String gov = dependency.gov().toString();
            		compoundWords.add(dep.substring(0, dep.lastIndexOf("/")) + " " + gov.substring(0, gov.lastIndexOf("/")));
            	}
            	if(depString.equals("nsubj")) {
            		String dep = dependency.dep().toString();
            		subject = dep.substring(0, dep.lastIndexOf("/"));
            	}
            }
        }    
        return compoundWords;
 	}
	
	/**
	 * Retrieves words from the question, depending on the string tag.
	 * N for nouns, V for verbs, JJ for adjectives, JJR for comparatives and JJS for superlatives.
	 * @param question Current question. 
	 * @param tag N for nouns, V for verbs and JJ for adjectives.
	 * @return List of the retrieved words. 
	 */
	private ArrayList<String> getWords(List<CoreMap> question, String tag) {
 		ArrayList<String> words = new ArrayList<String>();
 		
 		for (CoreMap sentence : question) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for(CoreLabel token: tokens) {
               	if(token.tag().startsWith(tag)){
            		String word = token.toString();
            		words.add(word.substring(0, word.lastIndexOf("-")));
            	}
            }
        }       	
 		return words;
 	}
	
 	/**
 	 * Removes pre specified verbs in {@link #VERBS}, that do not help to find properties.
 	 * @param oldVerbs Found verbs in the question.
 	 * @return List of cleaned verbs.
 	 */
 	private ArrayList<String> removeVerbs(ArrayList<String> oldVerbs) {
 		ArrayList<String> verbs = new ArrayList<String>();
 		
 		boolean found = false;
 		for(String verb: oldVerbs) {
 			for(String test: VERBS) {
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
 	
 	/**
 	 * Returns the lemmanization of the given word.
 	 * @param input A single word.
 	 * @return The lemmanization of the given word.
 	 */
 	public String getLemma(String input) {
 		Annotation doc = new Annotation(input);
 		PIPELINE.annotate(doc);
 		List<CoreMap> sentences = doc.get(SentencesAnnotation.class);
 		if(sentences.size() > 1) return null;
 		CoreMap sentence = sentences.get(0);
 		List<CoreLabel> token = sentence.get(TokensAnnotation.class);
 		return token.get(0).get(LemmaAnnotation.class);
 	}
}
