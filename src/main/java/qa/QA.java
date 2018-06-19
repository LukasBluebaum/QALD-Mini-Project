package qa;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jsonbuilder.AnswerContainer;
import jsonbuilder.GerbilFinalResponse;
import jsonbuilder.GerbilResponseBuilder;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import webservice.WebApplication;

public class QA {
	
	 private static final String QUESTION = "What is the highest mountain in Germany?";

	 public enum DebugMode {
	 	DebugOffline,
		 DebugOnline,
		 LoadDataset
	 }

	 static List<GerbilResponseBuilder> response = new ArrayList<GerbilResponseBuilder>();
	 private static QASystem system = new QASystemImpl();
	 private static final DebugMode debugMode = DebugMode.LoadDataset;
	 private static final boolean DEBUG = true;
	 static JSONParser parser = new JSONParser();
	 public static void main(String[] args) throws ParseException {

		AnswerContainer result = null;
	
		QuestionProcessor processor = new QuestionProcessor();
		if(debugMode == DebugMode.DebugOffline) {
			 try {
					result = processor.processQuestion(QUESTION);
				 } catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				 }		
		} else if(debugMode == DebugMode.LoadDataset){
			List<IQuestion> questions = LoaderController.load(Dataset.QALD8_Test_Multilingual);
				  
			for (IQuestion question : questions) {		
			
				GerbilFinalResponse resp = system.getAnswersToQuestion2((Question) question, "en");
				GerbilResponseBuilder grb = resp.getQuestions().get(0);
				grb.setId(question.getId());
				
				System.out.println("ID" + grb.getId());
				response.add(grb);
				
//				 //	result = processor.processQuestion(question.getLanguageToQuestion().get("en"));
//					 JSONObject answer = system.getAnswersToQuestion((Question) question, "en");
//					 JSONArray q =  (JSONArray) answer.get("questions");
//					 JSONObject s = (JSONObject) q.get(0);
//					
//					 try {  
//						System.out.println(q.get(0));
//						GerbilResponseBuilder grb = objectMapper.readValue(s.toJSONString() ,GerbilResponseBuilder.class);
//						 grb.setId(Integer.toString(i));
//						 response.add(grb);
//						 System.out.println(q);
//						 i++;
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				
				
			}		
			GerbilFinalResponse finalResponse = new GerbilFinalResponse();
			finalResponse.setList(response);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = null;

			try {
				json = ow.writeValueAsString(finalResponse);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(response);
//			System.out.println("------");
//			System.out.println(parser.parse(json));
			JSONObject obj = (JSONObject) parser.parse(json);
			 try (FileWriter file = new FileWriter("solution.json")) {
					file.write(json);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		}

		else if (debugMode == DebugMode.DebugOnline) {
			WebApplication.main(new String[0]);
		 }
		System.out.println((result != null) ? result.getAnswers() : "Unfortunately we are not able to provide an answer!");
		
	}
	
}	