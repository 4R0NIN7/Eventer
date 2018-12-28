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

    EditText textViewLogin;
    EditText textViewPunkty;
    EditText textViewImie;
    EditText textViewNazwisko;
    EditText textViewDataUrodzenia;
    EditText textViewTelefon;
    Button btnDodaj, btnEdytuj, btnZapisz;
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
        btnEdytuj = findViewById(R.id.btnEdytuj);
        btnZapisz = findViewById(R.id.btnZapisz);

        btnDodaj.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View view) {


            }
        });

        String s = "test";
        loadUserData(s);

        //edycja
        btnEdytuj.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDodaj.setVisibility(View.GONE);
                btnZapisz.setVisibility(View.VISIBLE);

                /*
                Kod na edycje
                 */
            }
        });
    }

    private void loadUserData(Object id) {
        new FetchUserDataTask().execute(id);
    }

    private class FetchUserDataTask extends AsyncTask<Object, Void, Object>{



        @Override
        protected Object doInBackground(Object... params) {

            OkHttpClient client = new OkHttpClient();
            Object IdOrLogin = params[0];
            Request req;
            try{
                Integer id = (Integer)IdOrLogin;
                req = new Request.Builder().url("http://192.168.198.1:51000/api/Uzytkownik/"+id).build();
            }catch(Exception e){
                String login = IdOrLogin.toString();
                req = new Request.Builder().url("http://192.168.198.1:51000/api/Uzytkownik/login/"+login).build();
            }
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
