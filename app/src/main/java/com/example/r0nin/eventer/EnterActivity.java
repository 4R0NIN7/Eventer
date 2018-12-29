package com.example.r0nin.eventer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class EnterActivity extends AppCompatActivity {

    private Button btnMapa, btnWydarzenie, btnProfil, btnWiadomosc;
    String login="";
    String message="";

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

                Bundle loginBundle = new Bundle();
                loginBundle.putString("message",message );
                loginBundle.putString("login",login );
                intent.putExtras(loginBundle);

                startActivity(intent);
            }
        });
        btnWydarzenie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,WydarzenieActivity.class);

                Bundle loginBundle = new Bundle();
                loginBundle.putString("message",message );
                loginBundle.putString("login",login );
                intent.putExtras(loginBundle);

                startActivity(intent);
            }
        });
        btnProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,ProfilActivity.class);

                Bundle loginBundle = new Bundle();
                loginBundle.putString("message",message );
                loginBundle.putString("login",login );
                intent.putExtras(loginBundle);

                startActivity(intent);
            }
        });
        btnWiadomosc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EnterActivity.this,KonwersacjeActivity.class);

                Bundle loginBundle = new Bundle();
                loginBundle.putString("message",message );
                loginBundle.putString("login",login );
                intent.putExtras(loginBundle);

                startActivity(intent);
            }
        });



        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            login = loginBundle.getString("login");
            message = loginBundle.getString("message");
            Toast.makeText(this, message+login, Toast.LENGTH_SHORT).show();
        }
    }
}
