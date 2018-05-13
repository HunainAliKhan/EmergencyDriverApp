package com.example.hunain.emergencydriverapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    ViewPager viewPager;
    LinearLayout sliderDotPanel;
    private int dotsCount;
    private ImageView[] dots;
    Button next;
    LinearLayout registerForm;
    EditText firstName,lastName,email,licenseNumber,password,city,phoneNumber;
    Spinner selectDepartment;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;


    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    Driver driver;
    HashMap<Integer,String> department;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        intilizeEditText();
        auth = FirebaseAuth.getInstance();
        // next = (Button)findViewById(R.id.n);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        driver = new Driver();
        next = (Button) findViewById(R.id.Next);
        sliderDotPanel = (LinearLayout) findViewById(R.id.slideLayout);
        registerForm = (LinearLayout) findViewById(R.id.registerForm);







        authentications();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(registerForm.getVisibility() == view.GONE){
                    registerForm.setVisibility(view.VISIBLE);
                }else{
                    userRegisteration();

            }
        }});







        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(),2000,4000);

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];


        for(int i = 0; i < dotsCount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);
            sliderDotPanel.addView(dots[i],params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i < dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


    public void authentications() {

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i("Phone Number Failed","Wrong Phone number formate");


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                //super.onCodeSent(s, forceResendingToken);

            }
        };
    }


    public void intilizeEditText(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);
        phoneNumber = findViewById(R.id.PhoneNumber);
        licenseNumber = findViewById(R.id.licenceNumber);
        email = findViewById(R.id.email);
        selectDepartment = findViewById(R.id.department_spinner);

    }

    public  String generateDeviceToken(){
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        return FirebaseInstanceId.getInstance().getToken();
    }


    public void insertDriverToDatabase()
    {

        driver.name = firstName.getText().toString() + " " + lastName.getText().toString();
        driver.password = password.getText().toString();
        driver.phoneNumber = phoneNumber.getText().toString();
        driver.email = email.getText().toString();
        driver.city = city.getText().toString();
        driver.licenseNumber = licenseNumber.getText().toString();
        driver.deviceToken = generateDeviceToken();
        driver.branchId = selectDepartment.getSelectedItemPosition();
        driver.isAvailable = true;
        driver.selectDepartment = selectDepartment.getSelectedItemPosition();

        Call<String> register =  new RestService().getService().driverRegistration(driver);
        Log.i("sdsadasds",driver.city);
        register.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Intent login = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(login);

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

/*

    public void saveEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"hkworior@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
                  Toast.makeText(SignupActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    public void sendEmailVarification(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Check your email for varification", Toast.LENGTH_SHORT);
                        FirebaseAuth.getInstance().signOut();
                    }
                }
            });
        }
    }
*/

    public void userRegisteration(){
        driver.name = firstName.getText().toString() +  " " + lastName.getText().toString();
        driver.email = email.getText().toString();
        driver.city = city.getText().toString();
        driver.phoneNumber = phoneNumber.getText().toString();
        driver.password = password.getText().toString();
        driver.licenseNumber = password.getText().toString();

        if(TextUtils.isEmpty(driver.name)){
                Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
                return;
        }else if(TextUtils.isEmpty(driver.email)){
            Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(driver.city)){
            Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(driver.phoneNumber)){
            Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(driver.password)){
            Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
            return;
        }else
        if(TextUtils.isEmpty(driver.licenseNumber)){
            Toast.makeText(this,"Please specified name",Toast.LENGTH_SHORT).show();
            return;
        }else if(selectDepartment.getSelectedItemPosition() == 0){
            Toast.makeText(this,"Please Select the department",Toast.LENGTH_SHORT).show();
            return;
        }



        if(driver.password.length() < 6 ){
            Toast.makeText(getApplicationContext(),"Password length short",Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(driver.email,driver.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                       sendEmailVarification();
                       insertDriverToDatabase();

                }else {
                    Toast.makeText(getApplicationContext(),"Problem in registeration",Toast.LENGTH_SHORT).show();





                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void sendEmailVarification(){
      user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"Please Check your email for varification",Toast.LENGTH_SHORT).show();
                        auth.signOut();


                    }
                }
            });
        }
    }



    public class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            SignupActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);

                    }else{
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

}
