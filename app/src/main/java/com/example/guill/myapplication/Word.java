package com.example.guill.myapplication;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class Word {

    String word;

    int iteration = 0;

    public Word(String word) {

        this.word = word;

        this.iteration ++;
    }

    public Word(String word, int iteration) {

        this.word = word;

        this.iteration = iteration;
    }

    public void addIteration() {

        this.iteration ++;
    }

    public int getIteration() {

        return this.iteration;
    }

    public String getWord() {

        return this.word;
    }
}
