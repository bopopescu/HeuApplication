package com.example.guill.myapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class ParseText {

    String text;
    String[] textNoPunctuated;
    Double duration;

    ArrayList<Word> wordsList = new ArrayList<Word>();

    /*
    **   Constructor
    */
    public ParseText(String myText, Double duration) {

        this.text = myText;
        this.textNoPunctuated = removePunctuation(myText);
        this.duration = duration;

        fillWordTab();

    }

    /*
    **   Remove the punctuation
    */
    public String[] removePunctuation(String text) {

        String[] words = text.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

        return words;
    }

    /*
    **   Get the number of the word in the text
    */
    public int getNbWord() {

        String parts[] = this.text.split(" ");

        int nbWord = parts.length;

        return nbWord;
    }

    /*
    **   Get the number of word per minute
    */
    public Double wordsPerMinute() {

        Double time = this.duration / 60;

        Double wordsPerMinute = getNbWord() / time;

        return wordsPerMinute;
    }

    /*
    **   Fill the table which contains words and their iteration
    */
    public void fillWordTab() {

        for(int i = 0; i < this.textNoPunctuated.length; i++) {

            boolean contains = false;
            String string = this.textNoPunctuated[i];

            for(Word word : this.wordsList) {
                if(word.getWord().equals(string)) {
                    word.addIteration();
                    contains = true;
                }
            }
            if(contains == false) {
                this.wordsList.add(new Word(string));
            }
        }

        Log.d("word list after fill", ""+ this.wordsList.size());
        Collections.sort(this.wordsList, new CustomComparator());

    }

    /*
    **   Get the most repeted word
    */
    public ArrayList<Word> getMostRepeted(int nbr) {

        ArrayList<Word> list = new ArrayList<Word>();

        int i = 0;

        Log.d("wordList", " "+ this.wordsList.size());
        while (i < nbr) {
            list.add(this.wordsList.get(i));
            i++;
        }

        return list;
    }

}
