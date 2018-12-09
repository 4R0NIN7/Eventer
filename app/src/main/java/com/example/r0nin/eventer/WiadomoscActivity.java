package com.example.r0nin.eventer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WiadomoscActivity extends AppCompatActivity {

    protected Button btnWyslij;
    protected EditText editTextTresc, editTextUzytkownik;
    protected RadioGroup radioGroupTyp , radioGroupOpinia;
    protected RadioButton radioButtonOpinia, radioButtonWiadomosc, radioButtonSkarga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiadomosc);

        btnWyslij = (Button) findViewById(R.id.btnWyslij); //przycisk do wyslania
        editTextTresc = (EditText) findViewById(R.id.editTextTresc); //tresc wiadomosci
        editTextUzytkownik = (EditText) findViewById(R.id.editTextUzytkownik); //uzytkownik
        radioGroupTyp = (RadioGroup) findViewById(R.id.radioGroupTyp);
        radioGroupOpinia = (RadioGroup) findViewById(R.id.radioGroupOpinia);
        radioButtonOpinia = (RadioButton) findViewById(R.id.radioButtonOpinia);
        radioButtonSkarga = (RadioButton) findViewById(R.id.radioButtonSkarga);
        radioButtonWiadomosc = (RadioButton) findViewById(R.id.radioButtonWiadomosc);

        radioButtonOpinia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupTyp.setEnabled(false);
                radioGroupOpinia.setVisibility(View.VISIBLE);
                editTextTresc.setEnabled(false);
            }
        });

        radioButtonWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupTyp.setEnabled(false);
                radioButtonOpinia.setEnabled(false);
            }
        });
        radioButtonSkarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioGroupTyp.setEnabled(false);
                radioButtonOpinia.setEnabled(false);
            }
        });

        //dodac wybierz uzytkownika z listy
        btnWyslij.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    
}
