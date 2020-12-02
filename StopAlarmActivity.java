package com.example.buspick;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class StopAlarmActivity extends AppCompatActivity {
    private EditText search;
    private Button bt;
    private ListView tv_outPut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);

        search = (EditText)findViewById(R.id.alarmsearch);
        bt = (Button)findViewById(R.id.alarmbutton);
        tv_outPut = (ListView) findViewById(R.id.output);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues val = new ContentValues();
                String url_input = IPsetting.IPaddress+"StationFindTest.jsp?Sname="+search.getText().toString();
                StopAlarmActivity.NetworkTask nt = new StopAlarmActivity.NetworkTask(url_input, val);
                //cmd - ipconfig - IPv4 확인. IP주소 바꿔주기
                nt.execute();
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
            //웹페이지 결과에서 html 태그 제거
            s = s.replace("<br>","");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title></head><body>","");
            s = s.replace("stationID=","");
            s = s.replace(",","");
            s = s.replace("stationName=","-");
            s = s.replace("</body></html>","");
            s = s.replace("routeID=","(");
            s = s.replace("routeName=",") ");
            String delTag = s.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "\n");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            ArrayList<String> infoList = new ArrayList<>();

            String[] list = s.split(";");
            for(int i = 0; i < list.length; i++){
                infoList.add(list[i]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(StopAlarmActivity.this, android.R.layout.simple_list_item_1, infoList);

            tv_outPut.setAdapter(adapter);

            tv_outPut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(
                            StopAlarmActivity.this, StopAlarm02Activity.class);
                    int check = tv_outPut.getCheckedItemPosition();
                    String info = (String)parent.getAdapter().getItem(position);
                    intent.putExtra("sname",info);

                    startActivity(intent);
                }
            });//
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
