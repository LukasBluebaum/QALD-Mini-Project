package qa;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;

public class QA {
	
	 private static final String QUESTION = "What is the highest mountain in Germany?";
	 
	 private static final boolean DEBUG = true;
	
	 public static void main(String[] args) {

		Set<String> result = null;
	
		QuestionProcessor processor = new QuestionProcessor();
		if(DEBUG) {
			 try {
					result = processor.processQuestion(QUESTION);
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }		
		} else {
			List<IQuestion> questions = LoaderController.load(Dataset.QALD6_Test_Multilingual);
			for (IQuestion question : questions) {				
				 try {
					result = processor.processQuestion(question.getLanguageToQuestion().get("en"));
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }				
			}		
		}		
		System.out.println((result != null) ? result : "Unfortunately we are not able to provide an answer!");
		
	}
	
}	