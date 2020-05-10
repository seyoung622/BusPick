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
import android.widget.Toast;

import java.util.ArrayList;

public class RidingBell02Activity extends AppCompatActivity {

    private Button bt;
    private ListView tv_outPut;
    private String[] information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_bell02);

        TextView station = (TextView) findViewById(R.id.station); //선택한 정류장
        bt = (Button) findViewById(R.id.search);
        tv_outPut = (ListView) findViewById(R.id.output);

        Intent intent = getIntent();
        station.setText(intent.getStringExtra("sname"));

        String info = station.getText().toString();
        information = info.split(",");
        //information[0] = "2000000000"
        //information[1] = "창하철강"

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues val = new ContentValues();
                String url_input = "http://192.168.81.29:8765/BusPickTest/BusArrivalTest.jsp?SID=" + information[0];
                RidingBell02Activity.NetworkTask nt = new RidingBell02Activity.NetworkTask(url_input, val);
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
            s = s.replace("<br>", "");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title></head><body>", "");
            s = s.replace("stationID=", "");
            s = s.replace("stationName=", "-");
            s = s.replace("</body></html>", "");
            s = s.replace("routeID=", "(");
            s = s.replace("routeName=", ") ");
            String delTag = s.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "\n");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            final ArrayList<String> infoList = new ArrayList<>();

            String[] list = s.split(";");
            for (int i = 0; i < list.length; i++) {
                infoList.add(list[i]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RidingBell02Activity.this, android.R.layout.simple_list_item_1, infoList);

            tv_outPut.setAdapter(adapter);


            tv_outPut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(
                            RidingBell02Activity.this, RidingBell03Activity.class);
                    int check = tv_outPut.getCheckedItemPosition();

                    //리스트뷰에서 선택한 택스트
                    String info = (String) parent.getAdapter().getItem(position);

                    String request = information[0] + ","+information[1] +","+ info;
                    intent.putExtra("bname", request);

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
