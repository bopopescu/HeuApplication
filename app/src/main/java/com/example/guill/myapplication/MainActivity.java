package com.example.guill.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {

    Button synonymeButton;
    Chronometer chronometer;
    TextView textViewResult;
    TextView onomatopoeiaTextView;
    TextView synonymeTextView;
    private LottieAnimationView animationView;
    //private LottieAnimationView timerAnimationView;
    private CustomGauge gauge1;


    Boolean isRecording = false;
    String speechResult = "";

    long startTime;
    long stopTime;
    Double totalTime;

    Intent intent;
    private static final String TAG = "spk2txtD2";

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        synonymeButton = (Button) findViewById(R.id.synonymeButton);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        onomatopoeiaTextView = (TextView) findViewById(R.id.onomatopoeiaTextView);
        synonymeTextView = (TextView) findViewById(R.id.synonymeTextView);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        this.synonymeTextView.bringToFront();
        this.synonymeTextView.setVisibility(this.synonymeTextView.INVISIBLE);

        gauge1 = (CustomGauge) findViewById(R.id.gauge1);

        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
      //  timerAnimationView = (LottieAnimationView) findViewById(R.id.timerAnimationView);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new listener());

        int background = getApplicationContext().getResources().getColor(R.color.backgroundBlack);
        getWindow().getDecorView().setBackgroundColor(background);

        chronometer.setTextSize(30);

        gauge1.setEndValue(300);
        gauge1.setStartValue(20);
        gauge1.setValue(100);
    }

    public void synonymePressed(View v) {

        if (this.synonymeButton.isActivated() == false) {
            this.synonymeButton.setText("Hide synonymes");
            synonymeButton.setActivated(true);

            this.synonymeTextView.setVisibility(this.synonymeTextView.VISIBLE);
          //  this.recordButton.setVisibility(this.recordButton.INVISIBLE);

        } else {
            this.synonymeButton.setActivated(false);
            this.synonymeButton.setText("Show synonymes");
            this.synonymeTextView.setVisibility(this.synonymeTextView.INVISIBLE);
           // this.recordButton.setVisibility(this.recordButton.VISIBLE);

        }
    }

    private void analyzeText() {

        ParseText parseText = new ParseText(speechResult, totalTime);
        int wordsPerMinute = parseText.wordsPerMinute();

        ArrayList<Word> list = new ArrayList<Word>();
        ArrayList<Word> onomatopoeiaList = new ArrayList<Word>();

        list = parseText.getMostRepeted(5);
        onomatopoeiaList = parseText.getMostRepetedOnomatopoeia(20);
        // String analyseResult = AnalyseText(content);
        String wordsRepeted = "";
        String synonymeText = "SYNONYMES PROPOSÉS";
        for (Word word : list) {
            Log.d("synonyme list", "" + word.synonymeList.size());
            wordsRepeted += word.word + ": " + word.iteration + "\n";
            synonymeText += word.getSynonyme();
        }

        synonymeTextView.setText(synonymeText);

        textViewResult.setText("Mots par minute : " + String.valueOf(wordsPerMinute) + "\n"
                + "Mots les plus utilisés : \n" + wordsRepeted);

        String onomatopoeiaRepeted = "";

        for (Word word : onomatopoeiaList) {
            onomatopoeiaRepeted += word.word + ": " + word.iteration + "\n";
        }

        onomatopoeiaTextView.setText("Onomatopées les plus utilisés : \n" + onomatopoeiaRepeted);

    }

    public void manageRecord(View v) {

        if (isRecording == false) {
            isRecording = true;
            //recordButton.setText("Stop recording");
            startTime = System.currentTimeMillis();

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            startRecording();

            animationView.loop(true);
            animationView.playAnimation();

           // timerAnimationView.loop(true);
            // timerAnimationView.playAnimation();


        } else {

            chronometer.stop();

            isRecording = false;
           // recordButton.setText("Start recording");
            stopTime = System.currentTimeMillis();

            totalTime = (double)((stopTime - startTime) / 1000);
            Log.d("total time", "" + totalTime);

            // Launch text analyze

            analyzeText();

            animationView.loop(false);
        }
    }

    private void startRecording() {

        speechResult = "";

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //specify the max number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //User of SpeechRecognizer to "send" the intent.
        speechRecognizer.startListening(intent);
    }

    private String AnalyseText(String myText) {
        myText = CleanText(myText);
        System.out.println(myText);
        return "result";
    }

    private String CleanText(String myText) {
        return myText.replaceAll("[-+.^:,]", "");
    }


    @Override
    public void finish() {
        speechRecognizer.destroy();
        speechRecognizer = null;
        super.finish();
    }

    class listener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params)	{
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech(){
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB){
            //Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)	{
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()	{
            System.out.println("end of speech");
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)	{
            Log.d(TAG,  "error " +  error);
            if (error == 7) {
                Log.d(TAG,  "ENTER ERROR 7 " +  "error 7");
                speechRecognizer.stopListening();
                speechRecognizer.startListening(intent);
            }
        }
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults " + results);
            // Fill the list view with the strings the recognizer thought it could have heard, there should be 5, based on the call
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            Log.d(TAG, "result " + matches.get(0));
            speechResult = speechResult + matches.get(0);
            if (isRecording == true) {
                speechRecognizer.startListening(intent);
            }
            else {
                    System.out.println(speechResult);
                }
            }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
            ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            Log.d(TAG, "Partial result " + matches.get(0));
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

}
