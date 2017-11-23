package com.example.guill.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ShowableListMenu;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.google.android.flexbox.FlexboxLayout;
import com.pchmn.materialchips.ChipView;

import pl.pawelkleczkowski.customgauge.CustomGauge;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    TextView textViewSpeed;
    TextView textViewRepetedOnomatopoeia;
    FlexboxLayout flexboxLayoutMostRepeted;
    FlexboxLayout flexboxLayoutRepetedOnomatopoeia;
    Button buttonSeeText;
    Button buttonSaveText;

    private CustomGauge speedGaugeFinal;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        /*
        * Init var layout
        * */
        textViewSpeed = (TextView) findViewById(R.id.textViewSpeed);
        textViewRepetedOnomatopoeia = (TextView) findViewById(R.id.textViewRepetedOnomatopoeia);
        flexboxLayoutMostRepeted = (FlexboxLayout) findViewById(R.id.flexboxLayoutMostRepeted);
        flexboxLayoutRepetedOnomatopoeia = (FlexboxLayout) findViewById(R.id.flexboxLayoutRepetedOnomatopoeia);
        speedGaugeFinal = (CustomGauge) findViewById(R.id.speedGaugeFinal);
        buttonSeeText = (Button) findViewById(R.id.buttonSeeText);
        buttonSaveText = (Button) findViewById(R.id.buttonSaveText);

        /*
        * Get var from last activity
        * */
        final String speechResult = getIntent().getStringExtra("speechResult");
        final int totalTime = getIntent().getIntExtra("totalTime", 0);


        /*
        * Init parsed var
        * */
        ParseText parseText = new ParseText(speechResult, totalTime);
        int wordsPerMinute = parseText.wordsPerMinute();
        ArrayList<Word> listMostRepeted = parseText.getMostRepeted(10);
        ArrayList<Word> listRepetedOnomatopoeia = parseText.getMostRepetedOnomatopoeia(6);


        /*
        * Display all elements of layout
        * */
        for (final Word word : listMostRepeted) {
            //System.out.println("Mot : " + word.word + " nb : " + word.iteration);

            final ChipView chipView = new ChipView(ResultActivity.this);
            chipView.setLabel(word.word + " (" + word.iteration + ")");

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            chipView.setLayoutParams(params);

            flexboxLayoutMostRepeted.addView(chipView);
            chipView.setOnChipClicked(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
                    builder.setMessage(getTextFromSynonymeList(word.getSynonymeList()))
                            .setNegativeButton("", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            });
        }

        for (final Word word : listRepetedOnomatopoeia) {
            //System.out.println("Mot : " + word.word + " nb : " + word.iteration);

            final ChipView chipsViewOnomatopoeia = new ChipView(ResultActivity.this);
            chipsViewOnomatopoeia.setLabel(word.word + " (" + word.iteration + ")");

            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 5, 5, 5);
            chipsViewOnomatopoeia.setLayoutParams(params);

            flexboxLayoutRepetedOnomatopoeia.addView(chipsViewOnomatopoeia);
        }
        if (listRepetedOnomatopoeia.size() > 0) {
            textViewRepetedOnomatopoeia.setText("Essayez de les éviter");
        }

        speedGaugeFinal.setValue(wordsPerMinute);

        String textWordsPerMinute = "Votre débit de parole est trop lent : " + String.valueOf(wordsPerMinute) + " Mpm";
        if (wordsPerMinute >= 100) {
            textWordsPerMinute = "Votre débit de parole est lent : " + String.valueOf(wordsPerMinute) + " Mpm";
        }
        if (wordsPerMinute >= 131) {
            textWordsPerMinute = "Votre débit de parole est normal : " + String.valueOf(wordsPerMinute) + " Mpm";
        }
        if (wordsPerMinute >= 181) {
            textWordsPerMinute = "Votre débit de parole est rapide : " + String.valueOf(wordsPerMinute) + " Mpm";
        }
        if (wordsPerMinute >= 211) {
            textWordsPerMinute = "Votre débit de parole est trop rapide : " + String.valueOf(wordsPerMinute) + " Mpm";
        }
        textViewSpeed.setText(textWordsPerMinute);

        buttonSeeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeText(speechResult);
            }
        });

        buttonSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveText(speechResult, totalTime);
            }
        });

    }

    public void seeText(final String speechResult) {
        Intent intent = new Intent(getBaseContext(), ShowText.class);
        //intent.putExtra("ParseText", (Serializable) parseText);
        intent.putExtra("speechResult", speechResult);
        startActivity(intent);
    }

    public void saveText(final String speechResult, final int totalTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Titre");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                DateFormat df = new SimpleDateFormat("dd-MM-HH:mm");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                String name = m_Text + "-" + reportDate;
                Historic historic = new Historic();
                historic.writeJson(name, "historic_heu.json", speechResult, totalTime);
                Toast.makeText(getBaseContext(), "C'est fait !", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }


    public String getTextFromSynonymeList(ArrayList<String> list) {
        String textSynonyme = "";
        int i = 0;
        for (String synonyme: list) {
            textSynonyme = textSynonyme + synonyme;
            textSynonyme = textSynonyme + ", ";
            if (i == 9) {
                break;
            }
            i = i + 1;
        }
        if (textSynonyme == "") {
            textSynonyme = "Aucun synonyme pour ce mot";
        }
        return textSynonyme;
    }
}
