package qa;

import org.aksw.qa.commons.datastructure.Question;
import org.json.simple.JSONObject;

/**
 * QA System interface to 
 */
public interface QASystem {

	/**
	 * Gets a {@link org.aksw.qa.commons.datastructure.Question Question} element with the question
	 * obtainable using  {@link org.aksw.qa.commons.datastructure.Question#getLanguageToQuestion() question.getLanguageToQuestion().get(lang)} <br/>
	 * <br/>
	 * The response has to be in the <a href="https://github.com/dice-group/gerbil/wiki/Question-Answering#web-service-interface">QALD JSON format</a>  
	 * 
	 * @param question The question object which holds the question as a string for a specific language
	 * @param lang the language which is asked
	 * @return a QALD JSON object 
	 */
	public JSONObject getAnswersToQuestion(final Question question, String lang);
	
	
	/**
	 * Closes all the resources used
	 */
	public void close();
}
