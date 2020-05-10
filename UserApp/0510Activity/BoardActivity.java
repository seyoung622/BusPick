package com.example.buspick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BoardActivity extends AppCompatActivity {

    private WebView mwv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        mwv =(WebView)findViewById(R.id.activity_board_webview);

        WebSettings mws=mwv.getSettings();//Mobile Web Setting
        mws.setJavaScriptEnabled(true);//자바스크립트 허용
        mws.setLoadWithOverviewMode(true);//컨텐츠가 웹뷰보다 클 경우 스크린 크기에 맞게 조정

        mwv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url=request.getUrl().toString();
                view.loadUrl(url);
                return true;
            }
        });

        WebSettings set = mwv.getSettings();
        set.setLoadWithOverviewMode(true);
        set.setUseWideViewPort(true);

        mwv.loadUrl("http://172.30.1.56:8080/BusPickBoard/main/main.jsp");
    }

    //   추가전에 뒤로가기 이벤트 호출시 홈으로 돌아갔으나, 이젠 일반적인 뒤로가기 기능 활성화
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mwv.canGoBack()) {
                mwv.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mymenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        switch (item.getItemId()){

            case R.id.home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.data:
                intent = new Intent(this, DataOutputActivity.class);
                startActivity(intent);
                return true;

            case R.id.riding:
                intent = new Intent(this, RidingBellActivity.class);
                startActivity(intent);
                return true;

            case R.id.stopA:
                intent = new Intent(this, StopAlarmActivity.class);
                startActivity(intent);
                return true;

            case R.id.stopB:
                intent = new Intent(this, StopBellActivity.class);
                startActivity(intent);
                return true;

            case R.id.shortest:
                intent = new Intent(this, ShortestPathActivity.class);
                startActivity(intent);
                return true;

            case R.id.board:
                intent = new Intent(this, BoardActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
