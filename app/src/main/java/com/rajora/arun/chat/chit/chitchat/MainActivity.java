package com.rajora.arun.chat.chit.chitchat;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthCredential;
import com.rajora.arun.chat.chit.authenticator.login.Login;
import com.rajora.arun.chat.chit.authenticator.login.User_Metadata;

public class MainActivity extends AppCompatActivity {
    final static int REQUEST_CODE_LOGIN=100;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(this,Login.class);
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    Toast.makeText(MainActivity.this,"logged in as- "+user.getUid(),Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MainActivity.this,"logged out",Toast.LENGTH_SHORT).show();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        startActivityForResult(intent,REQUEST_CODE_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE_LOGIN && resultCode== Activity.RESULT_OK){
            Toast.makeText(this,data.getStringExtra(User_Metadata.PHONE_NUMBER),Toast.LENGTH_SHORT).show();
            String ph_no=data.getStringExtra(User_Metadata.PHONE_NUMBER);
            String token=data.getStringExtra(User_Metadata.O_AUTH_TOKEN);
            String secret=data.getStringExtra(User_Metadata.O_AUTH_SECRET);
            String firebase_token=data.getStringExtra(User_Metadata.FIREBASE_TOKEN);
            Log.d("findme","phone "+ph_no);
            Log.d("findme","token "+token);
            Log.d("findme","secret "+secret);
            mAuth.signInWithCustomToken(firebase_token)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(MainActivity.this,"ready",Toast.LENGTH_SHORT).show();
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Auth failed on firebase",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this,"YAHOOOOOOO!!!!!!!!!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
