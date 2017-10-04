package com.example.guill.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private final int SPEECH_RECOGNITION_CODE = 1;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;

    private SpeechRecognizer speechRecognizer;
    private Intent intent;

    Button buttonChooseFile;
    public TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChooseFile = (Button) findViewById(R.id.buttonChooseFile);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        initVoiceRecognizer();

    }

    public void startListening(View v) {
        if (speechRecognizer!=null && buttonChooseFile.isActivated() == true) {
            speechRecognizer.cancel();
        }
        if(buttonChooseFile.isActivated() == false) {
            speechRecognizer.startListening(intent);
        }
        buttonChooseFile.setActivated(!buttonChooseFile.isActivated());

        changeButtonText();
    }

    private void changeButtonText() {
        if(buttonChooseFile.isActivated()) {
            buttonChooseFile.setText("Stop recording");
        }
        else {
            buttonChooseFile.setText("Start recording");
        }
    }
    private SpeechRecognizer getSpeechRecognizer(){
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new VoiceListener());
        }
        return speechRecognizer;
    }

    private void initVoiceRecognizer() {
        speechRecognizer = getSpeechRecognizer();
        intent = new  Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }


   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == OPEN_REQUEST_CODE)
            {
                if (resultData != null) {
                    currentUri = resultData.getData();

                    try {
                        String content = readFileContent(currentUri);
                        System.out.println(content);
                        ParseText parseText = new ParseText(content, (double) 960);
                        double wordsPerMinute = parseText.wordsPerMinute();
                        ArrayList<Word> list = new ArrayList<Word>();
                        list = parseText.getMostRepeted(10);
                        //String analyseResult = AnalyseText(content);
                        String wordsRepeted = "";

                        for (Word word: list) {
                            wordsRepeted += word.word + ": " + word.iteration + "\n";
                        }

                        textViewResult.setText("Mots par minute : " + String.valueOf(wordsPerMinute) + "\n"
                                + "Mots les plus utilisés : \n" + wordsRepeted);
                    } catch (IOException e) {
                        // Handle error here
                        e.printStackTrace();
                    }
                }
            }
        }
    } */

    private String AnalyseText(String myText) {
        myText = CleanText(myText);
        System.out.println(myText);
        return "result";
    }

    private String CleanText(String myText) {
        return myText.replaceAll("[-+.^:,]","");
    }


    private String readFileContent(Uri uri) throws IOException {

        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null) {
            stringBuilder.append(currentline + "\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

}

