package com.example.r0nin.eventer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class WiadomosciActivity extends AppCompatActivity {

    protected Button btnWyslij;
    protected EditText editTextTresc, editTextUzytkownik;
    protected RadioGroup radioGroupTyp , radioGroupOpinia;
    protected RadioButton radioButtonWiadomosc, radioButtonSkarga;

    protected ListView listViewWiadomosci;
    protected ArrayAdapter<String> adapter;
    protected ArrayList<String> listItems;

    String myLogin="";
    String recipientLogin="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiadomosci);


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



            }
        });


        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            myLogin = loginBundle.getString("myLogin");
            recipientLogin = loginBundle.getString("recipientLogin");

            Toast.makeText(this, "My login: "+myLogin+", Recipient: "+recipientLogin, Toast.LENGTH_SHORT).show();

        }
    }
}
