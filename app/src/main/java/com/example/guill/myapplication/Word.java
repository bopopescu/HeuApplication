package com.example.guill.myapplication;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class Word {

    WordType type;

    String word;

    int iteration = 0;


    public Word(String word) {

        this.word = word;

        this.setWordType();

        this.iteration ++;
    }

    public Word(String word, int iteration) {

        this.word = word;

        this.setWordType();

        this.iteration = iteration;
    }

    public void addIteration() {

        this.iteration ++;
    }

    /**
     * Define the type of the word (normal, or onomatopoeia)
     */
    public void setWordType() {

        if(isOnomatopoeia()) {
            this.type = WordType.Onomatopoeia;
        }
        else {
            this.type = WordType.Normal;
        }
    }

    /**
     *
     * Define if a word is an onomatopoeia
     */
    public boolean isOnomatopoeia() {

        if(Onomatopoeia.list.contains(this.word)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * Getters
     */
    public int getIteration() {

        return this.iteration;
    }

    public String getWord() {

        return this.word;
    }
}
