package com.example.qrss3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    static String gate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonWaseda = findViewById(R.id.buttonWaseda);
        Button buttonToyama = findViewById(R.id.buttonToyama);


        buttonWaseda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gate = "早稲田";

                Intent intent = new Intent(getApplication(), readActivity.class);
                intent.putExtra("gate", gate);
                startActivity(intent);
            }
        });

        buttonToyama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gate = "戸山";

                Intent intent = new Intent(getApplication(), readActivity.class);
                intent.putExtra("gate", gate);
                startActivity(intent);
            }
        });


    }

}

