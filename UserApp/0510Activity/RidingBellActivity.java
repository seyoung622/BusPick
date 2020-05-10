package com.example.buspick;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.HashMap;


public class RidingBellActivity extends AppCompatActivity {

    private Button btnShowLocation;
    private TextView txtLat;
    private TextView txtLon;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    // GPSTracker class
    private GpsInfo gps;
    private ListView tv_outPut;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riding_bell);

        btnShowLocation = (Button) findViewById(R.id.btn_start);
        txtLat = (TextView) findViewById(R.id.tv_latitude);
        txtLon = (TextView) findViewById(R.id.tv_longitude);
        tv_outPut = (ListView) findViewById(R.id.output);
        bt = (Button)findViewById(R.id.search);



        // GPS 정보를 보여주기 위한 이벤트 클래스 등록
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // 권한 요청을 해야 함
                if (!isPermission) {
                    callPermission();
                    return;
                }

                gps = new GpsInfo(RidingBellActivity.this);
                // GPS 사용유무 가져오기
                if (gps.isGetLocation()) {

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    txtLat.setText(String.valueOf(latitude));
                    txtLon.setText(String.valueOf(longitude));


                    Toast.makeText(
                            getApplicationContext(),
                            "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    // GPS 를 사용할수 없으므로
                    gps.showSettingsAlert();
                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues val = new ContentValues();
                String url_input = "http://192.168.81.29:8765/BusPickTest/NearStationTest.jsp?Loc="+txtLat.getText().toString()+","+txtLon.getText().toString();
                        //+37.3418968+","+126.7319852;
                RidingBellActivity.NetworkTask nt = new NetworkTask(url_input, val);
                nt.execute();

            }
        });

        callPermission();  // 권한 요청을 해야 함
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
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
        //http 통신 결과
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //웹페이지 결과에서 html 태그 제거
            s = s.replace("<br>","");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title></title></head><body>","");
            s = s.replace("stationID=","");
            s = s.replace("stationName=","-");
            s = s.replace("</body></html>","");
            s = s.replace("routeID=","(");
            s = s.replace("routeName=",") ");
            String delTag = s.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "\n");

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            final ArrayList<String> infoList = new ArrayList<>();

            String[] list = s.split(";");
            for(int i = 0; i < list.length; i++){
                infoList.add(list[i]);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RidingBellActivity.this, android.R.layout.simple_list_item_1, infoList);

            tv_outPut.setAdapter(adapter);


          tv_outPut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(
                            RidingBellActivity.this, RidingBell02Activity.class);
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
