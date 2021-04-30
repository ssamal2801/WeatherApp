package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText enterCityView;
    TextView descriptionView;
    TextView selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterCityView = findViewById(R.id.enterCityView);
        descriptionView = findViewById(R.id.descriptionView);
        selectedView = findViewById(R.id.selectedView);
    }


    public class downloadWether extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... addresses)
        {
            String result ="";

            try {
                URL url = new URL(addresses[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                char ch;
                while (data!=-1)
                {
                    ch = (char) data;
                    result += ch;
                    data = reader.read();
                }

            } catch (Exception e) {
                Log.i("PROBLEM FOUND :","PROBLEM WHILE GETTING URL");
                return null;
            }

            return result;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject jsonObject = new JSONObject(s);
                String weatherInfo = jsonObject.getString("weather");
                JSONObject main = new JSONObject(jsonObject.getString("main"));

                double feelsLike = main.getDouble("feels_like");

                Log.i("ALL JASON DATA :",weatherInfo);
                Log.i("FEELS LIKE :",String.valueOf(feelsLike)+" Degree");

                JSONArray arr = new JSONArray(weatherInfo);
                String updateUser="";
                String mn;
                String description;

                for (int i=0; i<arr.length();i++)
                {
                    JSONObject obj = arr.getJSONObject(i);
                    mn=obj.getString("main");
                    description = obj.getString("description");

                    if (!mn.equals("") && !description.equals(""))
                    {
                        updateUser = "Atmosphere: "+description + "\r\n";
                    }
                    else Toast.makeText(getApplicationContext(),"Urgh! City not found :(",Toast.LENGTH_LONG).show();
                }

                if (!updateUser.equals(""))
                {
                    descriptionView.setAlpha(1);
                    descriptionView.setText(updateUser);

                    String loc =enterCityView.getText().toString();

                    selectedView.setAlpha(1);
                    selectedView.setText(loc+" Feels "+feelsLike + "℃");
                }
                else Toast.makeText(getApplicationContext(),"Urgh! City not found :(",Toast.LENGTH_LONG).show();



            }catch (Exception e)
            {
                Log.i("PROBLEM FOUND","PROBLEM IN GETTING JSON");
                Toast.makeText(getApplicationContext(),"Urgh! City not found :(",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void getWether(View view)
    {
       try
        {
        downloadWether dw = new downloadWether();
        String location=enterCityView.getText().toString();

        dw.execute("https://openweathermap.org/data/2.5/weather?q="+ location +"&appid=439d4b804bc8187953eb36d2a8c26a02").get();

        Log.i("Location is ",location);

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(enterCityView.getWindowToken(),0);

        }catch (Exception e)
            {
             Log.i("ERROR ","ERROR IN ONCLICK METHOD");
                Toast.makeText(getApplicationContext(),"Urgh! City not found :(",Toast.LENGTH_LONG).show();
            }

    }
}