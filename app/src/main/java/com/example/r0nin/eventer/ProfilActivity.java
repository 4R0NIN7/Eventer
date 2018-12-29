package com.example.r0nin.eventer;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
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
    Button  /*btnDodaj,*/ btnEdytuj, btnZapisz;

    private String login="";

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
        //btnDodaj = findViewById(R.id.btnDodaj);
        btnEdytuj = findViewById(R.id.btnEdytuj);
        btnZapisz = findViewById(R.id.btnZapisz);

        //Ustawienie taga na KeyListenera
        textViewLogin.setTag(textViewLogin.getKeyListener());
        textViewPunkty.setTag(textViewLogin.getKeyListener());
        textViewImie.setTag(textViewLogin.getKeyListener());
        textViewNazwisko.setTag(textViewLogin.getKeyListener());
        textViewDataUrodzenia.setTag(textViewLogin.getKeyListener());
        textViewTelefon.setTag(textViewLogin.getKeyListener());
        //Ustawienie text boxow na nie edytowalne
        textViewLogin.setKeyListener(null);
        textViewPunkty.setKeyListener(null);
        textViewImie.setKeyListener(null);
        textViewNazwisko.setKeyListener(null);
        textViewDataUrodzenia.setKeyListener(null);
        textViewTelefon.setKeyListener(null);

//        btnDodaj.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//
//
//            }
//        });
        //edycja
        btnEdytuj.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //btnDodaj.setVisibility(View.GONE);
                btnZapisz.setVisibility(View.VISIBLE);
                textViewLogin.setKeyListener((KeyListener) textViewLogin.getTag());
                textViewPunkty.setKeyListener((KeyListener) textViewLogin.getTag());
                textViewImie.setKeyListener((KeyListener) textViewLogin.getTag());
                textViewNazwisko.setKeyListener((KeyListener) textViewLogin.getTag());
                textViewDataUrodzenia.setKeyListener((KeyListener) textViewLogin.getTag());
                textViewTelefon.setKeyListener((KeyListener) textViewLogin.getTag());
                /*
                Kod na edycje
                 */
            }
        });

        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            login = loginBundle.getString("login");


        }
        loadUserData(login);
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
                req = new Request.Builder().url(getResources().getString(R.string.apiUrl)+"/api/Uzytkownik/"+id).build();
            }catch(Exception e){
                String login = IdOrLogin.toString();
                req = new Request.Builder().url(getResources().getString(R.string.apiUrl)+"/api/Uzytkownik/login/"+login).build();
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

                String points = userJson.getString("punkty");
                String firstName = userJson.getString("imie");
                String lastName = userJson.getString("nazwisko");
                String date = userJson.getString("data_urodzenia");
                String phone = userJson.getString("nr_telefonu");

                textViewLogin.setText("Login: " + login);
                textViewPunkty.setText("Punkty: "+points);
                if(firstName != "null")
                    textViewImie.setText("Imię: "+firstName);
                if(lastName != "null")
                    textViewNazwisko.setText("Nazwisko: "+lastName);
                if(date != "null")
                    textViewDataUrodzenia.setText("Data urodzenia: "+date);
                if(phone != "null")
                    textViewTelefon.setText("Telefon: "+phone);
                if(firstName == "null" || lastName == "null" || date == "null" || phone == "null"){
                    Toast.makeText(ProfilActivity.this, ProfilActivity.this.getText(R.string.empty), Toast.LENGTH_LONG).show();
                }
                }


            } catch (JSONException e) {

            }
        }
    }

}
