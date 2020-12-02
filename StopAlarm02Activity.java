package com.example.buspick;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class StopAlarm02Activity extends AppCompatActivity {



    private Button bt;
    private String[] information;
    private ListView tv_outPut;
    private Button button1;
    private TextView txtResult;
    private double StationXLoc = 0; // 위도
    private double StationYLoc = 0; //경도
    private TextView distance;
    private boolean Flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm02);


        TextView station = (TextView) findViewById(R.id.station); //선택한 정류장
        Intent intent = getIntent();
        station.setText(intent.getStringExtra("sname"));

        String info = station.getText().toString();
        information = info.split("-");


        tv_outPut = (ListView) findViewById(R.id.output);
        distance = (TextView)findViewById(R.id.distance);
        bt = (Button) findViewById(R.id.next);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues val = new ContentValues();
                String url_input = IPsetting.IPaddress+"AlarmLocation.jsp?SID=" + information[1];
                StopAlarm02Activity.NetworkTask nt = new StopAlarm02Activity.NetworkTask(url_input, val);
                nt.execute();

            }
        });

        button1 = (Button)findViewById(R.id.button1);
        txtResult = (TextView)findViewById(R.id.txtResult);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( StopAlarm02Activity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else{
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String provider = location.getProvider();
                    double longitude = location.getLongitude(); // 경도.긴거 실시간 현재 위치
                    double latitude = location.getLatitude(); // 위도 .

                    txtResult.setText("위치정보 : " + provider + "\n" +
                            "경도 : " + longitude + "\n"+
                            "위도 : " + latitude + "\n"
                    );

                    double dist = distance(latitude, longitude,StationYLoc,StationXLoc);
                    distance.setText("정류장까지 거리 : "+"\n"+"약 " + Double.toString(dist)+"m");
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            10000,
                            1,
                            gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            10000,
                            1,
                            gpsLocationListener);
                }
            }
        });
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();



            txtResult.setText("위치정보 : " + provider + "\n" +
                    "경도 : " + longitude + "\n"+
                    "위도 : " + latitude + "\n"
            );
            double dist = distance(latitude, longitude,StationYLoc,StationXLoc);
            distance.setText("정류장까지 거리 : "+"\n"+"약 " + Double.toString(dist)+"m");



            if(dist <= 500  && Flag==false)
            {
                createNotification();


                AlertDialog.Builder builder = new AlertDialog.Builder(StopAlarm02Activity.this);

                builder.setTitle("하차 알람").setMessage("버스 하차 500m 전입니다");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        removeNotification();
                        Flag = true;

                    }
                }
                );

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

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
            s = s.replace("</body></html>","");
            s = s.replace("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>Insert title here</title></head><body>","");

            String delTag = s.replaceAll("<(/)?([a-zA-Z]*)(\\\\s[a-zA-Z]*=[^>]*)?(\\\\s)*(/)?>", "\n");

            String[] locinfo = s.split(",");

            StationXLoc = Double.parseDouble(locinfo[0]); //위
            StationYLoc = Double.parseDouble(locinfo[1]); //경
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.

            ArrayList<String> infoList = new ArrayList<>();

            String[] list = s.split(";");
            for (int i = 0; i < list.length; i++) {
                infoList.add(list[i]);
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(StopAlarm02Activity.this, android.R.layout.simple_list_item_1, infoList);

            tv_outPut.setAdapter(adapter);

        }
    }


    /*
     * 두 좌표 사이의 거리를 계산
     * @param
     *  lat1: 위치 1의 위도
     *  lon1: 위치 1의 경도
     *  lat2: 위치 2의 위도
     *  lon2: 위치 2의 경도
     * @return
     *  dist: 두 좌표 사이의 거리
     */
    public double distance(double lat1,double lon1, double lat2, double  lon2) {
//        double ux = Double.parseDouble(lat1);
//        double uy = Double.parseDouble(lon1);
//        double sx = Double.parseDouble(lat2);
//        double sy = Double.parseDouble(lon2);
        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1609.344;
        return dist;
    }



    /*
     * 라디안 단위 변환
     * @param
     *  deg: 도
     * @return
     *  변환된 라디안 값
     */
    public static double deg2rad(double deg) {
        return (deg / 180.0 * Math.PI);
    }

    /*
     * 각도 단위 변환
     * @param
     *  rad: 라디안 값
     * @return
     *  변환된 각도 값
     */
    public static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }



    private void createNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("하차 알람");
        builder.setContentText("버스 하차 500m 전입니다");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        builder.setColor(Color.RED);
        // 사용자가 탭을 클릭하면 자동 제거
        builder.setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        // id값은
        // 정의해야하는 각 알림의 고유한 int값
        notificationManager.notify(1, builder.build());
    }
    private void removeNotification() {

        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);


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



