package com.example.qrss3;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
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
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.util.Arrays;
import java.util.List;


public class readActivity extends AppCompatActivity {

    SoundPool soundPool;
    int soundpi;
    static String gate;
    NfcAdapter nfcAdapter;
    private CompoundBarcodeView barcodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        //ゲートの引継ぎ
        Intent intent = getIntent();
        gate = intent.getStringExtra("gate");

        //nfcAdapter初期化
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //オーディオ
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(1)
                .build();


        soundpi = soundPool.load(this, R.raw.pi_cut, 1);
        nfcAdapter.enableReaderMode(readActivity.this, new MyReaderCallback(), NfcAdapter.FLAG_READER_NFC_F, null);

        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcodeView);
        barcodeView.resume();
        barcodeReader();
    }


    public void barcodeReader() { //読み取り処理を行うメソッド
        barcodeView.decodeSingle(new BarcodeCallback() { //読み取りを行う
            @Override
            public void barcodeResult(BarcodeResult result) {
                String readIDM = result.getText();
                Log.d("huga", readIDM);
                sendURL(readIDM); //result.getText()で読み取り結果を取得し、ダイアログのメソッドへ

            }

            @Override
            public void possibleResultPoints(List resultPoints) {
            }
        });
    }

    private void sendURL(String idmString){
        new AlertDialog.Builder(this)
//              .setMessage(mStr + "でOK?")
                .setPositiveButton("OK!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 何かしらの処理
                        barcodeReader(); //もう一回呼び出すことで擬似的に連続読み取り
                    }
                })
                .setCancelable(false)
                .show();

                soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);
                TextView resultText = (TextView)findViewById(R.id.resultText);
                String resultTextIDM = "idm: " + idmString;
                resultText.setText(resultTextIDM);
                WebView myWebView = (WebView) findViewById(R.id.webView1);

                //アプリ内ブラウザを使用
                myWebView.setWebViewClient(new WebViewClient());
                String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString + "&&gate=" + gate;
                myWebView.loadUrl(sentURL);
                Log.d("hugaaaa", sentURL);
    }


    @Override
    protected void onResume(){
        super.onResume();

        // NFCがかざされたときの設定
        Intent intent = new Intent(this, this.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // ほかのアプリを開かないようにする
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

        //バーコードリーダー
        barcodeView.resume();
    }

    @Override
    protected void onPause(){
        super.onPause();

        // Activityがバックグラウンドになったときは受け取らない
        nfcAdapter.disableForegroundDispatch(this);

        //バーコードリーダー
        barcodeView.pause();
    }


    //NFC読み取り結果
    class MyReaderCallback implements NfcAdapter.ReaderCallback {

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void onTagDiscovered(Tag tag) {
            //idmを取得
            byte[] idm = tag.getId();
            Log.d("piyo", "タグを読み取れたよ");

            //16進数に変換
            StringBuilder sb = new StringBuilder();
            for (byte d : idm) {
                sb.append(String.format("%02X", d));
            }
            String idmString = sb.toString();
            Log.d("hoge", idmString);



            //音を鳴らしてSSに送る
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.activity_read);
                    WebView myWebView = (WebView)findViewById(R.id.webView1);
                    TextView resultText = (TextView)findViewById(R.id.resultText);
                    String resultTextIDM = "idm: " + idmString;
                    //アプリ内ブラウザを使用
                    myWebView.setWebViewClient(new WebViewClient());
                    String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString + "&&gate=" + gate;
                    myWebView.loadUrl(sentURL);
                    Log.d("huga", sentURL);

                    soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);
                    resultText.setText(resultTextIDM);
                }
            });
        }
    }

}
