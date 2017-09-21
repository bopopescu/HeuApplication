package com.example.guill.myapplication;

/**
 * Created by jeremydebelleix on 21/09/2017.
 */
import java.util.Comparator;

public class CustomComparator implements Comparator<Word> {
    @Override
    public int compare(Word w1, Word w2) {
        return w2.getIteration() - w1.getIteration();
    }
}
