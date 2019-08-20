package com.example.lakshay.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
   EditText editText;
   TextView weather;
   TextView resultTextView;
   TextView tempTextView;


    public class DownloadTask extends AsyncTask<String ,Void,String> {

        @Override
        protected String doInBackground(String... strings) {


            String result ="";
            URL url;
            HttpURLConnection urlConnection =null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data !=-1)
                {
                    char current =(char) data;
                    result += current;
                    data =reader.read();
                }
                return  result;

            } catch ( Exception e) {
//                runOnUiThread(new Runnable(){
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "Check your Internet!!",Toast.LENGTH_SHORT).show();
//                    }
//                });
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String message="";
                JSONObject jObject = new JSONObject(result);
                String weatherInfo =jObject.getString("weather");
                String tempInfo = jObject.getString("main");
                JSONObject tempObject = new JSONObject(tempInfo);
                String temperature = tempObject.getString("temp");
                Double temperatureInKelvin = Double.parseDouble(temperature);
                Double finalTemperature = temperatureInKelvin - 273.15;

                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0;i<arr.length();i++)
                {
                    JSONObject jPart = arr.getJSONObject(i);
                    String main="" ;
                    String description="";
                    main = jPart.getString("main");
                    description=jPart.getString("description");
                    if(main!="" && description!=""){
                        message += main + ": " + description + "\r\n";
                    }

                }
         if(message!="")
         {
             resultTextView.setText(message);
             tempTextView.setText(String.format("%.2f", finalTemperature) + "\u2103");

         }
//         else{
//             runOnUiThread(new Runnable(){
//                 public void run() {
//                     Toast.makeText(getApplicationContext(), "Sorry! Could not fetch details",Toast.LENGTH_SHORT).show();
//                 }
//
//             });
//
//         }


            } catch (Exception e) {
                runOnUiThread(new Runnable(){
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Sorry! Could not fetch details",Toast.LENGTH_SHORT).show();
                    }
                });
                e.printStackTrace();
            }


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText =(EditText)findViewById(R.id.editText);
        tempTextView =(TextView)findViewById(R.id.tempTextView);
        resultTextView = (TextView)findViewById(R.id.resultTextView);

    }











    public void findWeather(View v)
    {
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
        String encodedCityName = URLEncoder.encode(editText.getText().toString());

        Log.i("cityName",editText.getText().toString());
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+ encodedCityName +"&APPID=1414b7f800c92608457c34f441368278");





    }




}
