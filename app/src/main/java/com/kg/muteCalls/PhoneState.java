package com.kg.muteCalls;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.kg.muteCalls.room.Contact;
import com.kg.muteCalls.room.DaoInterface;
import com.kg.muteCalls.room.DatabaseContact;

import java.util.ArrayList;
import java.util.List;

public class PhoneState extends BroadcastReceiver {

     AudioManager audioManager;
     List<Contact> contactList=new ArrayList<>();

     static int initialState;
    final static int CODE=101;





    @Override
    public void onReceive(final Context context, final Intent intent) {


        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);



        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){

            initialState = audioManager.getRingerMode();

            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if(audioManager.getRingerMode()==2){

            muteCall(incomingNumber,context);
            }

        }
        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
            if (initialState == 2) {

                if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NOTIFICATION_POLICY)!= PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,"Give required permission",Toast.LENGTH_LONG).show();
                }
                else
                {

                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);}

            }
        }




    }

    private void muteCall(final String incomingNumber, Context context) {
        DatabaseContact databaseContact=DatabaseContact.getInstance(context);
        final DaoInterface daoInterface=databaseContact.daoInterface();


        DatabaseContact.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                contactList=daoInterface.getContact();



               if (contactList != null){

                    for (int i=0;i<contactList.size();i++){
                        Contact con=contactList.get(i);
                        if (PhoneNumberUtils.compare(con.getNumber(), incomingNumber)){

                            if(audioManager.getRingerMode()==2)
                            { audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                             }
                            break;
                        }
                    }


                }





            }
        });

    }


}



