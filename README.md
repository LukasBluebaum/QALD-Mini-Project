# QALD-Mini-Project


# Instructions
Simple Usage example: Create an `QuestionProcessor` and call the `processQuestion` function with a string as argument.
The function will then return an `AnswerContainer` with the answers to the question.

```java

public static void main(String[] args) throws UnsupportedEncodingException {
      String question = "When was Albert Einstein Born?";
      QuestionProcessor processor = new QuestionProcessor();
      AnswerContainer result = processor.processQuestion(question);
}
```

In the `QA` class from the `qa` package there are also a few usage examples. See these variables at the top of the class.

```java
   public enum DebugMode {
		 	DebugOffline,
			 DebugOnline,
			 LoadDataset
	 }
   
	 private static final DebugMode DEBUGMODE = DebugMode.LoadDataset;
	 
	 private static final Dataset DATASET = Dataset.QALD8_Test_Multilingual;
	
	 private static final String QUESTION = "When was Albert Einstein Born?";
```

1. Set `DEBUGMODE` to `DebugMode.DebugOffline` 
    - Then the question specified in `QUESTION` will be answered.
1. Set `DEBUGMODE` to `DebugMode.LoadDataset` 
    - Then all questions in the specified dataset `DATASET` will be answered and written to a JSON-file. Which then can be used to               benchmark the system via gerbil.
3. Set `DEBUGMODE` to `DebugMode.DebugOnline` 
    - Then the localhost can be requested via
    `curl -d "query=When was Albert Einstein Born?&lang=en" -X POST http://localhost:8080/gerbil`
