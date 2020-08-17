package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    EditText city;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        city=findViewById(R.id.cityTextView);
        result=findViewById(R.id.weatherView);
    }

    public void getWeather(View view){

        DownloadTask task=new DownloadTask();
        try {
            String encodedCity= URLEncoder.encode(city.getText().toString() ,"UTF-8");
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=439d4b804bc8187953eb36d2a8c26a02");
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Could not find weather for provided city:(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(city.getWindowToken(), 0);
    }
    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            String result="";
            HttpsURLConnection connection=null;
            try {
                url=new URL(urls[0]);
                connection=(HttpsURLConnection)url.openConnection();
                InputStreamReader reader =new InputStreamReader(connection.getInputStream());
                int data=reader.read();
                while(data!=-1)
                {
                    char current=(char)data;
                    result+=current;
                    data=reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                   return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject=new JSONObject(s);
                String weatherInfo=jsonObject.getString("weather");

                JSONArray jsonArray=new JSONArray(weatherInfo);

                JSONObject part=null;
                JSONObject tempPart=jsonObject.getJSONObject("main");

                String temp=tempPart.getString("temp");
                String min_temp=tempPart.getString("temp_min");
                String max_temp=tempPart.getString("temp_max");
                String weather="";
                if(!temp.equals(""))
                    weather="Temperature: "+temp+"\n\n Min. temperature: "+min_temp+"\n Max. temperature: "+max_temp+"\n\n";

                for(int i=0;i<jsonArray.length();i++){
                    part= jsonArray.getJSONObject(i);
                    String main=part.getString("main");
                    String description=part.getString("description");
                    if(!main.equals("") && !description.equals(""))
                    weather+="Forecast: "+main+"\n Description: "+description+"\n\n";
                }


                if(!weather.equals("")){
                    result.setText(weather);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Could not find weather for provided city:(", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Could not find weather for "+city.getText().toString() +" :( ", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
               }

        }
    }

}