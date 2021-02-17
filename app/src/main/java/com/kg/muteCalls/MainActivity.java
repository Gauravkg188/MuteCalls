/**
 *
 * By Gaurav Kumar
 * application provide functionality to make a list of contact whose calls be in silent mode always even if user phone is in normal mode
 */


package com.kg.muteCalls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kg.muteCalls.R;
import com.kg.muteCalls.room.Contact;
import com.kg.muteCalls.room.ContactViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ContactViewModel contactViewModel;
    private RecyclerView recyclerView;
    private FloatingActionButton actionButton;
    private Adapter adapter;
    final static int REQUEST_CODE=1;
    final static int CODE=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter=new Adapter(MainActivity.this);
        recyclerView.setAdapter(adapter);
        actionButton=findViewById(R.id.addContact);
        contactViewModel= new ViewModelProvider(this).get(ContactViewModel.class);
        contactViewModel.getContactList().observe(this, new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                adapter.setContactList(contacts);
            }
        });



        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final Contact contact = adapter.getContact(viewHolder.getAdapterPosition());
                if (direction == ItemTouchHelper.LEFT) {

                    contactViewModel.delete(contact);
                    Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    Snackbar.make(recyclerView, null, Snackbar.LENGTH_LONG)
                            .setAction("Undo delete", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    contactViewModel.insert(contact);
                                    Toast.makeText(MainActivity.this, "undo successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }

            }
        }).attachToRecyclerView(recyclerView);
    }

    private void checkPermission() {


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED )

        {
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_PHONE_STATE,

            },CODE);

        }
        else
        {
            openContact();
        }



    }

    private void openContact() {


        Context context=getApplicationContext();

        NotificationManager manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(!manager.isNotificationPolicyAccessGranted())
        {
            Intent intent=new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }

        if(!manager.isNotificationPolicyAccessGranted())
        {
            Toast.makeText(this,"Please grant access",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==REQUEST_CODE && data!=null)
        {
            Uri contactData=data.getData();
            ContentResolver contentResolver=getContentResolver();
            Cursor cursor=contentResolver.query(contactData,null,null,null,null);

            if (cursor.moveToFirst()) {

                String cNumber = null;
                String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID ));

                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if (hasPhone.equalsIgnoreCase("1")) {
                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null, null);
                    phones.moveToFirst();
                    cNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Log.d("contactAdd", "onActivityResult: " + cNumber);
                }
                String cName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Log.d("contactAdd", "onActivityResult: " + cName);

                Contact contact=new Contact(cName,cNumber);
                contactViewModel.insert(contact);
                adapter.notifyDataSetChanged();


            }

        }
    }


}