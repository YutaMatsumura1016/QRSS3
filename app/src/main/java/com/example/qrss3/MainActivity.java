package com.example.qrss3;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

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

