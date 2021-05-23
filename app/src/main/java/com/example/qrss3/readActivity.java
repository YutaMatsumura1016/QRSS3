package com.example.qrss3;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import java.util.List;


public class readActivity extends AppCompatActivity {

    String readingString;
    SoundPool soundPool;
    int soundpi;
    String idmString;
    static String gate;
    NfcAdapter nfcAdapter;
    private CompoundBarcodeView barcodeView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ゲートの引継ぎ
        Intent intent = getIntent();
        gate = intent.getStringExtra("gate");

        if(gate.equals("早稲田")){
            setTheme(R.style.Waseda);
        }else if(gate.equals("戸山")){
            setTheme(R.style.Toyama);
        }

        setContentView(R.layout.activity_read);

        readingString = "読み取り中(" + gate + "キャンパス)";
        TextView nowReading = (TextView) findViewById(R.id.nowReading);
        nowReading.setText(readingString);

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

    //バーコードリーダー
    public void barcodeReader() {
        barcodeView.decodeSingle(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);
                idmString = result.getText();
                TextView resultText = (TextView) findViewById(R.id.resultText);
                String resultTextIDM = "idm: " + idmString;
                resultText.setText(resultTextIDM);
                WebView myWebView = (WebView) findViewById(R.id.webView1);
                String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString + "&&gate=" + gate;

                //GASに送信、結果が帰ってきたら次の読み込み開始

                myWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        makeDialog();
                    }
                });
                myWebView.loadUrl(sentURL);
            }


            @Override
            public void possibleResultPoints(List resultPoints) {
            }
        });
    }

    //何も表示しないダイアログを2秒で消すことでQRコード読み取りシステムを再起動
    private void makeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                barcodeReader();
            }
        });

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {}

        dialog.cancel();
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


    //NFC読み取り
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
            idmString = sb.toString();
            Log.d("hoge", idmString);


            //音を鳴らしてSSに送る
            final Handler mainHandler = new Handler(Looper.getMainLooper());

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.activity_read);
                    WebView myWebView = (WebView) findViewById(R.id.webView1);
                    TextView resultText = (TextView) findViewById(R.id.resultText);
                    String resultTextIDM = "idm: " + idmString;
                    //アプリ内ブラウザを使用
                    myWebView.setWebViewClient(new WebViewClient());
                    String sentURL = "https://script.google.com/a/wasedasai.net/macros/s/AKfycbw9BMWL3BLRhB8ZlIs32scTBWceP0TYy28wnWtBD2btOatmNiiw/exec?idm=" + idmString + "&&gate=" + gate;
                    myWebView.loadUrl(sentURL);
                    Log.d("huga", sentURL);

                    soundPool.play(soundpi, 1.0f, 1.0f, 0, 0, 1);

                    resultText.setText(resultTextIDM);

                    //バーコードリーダーの再起動
                    barcodeView = (CompoundBarcodeView) findViewById(R.id.barcodeView);
                    barcodeView.resume();
                    barcodeReader();

                    //読み取りキャンパスの再設定
                    TextView nowReading = (TextView) findViewById(R.id.nowReading);
                    nowReading.setText(readingString);
                }
            });


        }
    }


}
