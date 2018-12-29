package com.example.r0nin.eventer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class WydarzenieActivity extends AppCompatActivity {
    protected ListView listViewTwojeWydarzenia, listViewWydarzeniaWPoblizu;
    protected ArrayAdapter<String> adapterTwojeWydarzenia, adapterWydarzeniaWPoblizu;
    protected ArrayList<String> listItemsTwojeWydarzenia, listItemsWydarzeniaWPoblizu;
    protected Button btnUtworz;
    protected EditText editTextNazwa, editTextDataPoczatku, editTextDataKonca;
    private String login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wydarzenie);
        editTextNazwa = (EditText) findViewById(R.id.editTextNazwa);
        editTextDataKonca = (EditText) findViewById(R.id.editTextDataKonca);
        editTextDataPoczatku = (EditText) findViewById(R.id.editTextDataPoczatku);
        btnUtworz = (Button) findViewById(R.id.btnUtworz);

        btnUtworz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            login = loginBundle.getString("login");


        }

        listViewTwojeWydarzenia = (ListView) findViewById(R.id.listViewTwojeWydarzenia);
        listItemsTwojeWydarzenia = new ArrayList<String>();
        adapterTwojeWydarzenia = new ArrayAdapter<String>(this,R.layout.list_item, listItemsTwojeWydarzenia);
        listViewTwojeWydarzenia.setAdapter(adapterTwojeWydarzenia);

        listViewWydarzeniaWPoblizu = (ListView) findViewById(R.id.listViewWydarzeniaWPoblizu);
        listItemsWydarzeniaWPoblizu = new ArrayList<String>();
        adapterWydarzeniaWPoblizu = new ArrayAdapter<String>(this,R.layout.list_item, listItemsWydarzeniaWPoblizu);
        listViewWydarzeniaWPoblizu.setAdapter(adapterWydarzeniaWPoblizu);

        //Jak kliknie to np: niech sie zapisze do tego wydarzenia lub z niego sie wypisze

        listViewTwojeWydarzenia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        listViewWydarzeniaWPoblizu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });



    }
}
