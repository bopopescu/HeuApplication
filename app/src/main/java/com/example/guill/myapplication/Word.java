package com.example.guill.myapplication;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jeremydebelleix on 20/09/2017.
 */

public class Word {

    WordType type;

    String word;

    ArrayList<String> synonymeList;

    int iteration = 0;


    public Word(String word) {

        this.word = word;

        this.setWordType();

        synonymeList = new ArrayList<String>();
        this.iteration ++;
    }

    public Word(String word, int iteration) {

        this.word = word;

        this.setWordType();

        synonymeList = new ArrayList<String>();
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

    public void loadSynonymeList() {
        try {
            getSynonymeThread.start();
            getSynonymeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSynonymeList(ArrayList<String> list) {

        this.synonymeList = list;
        Log.d("set synonyme", " " + this.synonymeList.size());

    }

    public Thread getSynonymeThread = new Thread() {
        public void run() {
            try {
                String word = getWord();

                Document document = Jsoup.connect("http://www.synonymo.fr/synonyme/" + word).get();
                String html = document.body().html();

                Pattern pattern = Pattern.compile("consulter les synonymes de ([^\"]*)");
                Matcher matcher = pattern.matcher(html);

                ArrayList<String> list = new ArrayList<String>();

                while(matcher.find())
                {
                    list.add(matcher.group(1));
                    Log.d("synonyme", matcher.group(1));
                }

                setSynonymeList(list);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public ArrayList<String> getSynonymeList() {
        return this.synonymeList;
    }

}
