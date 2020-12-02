/*
버스 상세정보
0 routeID=210000027,
1 routeName=12,
2 routeType=일반형시내버스,
3 startStationName=삼미시장,
4 endStationName=김포공항국내선(9번홈),
5 startFirstTime=04:30,
6 startLastTime=22:20,
7 endFirstTime=05:50,
8 endLastTime=23:40,
9 peekMinAllocation=10,
10 peekMaxAllocation=20,
11 districtCode=2
 */
package com.example.buspick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DataOutput04Activity extends AppCompatActivity {
    private TextView ToperationArea;
    private TextView TrouteName;
    private TextView ToperatingTime;
    private TextView TintervalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_output04);
        ToperationArea = (TextView)findViewById(R.id.operatingArea);
        TrouteName = (TextView)findViewById(R.id.routeName);
        ToperatingTime = (TextView)findViewById(R.id.operatingTime);
        TintervalTime = (TextView)findViewById(R.id.intervalTime);
        Intent intent = getIntent();
        String Info = intent.getStringExtra("detail").replace(" ", "");
        Info = Info.replace("\t", "");
        String[] detailInfo = Info.split(",");
        for(int i = 0 ; i <detailInfo.length; i++){
            String[] data = detailInfo[i].split("=");
            detailInfo[i] = data[1];
        }

        TrouteName.setText(detailInfo[1]);
        ToperationArea.setText("운행지역\n" + detailInfo[3] + "-" +detailInfo[4] +
                                "\n" + Dist.getDistName(detailInfo[11]) + detailInfo[2]);
        ToperatingTime.setText("운행시간\n"+
                                "기점 -"+ detailInfo[5] + "~" +detailInfo[6]+
                                "\n회차 -"+ detailInfo[7] + "~" +detailInfo[8]);
        TintervalTime.setText("배차간격\n"+detailInfo[9] + "~"+detailInfo[10]+"분");
    }
}
