package qa;
import java.io.UnsupportedEncodingException;

public class QA {
	
//	who=http://dbpedia.org/ontology/Person
//		where=http://dbpedia.org/ontology/location
//		in=http://dbpedia.org/ontology/location,http://dbpedia.org/ontology/locatedInArea
//		when=http://dbpedia.org/ontology/date,http://dbpedia.org/property/date
//		find=http://dbpedia.org/ontology/location
//		long=http://dbpedia.org/ontology/length
//		longer=http://dbpedia.org/ontology/length
//		longest=http://dbpedia.org/ontology/length
//		old=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		older=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		oldest=http://dbpedia.org/ontology/openingYear,http://dbpedia.org/ontology/birthDate
//		tall=http://dbpedia.org/ontology/height
//		taller=http://dbpedia.org/ontology/height
//		tallest=http://dbpedia.org/ontology/height
//		high=http://dbpedia.org/ontology/elevation
//		higher=http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/higher
//		highest=http://dbpedia.org/ontology/elevation,http://dbpedia.org/property/highest
//		small=http://dbpedia.org/ontology/areaTotal
//		smaller=http://dbpedia.org/ontology/areaTotal
//		smallest=http://dbpedia.org/ontology/areaTotal
//		large=http://dbpedia.org/ontology/areaTotal
//		larger=http://dbpedia.org/ontology/areaTotal
//		largest=http://dbpedia.org/ontology/areaTotal
//		big=http://dbpedia.org/ontology/areaTotal
//		bigger=http://dbpedia.org/ontology/areaTotal
//		biggest=http://dbpedia.org/ontology/areaTotal
	

	 private static final String question = "List all the musicals with music by Elton John.";
	
	 public static void main(String[] args) {

		String result = null;

		QuestionProcessor processor = new QuestionProcessor();
		 try {
			result = processor.processQuestion(question);
		 } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		 }	
		 System.out.println((result != null) ? result : "Unfortunately we are not able to provide an answer!");
	}
	
}	