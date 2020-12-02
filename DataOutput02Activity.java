/*
검섹 정류장, 세부 페이지
 */
package com.example.buspick;

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
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataOutput;
import java.util.ArrayList;

public class DataOutput02Activity extends AppCompatActivity {

    private Button bt;
    private ListView tv_outPut;
    private String[] information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output02);

        TextView info = (TextView) findViewById(R.id.info);
        bt = (Button) findViewById(R.id.next);
        tv_outPut = (ListView) findViewById(R.id.output);

        Intent intent = getIntent();
        info.setText(intent.getStringExtra("name"));


        String text = info.getText().toString();
        information = text.split(",");

        if(information.length >1){
            ContentValues val = new ContentValues();
            String url_input = IPsetting.IPaddress + "BusArrivalTest.jsp?SID=" + information[1];  //세부페이지 받아올 내용
            DataOutput02Activity.NetworkTask nt = new DataOutput02Activity.NetworkTask(url_input, val);
            nt.execute();
        }
        else{
            ContentValues val = new ContentValues();
            String url_input = IPsetting.IPaddress + "BusArrivalTest.jsp?SID=" + information[0];  //세부페이지 받아올 내용
            DataOutput02Activity.NetworkTask nt = new DataOutput02Activity.NetworkTask(url_input, val);
            nt.execute();
        }
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
            s = s.replace("<br>", "");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title></head><body>", "");
            s = s.replace("stationID=", "");
            s = s.replace("stationName=", "-");
            s = s.replace("</body></html>", "");
            s = s.replace("routeID=", "(");
            s = s.replace("routeName=", ") ");
            s = s.replace("\t", "");
            String delTag = s.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "\n");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            final ArrayList<String> infoList = new ArrayList<>();

            String[] list = s.split(";");
            if(list.length == 0){
                infoList.add("도착 정보 없음");
            }
            else {
                for (int i = 0; i < list.length; i++) {
                    infoList.add(list[i]);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DataOutput02Activity.this, android.R.layout.simple_list_item_1, infoList);

            tv_outPut.setAdapter(adapter);


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

            case R.id.board:
                intent = new Intent(this, BoardActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
