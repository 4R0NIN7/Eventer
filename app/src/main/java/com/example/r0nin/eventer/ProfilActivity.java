package com.example.r0nin.eventer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
                AsyncTask task = new AsyncTask(){

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
                            JSONObject json = new JSONObject(o.toString());
                            String login = json.getString("login");
                            Log.d("godzilla", "login: "+login);
                            textViewLogin.setText(json.getString("login"));
                            textViewPunkty.setText(json.getString("punkty"));
                            textViewImie.setText(json.getString("imie"));
                            textViewNazwisko.setText(json.getString("nazwisko"));
                            textViewDataUrodzenia.setText(json.getString("data_urodzenia"));
                            textViewTelefon.setText(json.getString("nr_telefonu"));
                        } catch (JSONException e) {
                            Log.d("godzilla", "error");
                        }


                    }
                }.execute();
            }
        });
    }

    private void getData(){

    }


}
