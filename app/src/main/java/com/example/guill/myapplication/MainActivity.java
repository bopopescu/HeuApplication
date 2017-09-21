package com.example.guill.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.net.Uri;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int CREATE_REQUEST_CODE = 40;
    private static final int OPEN_REQUEST_CODE = 41;
    private static final int SAVE_REQUEST_CODE = 42;

    Button buttonChooseFile;
    TextView textViewResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonChooseFile = (Button) findViewById(R.id.buttonChooseFile);
        textViewResult = (TextView) findViewById(R.id.textViewResult);

        //Click listener
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile;
                Intent intent;
                chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("*/*");
                intent = Intent.createChooser(chooseFile, "Choose a file");
                startActivityForResult(intent, OPEN_REQUEST_CODE);

            }
        });
        //End click listener
    }

    @Override
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
    }

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