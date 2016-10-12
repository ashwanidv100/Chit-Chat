package com.rajora.arun.chat.chit.chitchatdevelopers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,com.rajora.arun.chat.chit.authenticator.login.Login.class);
        startActivity(intent);
    }
}
