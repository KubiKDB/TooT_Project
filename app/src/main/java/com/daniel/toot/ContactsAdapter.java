package com.daniel.toot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.toot.entities.Contacts;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> {
    private List<Contacts> contacts;
    private ContactsListener contactsListener;
    private Context context;

    public ContactsAdapter(List<Contacts> contacts, ContactsListener contactsListener, Context context) {
        this.contacts = contacts;
        this.contactsListener = contactsListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recycler_item,
                        parent,
                        false
                )
        );
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.profile_pic.setOnClickListener(view ->
                contactsListener.onContactClicked(contacts.get(position),position
                ));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView profile_pic;
        TextView name;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_pic = itemView.findViewById(R.id.profile_pic);
            name= itemView.findViewById(R.id.name);
        }

        void setContact(Contacts contact) {
            //TODO set contact
        }
    }
}
