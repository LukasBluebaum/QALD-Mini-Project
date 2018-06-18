package jsonbuilder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.aksw.qa.commons.datastructure.Question;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jsonbuilder.AnswerContainer.AnswerType;
import qa.QASystem;

/**
 * An abstract layer for the qa system
 *
 */
public abstract class AbstractQASystem implements QASystem {

	protected static final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\/%=~_|]";
	
	@SuppressWarnings("unchecked")
	private JSONObject getAnswersAsQALD(Set<String> answers, AnswerType answerType) {
		if(answerType==null) {
			answerType = guessAnswerType(answers);
		}
		String varName = answerType.toString().toLowerCase();
		JSONObject answerJson = new JSONObject();
		JSONObject head = new JSONObject();
		JSONArray varArr = new JSONArray();
		if (answerType.equals(AnswerType.BOOLEAN)) {
			// boolean answers are handled different
			answerJson.put("head", head);
			answerJson.put("boolean", Boolean.valueOf(answers.iterator().next()));
			return answerJson;
		}
		varArr.add(varName);
		head.put("vars", varArr);

		JSONObject results = new JSONObject();
		JSONArray bindings = new JSONArray();
		if(answers == null)
		{
			answers = new HashSet<String>();
		}
		for (String answer : answers) {	
			
				System.out.println("???");
			JSONObject binding = new JSONObject();
			JSONObject var = new JSONObject();
			var.put("type", "literal");
			switch (answerType) {
			case RESOURCE:
				var.put("type", answerType.toString().toLowerCase());
				var.put("value", answer);
				break;
			default:
				// is literal
				processLiteral(answer, var);
				break;
			}

			binding.put(varName, var);
			bindings.add(binding);
		}
		results.put("bindings", bindings);
		answerJson.put("head", head);
		answerJson.put("results", results);

		return answerJson;
	}

	/**
	 * Guesses the broad AnswerType (Resource, boolean, literal)
	 * 
	 * @param answers
	 * @return
	 */
	public AnswerType guessAnswerType(Set<String> answers) {
		if(answers != null && answers.size()>=1) {
			Iterator<String> answerIt = answers.iterator();
			//check if boolean
			String answer = answerIt.next().toLowerCase();
			if(answers.size()==1 && answer.equals("true") || answer.equals("false")) {
				return AnswerType.BOOLEAN;
			}
			//check if all are uris
			boolean isUri=answer.matches(REGEX_URI);
			while(answerIt.hasNext()&&isUri) {
				answer = answerIt.next().toLowerCase();
				isUri &= answer.matches(REGEX_URI);
			}
			if(isUri) {
				return AnswerType.RESOURCE;
			}
			//otherwise assume it is a literal
			return AnswerType.LITERAL;
			
		}
		
		//otherwise its empty
		return AnswerType.EMPTY;
	}

	@SuppressWarnings("unchecked")
	private void processLiteral(String answer, JSONObject var) {
		// regular expression to check if answer got a lang tag
		boolean hasLangTag = answer.matches(".*@\\w+");
		// matches either a ^^<whatever> or ^^xml:tag datatype
		boolean hasDatatypeTag = answer.matches(".*\\^\\^(<[^<>]+>|xml:\\w+)");
		int offset = answer.length();
		if (hasLangTag) {
			int langStartIndex = answer.lastIndexOf("@");
			String lang = answer.substring(langStartIndex + 1);
			var.put("xml:lang", lang);
			offset = langStartIndex;
		} else if (hasDatatypeTag) {
			int datatypeStartIndex = answer.lastIndexOf("^^");
			String datatype = answer.substring(datatypeStartIndex + 2);
			if (datatype.startsWith("<")) {
				datatype = datatype.substring(1, datatype.length() - 1);
			}
			var.put("datatype", datatype);
			offset = datatypeStartIndex;
		}
		int beginIndex = 0;
		if (answer.startsWith("'''")) {
			beginIndex = 3;
			offset -= 3;
		}
		if (answer.startsWith("\"") || answer.startsWith("'")) {
			beginIndex = 1;
			offset -= 1;
		}
		var.put("value", answer.substring(beginIndex, offset));
	}

	@Override
	public JSONObject getAnswersToQuestion(final Question q, String lang) {
		// gets the prefered question
		String question = q.getLanguageToQuestion().get(lang);

		// retrieve the answers from the system
		AnswerContainer answers = retrieveAnswers(question, lang);
		// sets the answers
		q.setGoldenAnswers(lang, answers.getAnswers());
		// sets the answertype as lower case (e.g. resource)

		if(answers.getType() != null)
			q.setAnswerType(answers.getType().toString().toLowerCase());
		//
		q.setSparqlQuery(lang, answers.getSparqlQuery());
		try {
			q.setAnswerAsQALDJSON(getAnswersAsQALD(answers.getAnswers(), answers.getType()));

			// return the answer as a valid qaldJSON string
			GerbilFinalResponse resp = new GerbilFinalResponse();

			resp.setQuestions(q, lang);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json;

			json = ow.writeValueAsString(resp);
			return (JSONObject) new JSONParser().parse(json);
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void close() {
		//empty function
	}

	/**
	 * Retrieves the Answers from the System for a particular question and its
	 * language. <br/>
	 * It has to set the answers as a set, the answer types and the sparql query
	 * used
	 * 
	 * @param question
	 * @param lang
	 * @return
	 */
	public abstract AnswerContainer retrieveAnswers(String question, String lang);
	
}
