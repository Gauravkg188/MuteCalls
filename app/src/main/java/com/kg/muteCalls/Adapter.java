package com.kg.muteCalls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kg.muteCalls.room.Contact;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ContactHolder>{


    Context context;
    private List<Contact> contactList=new ArrayList<>();

    public Adapter(Context context)
    {
        this.context=context;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactHolder holder, int position) {

        final Contact contact=contactList.get(position);
        holder.profileName.setText(contact.getName());
        holder.profileNumber.setText(contact.getNumber());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public void setContactList(List<Contact> contactList)
    {
        this.contactList=contactList;
        notifyDataSetChanged();
    }

    class ContactHolder extends RecyclerView.ViewHolder{

        TextView profileName;
        TextView profileNumber;

        public ContactHolder(@NonNull View itemView) {
            super(itemView);
            profileName=itemView.findViewById(R.id.profileName);
            profileNumber=itemView.findViewById(R.id.profileNumber);
        }
    }

    public Contact getContact(int pos)
    {
        return contactList.get(pos);
    }
}
