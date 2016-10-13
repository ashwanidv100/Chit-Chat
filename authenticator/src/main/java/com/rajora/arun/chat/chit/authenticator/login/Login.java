package com.rajora.arun.chat.chit.authenticator.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digits.sdk.android.Digits;
import com.rajora.arun.chat.chit.authenticator.R;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.digits.sdk.android.*;

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class Login extends AppCompatActivity {
    private static final String TOKEN_URL="https://chit-chat-token-generator.azurewebsites.net/token";
    private static final String TWITTER_KEY = "TWITTER KEY";
    private static final String TWITTER_SECRET = "SECRET KEY";
    private final AuthCallback callback=new AuthCallback() {
        @Override
        public void success(final DigitsSession session, final String phoneNumber) {
            final Intent intent_result=new Intent();
            intent_result.putExtra(User_Metadata.O_AUTH_TOKEN,session.getAuthToken().token);
            intent_result.putExtra(User_Metadata.O_AUTH_SECRET,session.getAuthToken().secret);
            intent_result.putExtra(User_Metadata.PHONE_NUMBER,phoneNumber);
            RequestQueue queue = Volley.newRequestQueue(Login.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, TOKEN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            intent_result.putExtra(User_Metadata.FIREBASE_TOKEN,response);
                            setResult(Activity.RESULT_OK,intent_result);
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Login.this,"Please check internet connectivity.",Toast.LENGTH_SHORT);
                    setResult(Activity.RESULT_CANCELED);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("number",phoneNumber);
                    params.put("token",session.getAuthToken().token);
                    params.put("secret", session.getAuthToken().secret);
                    return params;
                }
            };
// Add the request to the RequestQueue.
            queue.add(stringRequest);
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
        setContentView(R.layout.activity_login);
        if(askPermission())
        {
            authenticate();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        authenticate();
    }

    public boolean askPermission(){
        Log.d("findme",String.valueOf(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)== PackageManager.PERMISSION_GRANTED));
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_PHONE_STATE},100);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},200);
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},300);
            return false;
        }
        return true;
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
