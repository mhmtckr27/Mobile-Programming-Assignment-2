package tr.edu.yildiz.mehmethayricakir;

import android.net.Uri;

public class Question {
    String question;
    String[] options;
    int correctOptionIndex;
    Uri uri;

    public Question(String question, String[] options, Uri uri, int correctOptionIndex) {
        this.question = question;
        this.options = options;
        this.uri = uri;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
