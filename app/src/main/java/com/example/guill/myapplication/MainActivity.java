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


    private LottieAnimationView animationView;
    private Chronometer chronometer;
    private CustomGauge speedGauge;

    int nbWords = 0;

    Button synonymeButton;
    TextView textViewResult;
    TextView onomatopoeiaTextView;
    TextView synonymeTextView;

    Boolean isRecording = false;
    String speechResult = "";

    int totalTime;

    Intent intent;
    private static final String TAG = "spk2txtD2";

    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        speedGauge = (CustomGauge) findViewById(R.id.speedGauge);

        speechResult = "";

    }

    /*public void synonymePressed(View v) {

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
    }*/

    private void startResultActivity() {

        /*ParseText parseText = new ParseText(speechResult, totalTime);
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

        String onomatopoeiaRepeted = "";

        for (Word word : onomatopoeiaList) {
            onomatopoeiaRepeted += word.word + ": " + word.iteration + "\n";
        }*/

        this.nbWords = 0;

        Intent intent = new Intent(getBaseContext(), ResultActivity.class);
        //intent.putExtra("ParseText", (Serializable) parseText);
        intent.putExtra("speechResult", speechResult);
        intent.putExtra("totalTime", totalTime);
        //intent.putExtra("list", list);
        /*intent.putExtra("onomatopoeiaList", onomatopoeiaList);*/
        //intent.putExtra("wordsPerMinute", wordsPerMinute);
        startActivity(intent);
    }

    public void manageRecord(View v) {

        if (isRecording == false) {
            isRecording = true;
            //recordButton.setText("Stop recording");

            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();

            animationView.loop(true);
            animationView.playAnimation();

            startRecording();
        }
        else {
            chronometer.stop();

            totalTime = getTime();

            animationView.loop(false);

            isRecording = false;

            finish();

            Log.d("before result activity", "" + speechResult);
           // speechResult = "Permettez-moi euh euh euh d’abord, avant euh ben toute chose, cet après-midi, d’avoir ben une pensée pour nos deux compatriotes qui ont été lâchement assassinées hier à Marseille. Il est encore trop tôt pour qualifier avec la certitude requise ce qui s’est passé et le ministre de l’Intérieur aura à le faire dans les prochaines heures. Et d’avoir dans le même temps, puisque nous sommes ici plongés au cœur du vaste monde à travers votre représentation, une pensée également émue pour nos amis américains qui ont eu à subir, eux aussi, la violence contemporaine à Las Vegas, il y a quelques heures.";
            startResultActivity();

        }
    }

    private int getTime() {

        long timeElapsed = SystemClock.elapsedRealtime() - chronometer.getBase();

        int seconds = (int) timeElapsed / 1000;

        return seconds;
    }

    private void startRecording() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new listener(this));

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        //specify the max number of results
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        //User of SpeechRecognizer to "send" the intent.
        speechRecognizer.startListening(intent);
    }

    private String CleanText(String myText) {
        return myText.replaceAll("[-+.^:,]", "");
    }

    @Override
    public void finish() {
        System.out.println("Enter in finish");
        speechRecognizer.destroy();
        //super.finish();
    }

    class listener implements RecognitionListener {

        private MainActivity activity;

        public listener(MainActivity activity) {
            this.activity = activity;
        }

        private ArrayList<ArrayList<String>> matches = new ArrayList<>();
        public void onReadyForSpeech(Bundle params)	{
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech(){
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB){}
        public void onBufferReceived(byte[] buffer)	{
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()	{
            System.out.println("end of speech");
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)	{
            Log.d(TAG,  "error " +  error);
            if (error == 7 || error == 5 || error == 2 || error == 6) {
                Log.d(TAG,  "ENTER ERROR");
                speechRecognizer.stopListening();
                speechRecognizer.startListening(intent);
            }
            if (error == 8) {
                Log.d(TAG,  "ENTER ERROR 8");
                finish();
                startRecording();
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
                finish();
            }
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
           // ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            this.matches.add(partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));

            if (matches.size() > this.activity.nbWords) {
                this.activity.nbWords = this.matches.size();
            }

            int time = getTime();
            Log.d("calc time", ""+ time);
            Log.d("calc chrono", "" + chronometer.getBase());
            Log.d("calc real time", "" + SystemClock.elapsedRealtime());

           // Log.d("calcul time", "" );

            float var = (float)this.activity.nbWords / (float) time;

            Log.d("activity nb word", "" + activity.nbWords);
            Log.d("calc var", ""+ var);

            float wordsPerMinute = (var * 60);
            Log.d("calc wordsperminute", ""+ wordsPerMinute);

            speedGauge.setValue((int)wordsPerMinute);

        }

        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

}
