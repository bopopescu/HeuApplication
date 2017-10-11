package com.example.guill.myapplication;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by jeremydebelleix on 27/09/2017.
 */

public class VoiceListener implements RecognitionListener {

    String result;

    public void onReadyForSpeech(Bundle params) {}
    public void onBeginningOfSpeech() {}
    public void onRmsChanged(float rmsdB) {}
    public void onBufferReceived(byte[] buffer) {}
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech");
    }
    public void onError(int error) {
        Log.v(TAG, "error lol " + error);
    }
    public void onResults(Bundle results) {
        String str = new String();
        Log.v(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            Log.v(TAG, "result " + data.get(i));
            str = "" + data.get(i);
        }

        this.result = str;

        Log.e("hello", ""+str);
    }
    public void onPartialResults(Bundle partialResults) {}
    public void onEvent(int eventType, Bundle params) {}

    public String getresult() {
        return this.result;
    }
}
