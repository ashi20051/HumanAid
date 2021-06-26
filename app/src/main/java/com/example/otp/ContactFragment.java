package com.example.otp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;


public class ContactFragment extends Fragment {

    ImageView addContact;


    public RecyclerView mContactrecyclerView;
    List<Contact> list = new ArrayList<>();


    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (list != null) {
            SensorService.tempList = list;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getContacts();
        SensorService.tempList = list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact, container, false);



        mContactrecyclerView = v.findViewById(R.id.listRecyclerView);
        mContactrecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        addContact = v.findViewById(R.id.addContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Add Contact Button clicked !!", LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AddContactActivity.class);
                startActivity(intent);
            }

        });

        getContacts();

        return v;

    }


    private void getContacts() {

        class GetTasks extends AsyncTask<Void, Void, List<Contact>> {

            @Override
            protected List<Contact> doInBackground(Void... voids) {
                List<Contact> contactList = DatabaseClient
                        .getInstance(getContext())
                        .getAppDatabase()
                        .contactDao()
                        .getAll();
                list = contactList;
                return contactList;

            }

            @Override
            protected void onPostExecute(List<Contact> contacts) {
                super.onPostExecute(contacts);
                ContactAdapter adapter = new ContactAdapter(getContext(), contacts);
                mContactrecyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }




}

