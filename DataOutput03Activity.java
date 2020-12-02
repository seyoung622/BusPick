package com.example.buspick;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DataOutput03Activity extends AppCompatActivity {
    private ListView tv_outPut;
    private String[] information;
    private Button detail;
    private String detailInfo = "";
    private String[] StationCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output03);

        detail = (Button)findViewById(R.id.detail);
        TextView info = (TextView)findViewById(R.id.info);
        tv_outPut = (ListView) findViewById(R.id.output);

        Intent intent = getIntent();
        String text = intent.getStringExtra("name");
        text = text.replace("(", "");
        text = text.replace(")", "");

        information = text.split(",");
        info.setText(information[1]);
        ContentValues val = new ContentValues();
        String url_input = IPsetting.IPaddress + "RouteInfoTest.jsp?RID=" + information[0];  //세부페이지 받아올 내용
        DataOutput03Activity.NetworkTask nt = new DataOutput03Activity.NetworkTask(url_input, val);
        nt.execute();

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        DataOutput03Activity.this, DataOutput04Activity.class);
                intent.putExtra("detail", detailInfo);
                startActivity(intent);
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
            String[] divide = s.split("divide");

            detailInfo = divide[0];
            String listData = divide[1];
            //웹페이지 결과에서 html 태그 제거
            listData = listData.replace("<br>", "");
            listData = listData.replace("stationID=", "");
            listData = listData.replace("stationName=", "-");
            listData = listData.replace("routeID=", "(");
            listData = listData.replace("routeName=", ") ");
            listData = listData.replace("\t", "");
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            final ArrayList<String> infoList = new ArrayList<>();
            String[] list = listData.split(";");
            StationCode = new String[list.length];

            for (int i = 0; i < list.length; i++) {
                String[] data = list[i].split(",");
                StationCode[i] = data[0];
                infoList.add(data[1] + "," + data[3]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(DataOutput03Activity.this, android.R.layout.simple_list_item_1, infoList);
            tv_outPut.setAdapter(adapter);
            tv_outPut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                        Intent intent = new Intent(
                                DataOutput03Activity.this, DataOutput02Activity.class);
                        String checkedInfo = (String)parent.getAdapter().getItem(position);
                        String[] splitInfo = checkedInfo.split(",");
                        String sid = StationCode[position];
                        intent.putExtra("name", splitInfo[1] + "," + sid);
                        startActivity(intent);
                }
            });
        }
    }
}
