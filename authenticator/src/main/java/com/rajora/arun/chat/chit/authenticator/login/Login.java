package com.rajora.arun.chat.chit.authenticator.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.digits.sdk.android.Digits;
import com.rajora.arun.chat.chit.authenticator.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.digits.sdk.android.*;
import io.fabric.sdk.android.Fabric;

public class Login extends AppCompatActivity {
    private static final String TWITTER_KEY = "YOR TWITTER KEY";
    private static final String TWITTER_SECRET = "YOUR TWITTER SECRET";
    private final AuthCallback callback=new AuthCallback() {
        @Override
        public void success(DigitsSession session, String phoneNumber) {
            Intent intent_result=new Intent();
            intent_result.putExtra(User_Metadata.O_AUTH_TOKEN,session.getAuthToken().token);
            intent_result.putExtra(User_Metadata.O_AUTH_SECRET,session.getAuthToken().secret);
            intent_result.putExtra(User_Metadata.PHONE_NUMBER,phoneNumber);
            setResult(Activity.RESULT_OK,intent_result);
            finish();
        }
        @Override
        public void failure(DigitsException exception) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        askPermission();
        setContentView(R.layout.activity_login);
        authenticate();

    }
    public void askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},200);
        }
    }
    public void authenticate(){
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());
        Digits.clearActiveSession();

        AuthConfig.Builder mAuthConfig=new AuthConfig.Builder()
                .withAuthCallBack(callback);
        Digits.authenticate(mAuthConfig.build());
    }
}
