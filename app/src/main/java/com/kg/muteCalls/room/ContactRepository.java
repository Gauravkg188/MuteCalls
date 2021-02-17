package com.kg.muteCalls.room;

import android.app.Application;

import androidx.lifecycle.LiveData;


import java.util.List;

public class ContactRepository {

    DatabaseContact databaseContact;
    private DaoInterface daoInterface;
    private LiveData<List<Contact>> contactList;

    public ContactRepository(Application application)
    {
        databaseContact=DatabaseContact.getInstance(application);
        daoInterface=databaseContact.daoInterface();
        contactList=daoInterface.getAllContact();
    }

    public void insert(final Contact contact)
    {
        DatabaseContact.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                  daoInterface.insert(contact);
            }
        });
    }

    public void delete(final Contact contact)
    {
        DatabaseContact.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                daoInterface.delete(contact);
            }
        });
    }

    public void deleteAllContact()
    {
        DatabaseContact.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                daoInterface.deleteAllContact();
            }
        });
    }

    public LiveData<List<Contact>> getContactList() {
        return contactList;
    }
}
