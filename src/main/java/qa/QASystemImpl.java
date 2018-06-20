package qa;

import java.io.UnsupportedEncodingException;

import jsonbuilder.AbstractQASystem;
import jsonbuilder.AnswerContainer;

public class QASystemImpl extends AbstractQASystem {
    @Override
    public AnswerContainer retrieveAnswers(String question, String lang) {

        QuestionProcessor questionProcessor = new QuestionProcessor();
        try {
             return questionProcessor.processQuestion(question);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
