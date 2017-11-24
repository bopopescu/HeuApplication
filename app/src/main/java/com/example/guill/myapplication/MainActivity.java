package com.example.guill.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class MainActivity extends AppCompatActivity {


    private LottieAnimationView animationView;
    private Chronometer chronometer;
    private CustomGauge speedGauge;

    int nbWords = 0;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    LinearLayout contentDrawer;

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


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        animationView = (LottieAnimationView) findViewById(R.id.lottieAnimationView);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        speedGauge = (CustomGauge) findViewById(R.id.speedGauge);

        //definir notre toolbar en tant qu'actionBar
        setSupportActionBar(toolbar);

        //afficher le bouton retour
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, 0, 0);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        contentDrawer = (LinearLayout) findViewById(R.id.contentDrawer);


        speechResult = "";
        listHistoric();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void listHistoric() {
        TextView textViewRecordHistoricTitle = new TextView(MainActivity.this);
        textViewRecordHistoricTitle.setText("Mes enregistrement");
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(0, 20, 0, 0); // llp.setMargins(left, top, right, bottom);
        textViewRecordHistoricTitle.setLayoutParams(llp);
        textViewRecordHistoricTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        contentDrawer.addView(textViewRecordHistoricTitle);
        final Historic historic = new Historic();
        ArrayList<String> list = historic.getRecords("historic_heu.json");
        for (final String recordName: list) {
            final TextView textViewRecordHistoric = new TextView(MainActivity.this);
            textViewRecordHistoric.setText(recordName);
            textViewRecordHistoric.setPadding(20, 20, 0, 0);
            contentDrawer.addView(textViewRecordHistoric);
            textViewRecordHistoric.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), ResultActivity.class);
                    String speechResult2 = historic.getValue("historic_heu.json", textViewRecordHistoric.getText().toString(), "text");
                    intent.putExtra("speechResult", speechResult2);
                    int totalTime2 = Integer.parseInt(historic.getValue("historic_heu.json", textViewRecordHistoric.getText().toString(), "time"));
                    intent.putExtra("totalTime", totalTime2);
                    startActivity(intent);
                }
            });
        }
    }

    private void startResultActivity() {
        this.nbWords = 0;
        Intent intent = new Intent(getBaseContext(), ResultActivity.class);
        intent.putExtra("speechResult", speechResult);
        intent.putExtra("totalTime", totalTime);
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

            finishReconizer();

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

    public void finishReconizer() {
        System.out.println("Enter in finishReconizer");
        speechRecognizer.destroy();
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
                finishReconizer();
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
                finishReconizer();
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

    @Override
    public void onResume(){
        super.onResume();
        if((contentDrawer).getChildCount() > 0){
            (contentDrawer).removeAllViews();
        }
        listHistoric();
        speechResult = "";
    }

}
