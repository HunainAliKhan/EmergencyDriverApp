package com.example.hunain.emergencydriverapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hunain.emergencydriverapp.Business_Object.ITokenBAO;
import com.example.hunain.emergencydriverapp.Business_Object.TokenBAO;
import com.example.hunain.emergencydriverapp.Data_Access.TokenDAO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    EditText email,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.emailInput);
        password = (EditText) findViewById(R.id.passwordInput);

        auth = FirebaseAuth.getInstance();


        //boolean b = user.isEmailVerified();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user != null) {
                    if (user.isEmailVerified()) {
                        Intent i = new Intent(LoginActivity.this, DriverMapsActivity.class);
                        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
                else
                {
                    Log.i("Message","Authentication Changed Logout");
                }
            }
        };
    }


    public void login(View view){
        if(TextUtils.isEmpty(email.getText().toString())){
            Toast.makeText(getApplicationContext(),"Enter incorrect Email",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Login is not successful", Toast.LENGTH_SHORT).show();
                }else{
                    user = task.getResult().getUser();
                    checkIsEmailVarified();

                }
            }
        });
    }

    public void checkIsEmailVarified(){

        if(!user.isEmailVerified()){
            Toast.makeText(getApplicationContext(),"Please Varify your email address",Toast.LENGTH_SHORT).show();

        }else {

            String recent_token = FirebaseInstanceId.getInstance().getToken();
            ITokenBAO token = new TokenBAO(new TokenDAO(getApplicationContext()),getApplicationContext());
            token.UpdateToken(recent_token);
            //i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
            Intent i = new Intent(LoginActivity.this,DriverMapsActivity.class);
            startActivity(i);
        }
    }


}
