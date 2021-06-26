package com.example.otp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyHolder> {


    private List<Contact> contactList;
    private Context mCtx;
    ItemClickListener itemClickListener;
    AlertDialog.Builder builder;
    EditText up_name, up_pno, up_msg;
    Button btn_update, btn_cancel;


    AlertDialog dialog;

    public ContactAdapter(Context mCtx, List<Contact> contactList) {
        this.mCtx = mCtx;
        this.contactList = contactList;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item, parent, false);
        return new MyHolder(view);
    }



    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        Contact contactInfo = contactList.get(position);
        holder.contact_name.setText(contactInfo.getName());
        holder.contact_pno.setText(contactInfo.getPhonenumber());
        holder.cmsg.setText(contactInfo.getMessage());




    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }



    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView contact_name, contact_pno, cmsg;
        ImageView contact_delete;


        public MyHolder(View itemView) {
            super(itemView);

            contact_name = itemView.findViewById(R.id.name);
            contact_pno = itemView.findViewById(R.id.phoneNo);
            cmsg = itemView.findViewById(R.id.message);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Contact contact = contactList.get(getAdapterPosition());
            Intent intent = new Intent(mCtx, UpdateContactActivity.class);
            intent.putExtra("Contact", contact);

            mCtx.startActivity(intent);
        }


    }




}