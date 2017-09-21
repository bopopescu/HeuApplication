package com.example.guill.myapplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class ParseText {

    String text;
    String[] textTab;
    Double duration;

    ArrayList<Word> wordsList = new ArrayList<Word>();

    Hashtable<String, Integer> table = new Hashtable<String, Integer>();

    /*
    **   Constructor
    */
    public ParseText(String myText, Double duration) {

        this.text = myText;
        this.textTab = removePunctuation(myText);
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

        for(int i = 0; i < this.textTab.length; i++)
        {
            String word = this.textTab[i];

            if(this.table.containsKey(word)) {

                this.table.put(word, this.table.get(word) + 1);
            }
            else {
                this.table.put(word, 1);
            }
        }

        for (String key: this.table.keySet()) {

            Word word = new Word(key, this.table.get(key));

            this.wordsList.add(word);
        }

        Collections.sort(this.wordsList, new CustomComparator());

    }

    /*
    **   Get the most repeted word
    */
    public ArrayList<Word> getMostRepeted(int nbr) {

        ArrayList<Word> list = new ArrayList<Word>();

        int i = 0;

        while (i < nbr) {
            list.add(this.wordsList.get(i));
            i++;
        }

        return list;
    }

}
