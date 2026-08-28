package ro.fr33styler.typeraceroffline;

import java.util.ArrayList;
import java.util.List;

public class Session {

    private int index;
    private int typedWords;
    private int lastTypedWords;
    private double sumOfDeltaTypedWords;
    private int lengthOfDeltaTypedWords;

    private int typedLetters;
    private Object highlight;

    private final List<String> words = new ArrayList<>();

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTypedWords() {
        return typedWords;
    }

    public void setTypedWords(int typedWords) {
        this.typedWords = typedWords;
    }

    public int getLastTypedWords() {
        return lastTypedWords;
    }

    public void setLastTypedWords(int lastTypedWords) {
        this.lastTypedWords = lastTypedWords;
    }

    public double getSumOfDeltaTypedWords() {
        return sumOfDeltaTypedWords;
    }

    public void setSumOfDeltaTypedWords(double sumOfDeltaTypedWords) {
        this.sumOfDeltaTypedWords = sumOfDeltaTypedWords;
    }

    public int getLengthOfDeltaTypedWords() {
        return lengthOfDeltaTypedWords;
    }

    public void setLengthOfDeltaTypedWords(int lengthOfDeltaTypedWords) {
        this.lengthOfDeltaTypedWords = lengthOfDeltaTypedWords;
    }

    public int getTypedLetters() {
        return typedLetters;
    }

    public void setTypedLetters(int typedLetters) {
        this.typedLetters = typedLetters;
    }

    public Object getHighlight() {
        return highlight;
    }

    public void setHighlight(Object highlight) {
        this.highlight = highlight;
    }

    public List<String> getWords() {
        return words;
    }

    public String getWordAt(int index) {
        return index < words.size() ? words.get(index) : "";
    }

}
