package com.example.r0nin.eventer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfilActivity extends AppCompatActivity {

    TextView textViewLogin;
    TextView textViewPunkty;
    TextView textViewImie;
    TextView textViewNazwisko;
    TextView textViewDataUrodzenia;
    TextView textViewTelefon;
    Button btnDodaj;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        textViewLogin = findViewById(R.id.textViewLogin);
        textViewPunkty = findViewById(R.id.textViewPunkty);
        textViewImie = findViewById(R.id.textViewImie);
        textViewNazwisko = findViewById(R.id.textViewNazwisko);
        textViewDataUrodzenia = findViewById(R.id.textViewDataUrodzenia);
        textViewTelefon = findViewById(R.id.textViewTelefon);
        btnDodaj = findViewById(R.id.btnDodaj);


        btnDodaj.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {
                id = 1;
                List<Object> params = new ArrayList<Object>(){ };
                params.add(id);
                params.add(textViewLogin);
                params.add(textViewPunkty);
                params.add(textViewImie);
                params.add(textViewNazwisko);
                params.add(textViewDataUrodzenia);
                params.add(textViewTelefon);
                new FetchUserDataTask().execute(params);
                /*AsyncTask task = new AsyncTask(){

                    @Override
                    protected Object doInBackground(Object[] objects) {
                        OkHttpClient client = new OkHttpClient();
                        Request req = new Request.Builder().url("http://192.168.198.1:51000/api/Uzytkownik/2").build();
                        Response res = null;
                        try{
                            res = client.newCall(req).execute();
                            Log.d("godzilla", "test1");
                            return res.body().string();
                        }catch(IOException exception){
                            exception.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Object o){
                        try {
                            String JSONstring = o.toString();
                            Log.d("godzilla", "jsonstring: "+JSONstring);
                            JSONArray json = new JSONArray(o.toString());
                            Log.d("godzilla", "jsonobject: "+json.toString());
                            JSONObject userJson = json.getJSONObject(0);


                            String login = userJson.getString("login");
                            Log.d("godzilla", "login: "+login);
                            textViewLogin.setText(userJson.getString("login"));
                            textViewPunkty.setText(userJson.getString("punkty"));
                            textViewImie.setText(userJson.getString("imie"));
                            textViewNazwisko.setText(userJson.getString("nazwisko"));
                            textViewDataUrodzenia.setText(userJson.getString("data_urodzenia"));
                            textViewTelefon.setText(userJson.getString("nr_telefonu"));
                        } catch (JSONException e) {
                            Log.d("godzilla", "error");
                        }


                    }
                }.execute();*/
            }
        });
    }

    private class FetchUserDataTask extends AsyncTask<List<Object>, Void, Object>{

        @Override
        protected Object doInBackground(List<Object>... params) {

            OkHttpClient client = new OkHttpClient();
            Request req = new Request.Builder().url("http://192.168.198.1:51000/api/Uzytkownik/2").build();
            Response res = null;
            try{
                res = client.newCall(req).execute();
                return res.body().string();
            }catch(IOException exception){
                exception.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try {
                if(o != null){
                JSONArray json = new JSONArray(o.toString());
                JSONObject userJson = json.getJSONObject(0);
                textViewLogin.setText("Login: " + userJson.getString("login"));
                textViewPunkty.setText("Punkty: "+userJson.getString("punkty"));
                textViewImie.setText("Imię: "+userJson.getString("imie"));
                textViewNazwisko.setText("Nazwisko: "+userJson.getString("nazwisko"));
                textViewDataUrodzenia.setText("Data urodzenia: "+userJson.getString("data_urodzenia"));
                textViewTelefon.setText("Telefon: "+userJson.getString("nr_telefonu"));
                }
            } catch (JSONException e) {

            }
        }
    }

}
