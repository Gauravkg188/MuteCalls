package com.kg.muteCalls.room;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.Objects;

public class ContactViewModel extends AndroidViewModel {

    private ContactRepository contactRepository;
    private LiveData<List<Contact>> contactList;
    Application application;

    public ContactViewModel(@NonNull Application application) {
        super(application);
        this.application=application;
        contactRepository=new ContactRepository(application);
        contactList=contactRepository.getContactList();
    }



    public void insert(Contact contact){ contactRepository.insert(contact);}
    public void delete(Contact contact){ contactRepository.delete(contact);}
    public void deleteAllContact(){ contactRepository.deleteAllContact();}

    public LiveData<List<Contact>>  getContactList() {
        return contactList;
    }
}
