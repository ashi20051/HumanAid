package com.example.otp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDetails extends AppCompatActivity {

    EditText firstName,lastName,emailId;
    Button save;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        emailId = findViewById(R.id.emailId);

        save = findViewById(R.id.save);

        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        UID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference docRef = fStore.collection("users").document(UID);

        save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(!firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()&&!emailId.getText().toString().isEmpty()){
                    String first = firstName.getText().toString();
                    String last = lastName.getText().toString();
                    String email = emailId.getText().toString();

                    Map<String,Object> user = new HashMap<>();
                    user.put("First",first);
                    user.put("Last",last);
                    user.put("Email",email);

                    docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>(){

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Data not saved!!!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else{
                    Toast.makeText(getApplicationContext(),"All fields are mandatory!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}