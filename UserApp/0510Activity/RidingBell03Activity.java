package com.example.buspick;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RidingBell03Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_bell03);

        final TextView bus = (TextView) findViewById(R.id.bus);


        Intent intent = getIntent();

        //인텐트로 넘어온 정보 쪼개기
        //[0] : 정류장 ID
        //[1] : 정류장 이름
        //[2] : 버스 이름
        //[3] : 남은 시간
        final String[] infoArr = intent.getStringExtra("bname").split(",");

        bus.setText(infoArr[1] +"/" +infoArr[2] +"/" + infoArr[3]);

        String info = bus.getText().toString();

        Button bell = (Button) findViewById(R.id.Bell);
        bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RidingBell03Activity.this);
                builder.setTitle("선택한 정류장 정보로 버스에 승차벨을 누르겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues val = new ContentValues();
                        String routeName = infoArr[2].replace("번 버스", "");
                        String url_input = "http://192.168.81.29:8765/BusPickTest/BusRidingRequest.jsp?Rname="+routeName+"&SID="+
                                infoArr[0];
                       // Toast.makeText(getApplicationContext(), url_input, Toast.LENGTH_LONG).show();
                        RidingBell03Activity.NetworkTask nt = new RidingBell03Activity.NetworkTask(url_input, val);
                        nt.execute();

                        Toast.makeText(getApplicationContext(),"승차벨 누르기 완료", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),RidingBell04Activity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("취소",null);
                builder.create().show();
            }
        });

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            s = s.replace("<br>", "");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title></head><body>", "");
            s = s.replace("</body></html>", "");
            //Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
                }
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

