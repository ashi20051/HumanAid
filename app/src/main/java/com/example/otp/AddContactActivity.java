package com.example.otp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class AddContactActivity extends AppCompatActivity {

    private EditText editTextName, editTextNumber, editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        editTextName = findViewById(R.id.editTextName);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextMessage = findViewById(R.id.editTextMessage);

        findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        final String sName = editTextName.getText().toString().trim();
        final String sPhoneNumber = editTextNumber.getText().toString().trim();
        final String sMessage = editTextMessage.getText().toString().trim();

        if (sName.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }

        if (sPhoneNumber.isEmpty() ) {
            editTextNumber.setError("phone number required");
            editTextNumber.requestFocus();
            return;
        }

        if (sMessage.isEmpty()) {
            editTextMessage.setError("message required");
            editTextMessage.requestFocus();
            return;
        }

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                Contact contact = new Contact();
                contact.setName(sName);
                contact.setPhonenumber(sPhoneNumber);
                contact.setMessage(sMessage);


                //adding to database
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .insert(contact);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();


                FragmentManager fragmentManager = getSupportFragmentManager();
                ContactFragment contactFragment = new ContactFragment();
                fragmentManager.beginTransaction().replace(R.id.container, contactFragment).commit();
                editTextName.setText("");
                editTextNumber.setText("");
                editTextMessage.setText("");
                finish();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

}
