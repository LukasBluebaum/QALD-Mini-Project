package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aksw.qa.commons.datastructure.Entity;

public class Question {
	
	public ArrayList<String> compoundWords;
	
	public ArrayList<String> verbs;
	
	public ArrayList<String> nouns;
	
	public String comparative;
	
	public String superlative;
	
	public String subject;

	public Map<String, List<String>> properties;
	
	public ArrayList<String> classes;
	
	public ArrayList<Entity> entityList;
	
	public String question;
	
	public String questionType;
	
	public Question(String question) {
		this.question = question;
	}
	
//	public String getComparative() {
//		return comparative;
//	}
//
//	public void setComparative(String comparative) {
//		this.comparative = comparative;
//	}
//
//	public String getSuperlative() {
//		return superlative;
//	}
//
//	public void setSuperlative(String superlative) {
//		this.superlative = superlative;
//	}
//
//	public String getSubject() {
//		return subject;
//	}
//
//	public void setSubject(String subject) {
//		this.subject = subject;
//	}
//
//	public ArrayList<String> getVerbs() {
//		return verbs;
//	}
//
//	public void setVerbs(ArrayList<String> verbs) {
//		this.verbs = verbs;
//	}
//
//	public ArrayList<String> getCompoundWords() {
//		return compoundWords;
//	}
//
//	public void setCompoundWords(ArrayList<String> compoundWords) {
//		this.compoundWords = compoundWords;
//	}
//
//	public ArrayList<String> getNouns() {
//		return nouns;
//	}
//
//	public void setNouns(ArrayList<String> nouns) {
//		this.nouns = nouns;
//	}
	
	
}
