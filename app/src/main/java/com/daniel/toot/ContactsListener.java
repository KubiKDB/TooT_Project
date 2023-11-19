package com.daniel.toot;

import com.daniel.toot.entities.Contacts;

public interface ContactsListener {
    void onContactClicked(Contacts contact, int position);
}
