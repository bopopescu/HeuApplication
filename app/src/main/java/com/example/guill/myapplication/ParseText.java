package com.example.guill.myapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class ParseText {

    String text;
    String[] textClean;
    Double duration;

    ArrayList<Word> wordsList = new ArrayList<Word>();

    /*
    **   Constructor
    */
    public ParseText(String myText, Double duration) {

        this.text = myText;
        this.textClean = cleanText(myText);
        this.duration = duration;

        fillWordTab();

    }

    /*
    **   Remove the punctuation, determinant and other useless words
    */
    public String[] cleanText(String text) {

        // Remove determinant and other words
        text = text.toLowerCase();
      //  text = text.replaceAll();
        text = text.replaceAll(Regex.determinant, "");
        text = text.replaceAll(Regex.other, "");

        Log.e("before replace space", text);

        Log.e("After replace space", text);

        // Create a table of words
        //String[] words = text.split("\\s+");
        String[] words = text.replaceAll("[^a-zA-Zéèçêûùàôöï’' ]+", "").split("\\s+");

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

        Log.d("textclean", "" + this.textClean);
        for(int i = 0; i < this.textClean.length; i++) {

            boolean contains = false;
            String string = this.textClean[i];

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

        while (i < nbr && i < this.wordsList.size()) {
            list.add(this.wordsList.get(i));
            Log.d("list "+i, "" + this.wordsList.get(i).word);
            i++;
        }

        return list;
    }

}
