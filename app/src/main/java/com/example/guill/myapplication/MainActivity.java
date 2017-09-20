package com.example.guill.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//Import Components
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//End import components

public class MainActivity extends AppCompatActivity {

    EditText editTextSelectFile;
    Button buttonStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextSelectFile = (EditText) findViewById(R.id.editTextSelectFile);
        buttonStart = (Button) findViewById(R.id.buttonStart);

        //Click listener
        editTextSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, 7);
            }
        });
        //End click listener
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        switch (requestCode) {
            case 7:
                if (resultCode == RESULT_OK) {
                    String PathHolder = data.getData().getPath();
                    Toast.makeText(MainActivity.this, PathHolder, Toast.LENGTH_LONG).show();
                    editTextSelectFile.setText(PathHolder);
                }
                break;
        }
    }

}


