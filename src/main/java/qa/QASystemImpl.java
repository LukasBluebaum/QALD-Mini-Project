package qa;

import jsonbuilder.AbstractQASystem;
import jsonbuilder.AnswerContainer;

import java.io.UnsupportedEncodingException;
import java.util.Set;

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
