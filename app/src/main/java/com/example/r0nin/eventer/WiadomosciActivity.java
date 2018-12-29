package com.example.r0nin.eventer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class WiadomosciActivity extends AppCompatActivity {

    String myLogin="";
    String recipientLogin="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiadomosci);


        Bundle loginBundle = getIntent().getExtras();
        if(loginBundle != null){
            myLogin = loginBundle.getString("myLogin");
            recipientLogin = loginBundle.getString("recipientLogin");

            Toast.makeText(this, "My login: "+myLogin+", Recipient: "+recipientLogin, Toast.LENGTH_SHORT).show();

        }
    }
}
