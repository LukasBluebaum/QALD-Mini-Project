package qa;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import jsonbuilder.AnswerContainer;
import jsonbuilder.GerbilFinalResponse;
import jsonbuilder.GerbilResponseBuilder;
import webservice.WebApplication;

public class QA {
	
	 public enum DebugMode {
		 	DebugOffline,
			 DebugOnline,
			 LoadDataset
		 }
	
	 private static final DebugMode DEBUGMODE = DebugMode.LoadDataset;
	 
	 private static final Dataset DATASET = Dataset.QALD8_Test_Multilingual;
	
	 private static final String QUESTION = "What is the birth name of Adele?";

	 static List<GerbilResponseBuilder> response = new ArrayList<GerbilResponseBuilder>();
	 private static QASystem system = new QASystemImpl();
	 static JSONParser parser = new JSONParser();
	 
	 
	 public static void main(String[] args) throws ParseException, InterruptedException {

		AnswerContainer result = null;
	
		QuestionProcessor processor = new QuestionProcessor();
		if(DEBUGMODE == DebugMode.DebugOffline) {
			 try {
					result = processor.processQuestion(QUESTION);
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }		
		} else if(DEBUGMODE == DebugMode.LoadDataset){
			List<IQuestion> questions = LoaderController.load(DATASET);
			
			int id = 1;
			for (IQuestion question : questions) {	
				Thread.sleep(3000);
				GerbilFinalResponse resp = system.getAnswersToQuestion2((Question) question, "en");
				GerbilResponseBuilder grb = resp.getQuestions().get(0);
				grb.setId(Integer.toString(id));
				id++;
				response.add(grb);				
			}		
			GerbilFinalResponse finalResponse = new GerbilFinalResponse();
			finalResponse.setList(response);
			
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = null;

			try {
				json = ow.writeValueAsString(finalResponse);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			try {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream("solutionTest.json"), StandardCharsets.UTF_8);
				writer.write(json);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
		} else if (DEBUGMODE == DebugMode.DebugOnline) {
			WebApplication.main(new String[0]);
		}
		System.out.println((result != null) ? result.getAnswers() : "Unfortunately we are not able to provide an answer!");		
	}	
}	