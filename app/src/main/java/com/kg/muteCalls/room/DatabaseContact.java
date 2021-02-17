package com.kg.muteCalls.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Contact.class},version = 3)
public abstract class DatabaseContact extends RoomDatabase {

    private static DatabaseContact Instance;
   private static final int No_Of_Threads=4;
   public static  final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(No_Of_Threads);

   public abstract DaoInterface daoInterface();


   public static synchronized DatabaseContact getInstance(Context context)
   {
          if(Instance==null)
          {
              Instance= Room.databaseBuilder(context.getApplicationContext(),DatabaseContact.class,
                      "databaseContact")
                      .fallbackToDestructiveMigration()
                      .build();
          }

          return Instance;
   }


}
