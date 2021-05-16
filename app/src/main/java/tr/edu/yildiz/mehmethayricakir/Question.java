package tr.edu.yildiz.mehmethayricakir;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private static final long serialVersionUID = 6529685098167757690L;
    String question;

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    ArrayList<String> options;
    int correctOptionIndex;
    String attachmentPath;

    public Question(String question, String optionA, String optionB, String optionC, String optionD, String optionE, String attachmentPath, int correctOptionIndex) {
        this.question = question;
        options = new ArrayList<>();
        options.add(optionA);
        options.add(optionB);
        options.add(optionC);
        options.add(optionD);
        options.add(optionE);
        this.attachmentPath = attachmentPath;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
