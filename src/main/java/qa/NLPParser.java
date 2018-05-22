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
	private static final  StanfordCoreNLP pipeline;
	
	private static final List<String> boolQuestion = Arrays.asList("DO","DID","HAS","WAS","HAVE","DOES","WERE", "IS", "ARE", "BE");
	
	private String comparative;
	
	private String superlative;
	
	private String subject;
	
	private ArrayList<String> compoundWords;
	
	private ArrayList<String> verbs;
	
	private ArrayList<String> adjectives;
	
	private ArrayList<String> nouns;
		
	static {
		Properties props = new Properties();
	    // props.setProperty("ssplit.eolonly","true");
	    props.setProperty("annotators","tokenize, ssplit, pos, depparse, lemma");
	    //props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
	    pipeline = new StanfordCoreNLP(props);
	}

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
   	
	public void getKeywords(String question) {
 		//PrintWriter out = new PrintWriter(System.out);

        String content = question;
        System.out.println(content);

        Annotation annotation = new Annotation(content);
        pipeline.annotate(annotation);

        //pipeline.prettyPrint(annotation, out);
        
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        compoundWords = getCompounds(sentences);
        verbs = removeVerbs(getWords(sentences, "V"));
        nouns = removeNouns(getWords(sentences, "N"));
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
            	if(c.equals("nsubj")) {
            		String dep = s.dep().toString();
            		subject = dep.substring(0, dep.lastIndexOf("/"));
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
 	
 	public String getLemma(String input) {
 		Annotation noun = new Annotation(input);
 		pipeline.annotate(noun);
 		List<CoreMap> sentences = noun.get(SentencesAnnotation.class);
 		if(sentences.size() > 1) return null;
 		CoreMap sentence = sentences.get(0);
 		List<CoreLabel> token = sentence.get(TokensAnnotation.class);
 		return token.get(0).get(LemmaAnnotation.class);
 	}
}
