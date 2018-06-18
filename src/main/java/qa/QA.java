package qa;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import jsonbuilder.AnswerContainer;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import webservice.WebApplication;

public class QA {
	
	 private static final String QUESTION = "What is the highest mountain in Germany?";

	 public enum DebugMode {
	 	DebugOffline,
		 DebugOnline,
		 LoadDataset
	 }

	 private static final DebugMode debugMode = DebugMode.DebugOffline;
	 private static final boolean DEBUG = true;
	
	 public static void main(String[] args) {

		AnswerContainer result = null;
	
		QuestionProcessor processor = new QuestionProcessor();
		if(debugMode == DebugMode.DebugOffline) {
			 try {
					result = processor.processQuestion(QUESTION);
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }		
		} else if(debugMode == DebugMode.LoadDataset){
			List<IQuestion> questions = LoaderController.load(Dataset.QALD6_Test_Multilingual);
			for (IQuestion question : questions) {				
				 try {
					result = processor.processQuestion(question.getLanguageToQuestion().get("en"));
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }				
			}		
		}

		else if (debugMode == DebugMode.DebugOnline) {
			WebApplication.main(new String[0]);
		 }
		System.out.println((result != null) ? result.getAnswers() : "Unfortunately we are not able to provide an answer!");
		
	}
	
}	