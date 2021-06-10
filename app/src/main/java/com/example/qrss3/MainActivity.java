package com.example.qrss3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    static String gate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView saiImage = (ImageView) findViewById(R.id.Sai);
        WebView loginWebView = (WebView) findViewById(R.id.webView2);

        Button buttonWaseda = findViewById(R.id.buttonWaseda);
        Button buttonToyama = findViewById(R.id.buttonToyama);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonClose = findViewById(R.id.buttonCloseweb);


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


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saiImage.setVisibility(View.INVISIBLE);
                loginWebView.setVisibility(View.VISIBLE);
                buttonWaseda.setVisibility(View.INVISIBLE);
                buttonToyama.setVisibility(View.INVISIBLE);
                buttonLogin.setVisibility(View.INVISIBLE);
                buttonClose.setVisibility(View.VISIBLE);

                loginWebView.setWebViewClient(new WebViewClient());
                String sentURL = "https://docs.google.com/document/d/16huuwNOUEYedL3L0Ig1YwP8e9mESXeoebFzlmjy9NQ0/edit?usp=sharing";
                loginWebView.loadUrl(sentURL);
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saiImage.setVisibility(View.VISIBLE);
                loginWebView.setVisibility(View.INVISIBLE);
                buttonWaseda.setVisibility(View.VISIBLE);
                buttonToyama.setVisibility(View.VISIBLE);
                buttonClose.setVisibility(View.INVISIBLE);
            }
        });

    }

}

