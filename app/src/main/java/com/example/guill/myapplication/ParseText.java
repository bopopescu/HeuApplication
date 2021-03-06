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
    int duration;

    ArrayList<Word> wordsList = new ArrayList<Word>();
    ArrayList<Word> onomatopoeiaList = new ArrayList<Word>();

    /*
    **   Constructor
    */
    public ParseText(String myText, int duration) {

        this.text = myText;
        this.textClean = cleanText(myText);
        this.duration = duration;

        fillWordTab();

    }

    /*
    **   Remove the punctuation, determinant and other useless words
    */
    public String[] cleanText(String text) {
        text = text.toLowerCase();

        // Remove determinant and other words
        text = text.replaceAll(Regex.determinant, "");
        text = text.replaceAll(Regex.other, "");

        // Create a table of words
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
    public int wordsPerMinute() {
        float var = (float)getNbWord() / (float)this.duration;

        int result = (int)(var * 60);

        return result;
    }
    /*
    **   Fill the table which contains words and their iteration
    */
    public void fillWordTab() {

        Log.d("fill wordtab", "hello");

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

        fillOnomatopoeiaList();

        Log.d("word list after fill", ""+ this.wordsList.size());
        Collections.sort(this.wordsList, new CustomComparator());
        Collections.sort(this.onomatopoeiaList, new CustomComparator());

    }

    public void fillOnomatopoeiaList() {

        Log.d("fill onomatopee", "hello");
        for(Word word: this.wordsList) {

            if(word.type == WordType.Onomatopoeia) {
                this.onomatopoeiaList.add(word);
                //this.wordsList.remove(word);
            }
        }
    }

    /*
    **   Get the most repeted word
    */
    public ArrayList<Word> getMostRepeted(int nbr) {

        Log.d("fill onomatopee", "getMostrepeted word");

        ArrayList<Word> list = new ArrayList<Word>();

        int i = 0;

        while (i < nbr && i < this.wordsList.size()) {
           // Word word = this.wordsList.get(i);
            this.wordsList.get(i).loadSynonymeList();
            Log.d("get most repeted", "" + this.wordsList.get(i).synonymeList.size());
            list.add(this.wordsList.get(i));
            i++;
        }

        return list;
    }

    public ArrayList<Word> getMostRepetedOnomatopoeia(int nbr) {

        Log.d("fill onomatopee", "getMostrepeted onomatopée");

        ArrayList<Word> list = new ArrayList<Word>();

        int i = 0;

        while (i < nbr && i < this.onomatopoeiaList.size()) {
            list.add(this.onomatopoeiaList.get(i));
            i++;
        }

        return list;
    }

}
