package com.example.jointest;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText id, pw, name, phone;
    private Button bt;
    private TextView tv_outPut;
    //CustomTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //task = new CustomTask();
        id = (EditText)findViewById(R.id.ID);
        pw = (EditText)findViewById(R.id.PW);
        name = (EditText)findViewById(R.id.NAME);
        phone = (EditText)findViewById(R.id.PHONE);
        bt = (Button)findViewById(R.id.SEND);
        tv_outPut = (TextView)findViewById(R.id.tv_outPut);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues val = new ContentValues();
                val.put("id", id.getText().toString());
                val.put("pw", pw.getText().toString());
                val.put("name", name.getText().toString());
                val.put("phone", phone.getText().toString());
                NetworkTask nt = new NetworkTask("http://192.168.81.80:8765/BUSPICKex1/Login/Login.jsp", val);
                nt.execute();
            }
        });
        /*bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EThread thread = new EThread(id.getText().toString(),
                        pw.getText().toString(),
                        name.getText().toString(),
                        phone.getText().toString());
                thread.start();
                try{
                    thread.join();
                }catch (InterruptedException e){

                }
            }
        });*/
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

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            tv_outPut.setText(s);
        }
    }
}


/*
class EThread extends Thread{
    private static final String TAG = "EThread";
    private String inputid;
    private String inputpw;
    private String inputname;
    private String inputphone;

    public EThread(String id, String pw, String name, String phone){
        this.inputid = id;
        this.inputpw = pw;
        this.inputname = name;
        this.inputphone = phone;
    }
    public void run(){
        try {
            CustomTask c = new CustomTask();
            String result = c.execute(inputid,inputpw,inputname,inputphone).get();
            Log.i("리턴 값",result);
        } catch(Exception e){

        }
    }
}*/
/*
class CustomTask extends AsyncTask<String, Void, String>{
    String sendMsg, receiveMsg;
    @Override
    protected String doInBackground(String... strings) {
        try{
            String str;
            URL url = new URL("http://192.168.0.49:8765/BUSPICKex1/Login/Login.jsp");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www=form-urlencoded");
            conn.setRequestMethod("POST");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
            sendMsg = "id="+strings[0]+"&pw="+strings[1]+"&name="+strings[2]+"&phone="+strings[3];
            Log.i("보낸 메시지",sendMsg);
            osw.write(sendMsg);
            osw.flush();

            if(conn.getResponseCode() == conn.HTTP_OK){
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
            }else {
                Log.i("통신 결과", conn.getResponseCode()+"에러");
                // 통신이 실패했을 때 실패한 이유를 알기 위해 로그를 찍습니다.
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return receiveMsg;
    }
}*/