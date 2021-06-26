package com.example.otp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore store;
    EditText phonenumber,code;
    Button next;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker ccp;
    String verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    boolean flag = false;
    String userOtp;


    @Override
    protected void onStart() {
        super.onStart();
        if(fAuth.getCurrentUser()!=null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking...");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void checkUserProfile() {
        DocumentReference docRef = store.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>(){

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                }else{

                    startActivity(new Intent(getApplicationContext(),AddDetails.class));
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        phonenumber = findViewById(R.id.phone);
        code = findViewById(R.id.codeEnter);
        progressBar = findViewById(R.id.progressBar);
        next = findViewById(R.id.nextBtn);
        state = findViewById(R.id.state);
        ccp = findViewById(R.id.ccp);

        store = FirebaseFirestore.getInstance();

        next.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String phone = phonenumber.getText().toString();
                if (!flag) {
                    if (!phone.isEmpty() && phone.length() == 10) {
                        String phoneNum = "+" + ccp.getSelectedCountryCode() + phone;
                        Toast.makeText(getApplicationContext(), "Phone number is " + phoneNum, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP");
                        state.setVisibility(View.VISIBLE);
                        requestOtp(phoneNum);
                    } else {
                        phonenumber.setError("Enter Valid Number");
                    }
                }
                else{

                    userOtp = code.getText().toString();
                    if(!userOtp.isEmpty()&&userOtp.length()==6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOtp);
                        verifyAuth(credential);
                    }
                    else{
                        code.setError("Valid OTP required");
                    }

                }
            }
        });


    }

    private void verifyAuth(PhoneAuthCredential credential) {

        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<AuthResult> task) {
                                                                             if(task.isSuccessful()){
                                                                                 Toast.makeText(getApplicationContext(),"Authentication is Successful",Toast.LENGTH_SHORT).show();
                                                                                 checkUserProfile();
                                                                             }
                                                                             else{
                                                                                 Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                                                                             }
                                                                         }

                                                                     }
        );
    }

    private void requestOtp(String phoneNum) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                code.setVisibility(View.VISIBLE);
                verificationId=s;
                token = forceResendingToken;
                next.setText("Verify");
                //next.setEnabled(false);
                flag = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(getApplicationContext(),"OTP Expired ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Cannot verify "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}