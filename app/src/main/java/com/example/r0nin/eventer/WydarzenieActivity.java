package com.example.r0nin.eventer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WydarzenieActivity extends AppCompatActivity {

    protected Button btnUtworz;
    protected EditText editTextNazwa, editTextDataPoczatku, editTextDataKonca;
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
    }
}
