package com.example.r0nin.eventer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KonwersacjeActivity extends AppCompatActivity {

    protected Button btnWyslij;
    protected EditText editTextTresc, editTextUzytkownik;
    protected RadioGroup radioGroupTyp , radioGroupOpinia;
    protected RadioButton radioButtonOpinia, radioButtonWiadomosc, radioButtonSkarga;
    /*
    ListView - widok listy
    listItems - wrzucasz wiadomosci
    Layout jednego elementu to list_item.xml w folderze layout
     */
    protected ListView listViewWiadomosci;
    protected ArrayAdapter<String> adapter;
    protected ArrayList<String> listItems;

    private String login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konwersacje);

        btnWyslij = (Button) findViewById(R.id.btnWyslij); //przycisk do wyslania
        editTextTresc = (EditText) findViewById(R.id.editTextTresc); //tresc wiadomosci
        editTextUzytkownik = (EditText) findViewById(R.id.editTextUzytkownik); //uzytkownik
        radioGroupTyp = (RadioGroup) findViewById(R.id.radioGroupTyp);
        radioGroupOpinia = (RadioGroup) findViewById(R.id.radioGroupOpinia);
        radioButtonSkarga = (RadioButton) findViewById(R.id.radioButtonSkarga);
        radioButtonWiadomosc = (RadioButton) findViewById(R.id.radioButtonWiadomosc);



        radioButtonWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupTyp.setEnabled(false);

            }
        });
        radioButtonSkarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupTyp.setEnabled(false);

            }
        });

        //dodac wybierz uzytkownika z listy
        btnWyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostMessage().execute("");
            }
        });

        listViewWiadomosci = (ListView) findViewById(R.id.listviewWiadomosci);
        listItems = new ArrayList<String>();





        adapter = new ArrayAdapter<String>(this,R.layout.list_item, listItems);

        listViewWiadomosci.setAdapter(adapter);
        /*
        Metoda, podczas wyboru wiadomosci
         */
        listViewWiadomosci.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //editTextTresc.setText(listItems.get(i)); //wrzucam to co kliknalem do editTextTresc
                launchMessagesActivity(listItems.get(i));


            }
        });

        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            login = loginBundle.getString("login");


        }

        //new GetMyMessages().execute(login);


        ActivityTimer timer = new ActivityTimer(10000, 1000);
        timer.callTimer();

    }

    private void launchMessagesActivity(String recipient) {
        Intent intent = new Intent(KonwersacjeActivity.this,WiadomosciActivity.class);

        Bundle loginBundle = new Bundle();
        loginBundle.putString("recipientLogin",recipient );
        loginBundle.putString("myLogin",login );
        intent.putExtras(loginBundle);

        startActivity(intent);


    }


    private class GetMyMessages extends AsyncTask<String, Void, Object>{

        @Override
        protected Object doInBackground(String... strings) {


            OkHttpClient client = new OkHttpClient();
            Request req;
            String login = strings[0];
            req = new Request.Builder().url(getResources().getString(R.string.apiUrl)+"/api/Wiadomosc/my-conversations/"+login).build();
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
                    Log.d("godzilla", "getting response");
                    JSONArray json = new JSONArray(o.toString());
                    listItems.clear();
                    for(int i = 0; i<json.length(); i++){
//                        JSONObject userJson = json.getJSONObject(i);
//                        String tresc = userJson.getString("tresc");
//                        String login_nadawcy = userJson.getString("login_nadawcy");
//                        String data_wyslania = userJson.getString("data_wyslania");
//                        Log.d("godzilla", "response: "+data_wyslania + " : " + login_nadawcy + " : " + tresc);
                        String login_nadawcy = json.get(i).toString();

                        listItems.add(login_nadawcy);
                    }

                    adapter.notifyDataSetChanged();
                    listViewWiadomosci.invalidateViews();
                    listViewWiadomosci.refreshDrawableState();


                }
            } catch (JSONException e) {

            }
        }
    }

    private class PostMessage extends AsyncTask<String, Void, Object> {



        @Override
        protected Object doInBackground(String... params) {

            String url = getResources().getString(R.string.apiUrl)+"/api/Wiadomosc/add";


            // Names and values will be url encoded
            final FormBody.Builder formBuilder = new FormBody.Builder();
            formBuilder.add("tresc", editTextTresc.getText().toString());
            formBuilder.add("login_nadawcy",login );
            formBuilder.add("login_odbiorcy", editTextUzytkownik.getText().toString());
            formBuilder.add("data_wyslania", "1.01.2000");

            RequestBody requestBody = formBuilder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                int responseCode = response.code();
                Log.d("godzilla", "responseCode: " + responseCode);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Object();
        }

        @Override
        protected void onPostExecute(Object o) {

            if(o != null){
                Log.d("godzilla", "ok");
                editTextTresc.setText("");
                editTextUzytkownik.setText("");
            }


        }



    }



    public class ActivityTimer {
        protected long period; //mili sekundy
        protected int delay;
        protected final Timer myTimer = new Timer();

        public ActivityTimer(long period, int delay){
            this.period = period;
            this.delay = delay;
        }
        private void callTimer()
        {

            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new GetMyMessages().execute(login);

                    Log.d("ActivityTimer","wiadomoscActivity - Timer poszedł");
                }
            }, delay,period);

        }
    }
}
