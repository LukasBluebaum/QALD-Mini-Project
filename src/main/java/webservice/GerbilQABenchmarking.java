package webservice;

import com.github.jsonldjava.utils.JsonUtils;
import org.aksw.qa.commons.datastructure.Question;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import qa.QASystem;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
public class GerbilQABenchmarking {
	private Logger log = LoggerFactory.getLogger(GerbilQABenchmarking.class);

	@Autowired
	protected QASystem system;
	


	@RequestMapping(value = "/gerbil", method = RequestMethod.POST)
	public String askGerbil(@RequestParam Map<String, String> params, final HttpServletResponse response) throws RuntimeException, IOException {
		String question = params.get("query");
		String lang = params.get("lang");
		log.debug("Received question = " + question);
		log.debug("Language of question = " + lang);
		
		Question q = new Question();
		q.getLanguageToQuestion().put(lang, question);
		
		//call your QA system here 
		JSONObject answer = system.getAnswersToQuestion(q, lang);
		log.info("Got: "+JsonUtils.toPrettyString(answer));
		return JsonUtils.toPrettyString(answer);
	}

	@PreDestroy
	public void close() {
		System.out.println("System will be shutdown");
		system.close();
	}
}