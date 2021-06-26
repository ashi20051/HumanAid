package com.example.otp;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class UpdateContactActivity extends AppCompatActivity {
    private EditText editTextName, editTextNumber, editTextMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);


        editTextName = findViewById(R.id.editTextName);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextMessage = findViewById(R.id.editTextMessage);




        final Contact contact = (Contact) getIntent().getSerializableExtra("Contact");

        loadTask(contact);

        findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
                updateTask(contact);
            }
        });

        findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTask(contact);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
        });
    }

    private void loadTask(Contact contact) {
        editTextName.setText(contact.getName());
        editTextNumber.setText(contact.getPhonenumber());
        editTextMessage.setText(contact.getMessage());
    }

    private void updateTask(final Contact contact) {
        final String sName= editTextName.getText().toString().trim();
        final String sNumber = editTextNumber.getText().toString().trim();
        final String sMessage = editTextMessage.getText().toString().trim();

        if (sName.isEmpty()) {
            editTextName.setError("Name required");
            editTextName.requestFocus();
            return;
        }

        if (sNumber.isEmpty()) {
            editTextNumber.setError("phone number required");
            editTextNumber.requestFocus();
            return;
        }

        if (sMessage.isEmpty()) {
            editTextMessage.setError("message required");
            editTextMessage.requestFocus();
            return;
        }

        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                contact.setName(sName);
                contact.setPhonenumber(sNumber);
                contact.setMessage(sMessage);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .update(contact);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putString("contactName", editTextName.getText().toString());
                bundle.putString("contactNumber", editTextNumber.getText().toString());
                bundle.putString("contactMessage", editTextMessage.getText().toString());
                ContactFragment frag = new ContactFragment();
                frag.setArguments(bundle);
                FragmentManager fragmentManager = getSupportFragmentManager();
                ContactFragment contactFragment = new ContactFragment();
                fragmentManager.beginTransaction().replace(R.id.container, contactFragment).commit();
                finish();

            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }


    private void deleteTask(final Contact contact) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .contactDao()
                        .delete(contact);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_LONG).show();
                FragmentManager fragmentManager = getSupportFragmentManager();
                ContactFragment contactFragment = new ContactFragment();
                fragmentManager.beginTransaction().replace(R.id.container, contactFragment).commit();
                finish();
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }

}
