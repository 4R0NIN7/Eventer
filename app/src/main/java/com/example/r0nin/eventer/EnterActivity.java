package com.example.r0nin.eventer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EnterActivity extends AppCompatActivity {

    private Button btnMapa, btnWydarzenie, btnProfil, btnWiadomosc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        btnMapa = findViewById(R.id.btnMapa);
        btnWydarzenie = findViewById(R.id.btnWydarzenie);
        btnProfil = findViewById(R.id.btnProfil);
        btnWiadomosc = findViewById(R.id.btnWiadomosc);

        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        btnWydarzenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,WydarzenieActivity.class);
                startActivity(intent);
            }
        });
        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,ProfilActivity.class);
                startActivity(intent);
            }
        });
        btnWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,WiadomoscActivity.class);
                startActivity(intent);
            }
        });
    }
}
