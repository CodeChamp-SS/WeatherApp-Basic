package com.saarthak.weatherapp;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText city_et;
    TextView result;
    String city;

    public void findWeather(View V) throws UnsupportedEncodingException {

        city = city_et.getText().toString();

        // to hide the keyboard automatically after button is pressed
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city_et.getWindowToken(),0);

        String encodedCity = URLEncoder.encode(city,"UTF-8");

//        Log.i("",city);
//        Log.i("URL","http://api.openweathermap.org/data/2.5/weather?q=" + city + "your api key here");

        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "your api key here");

    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            String res = "";
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    char curr = (char) data;
                    res += curr;
                    data = reader.read();
                }

                return res;

            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);

            try {
                JSONObject jsonObject = new JSONObject(res);

                String weatherInfo = jsonObject.getString("weather");
                String tempInfo = jsonObject.getString("main");

                String weather = "";

                JSONArray arr = new JSONArray(weatherInfo);
                for(int j=0;j<arr.length();j++){
                    JSONObject jsonPart = arr.getJSONObject(j);
                    weather += jsonPart.getString("main") + ", ";
                    weather += jsonPart.getString("description");
                }
                result.setText(weather + "\n" + tempInfo);

            }
            catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find weather", Toast.LENGTH_SHORT).show();
            }

        }
    }

//    http://api.openweathermap.org/data/2.5/weather?q=city,in&appid=b211804c9325ebca6a8a93b7096545b5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city_et = findViewById(R.id.editText);
        result = findViewById(R.id.textView2);

    }
}
