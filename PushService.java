package com.example.user.companycommunity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PushService extends IntentService {
    DatabaseReference reference;
    boolean flag ;
    String companyId,uId;

    public PushService(String name) {
        super(PushService.class.getSimpleName());
    }

    public PushService(){
        super(PushService.class.getSimpleName());

    }


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid() ;
        flag = false;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        companyId = intent.getStringExtra("companyId");
        reference = FirebaseDatabase.getInstance().getReference();

        Query notification = reference.child("notifications").child(companyId).child(uId).orderByKey().limitToLast(1);

        notification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(flag==false){
                    flag = true;
                }
                else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String message = snapshot.child("name").getValue().toString();
                        createNotification(message);
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void createNotification(String message ){
        Context context = getApplicationContext();
        android.app.Notification.Builder mBuilder = new android.app.Notification.Builder(context)
                .setSmallIcon(R.drawable.ic_push)
                .setContentTitle(message)
                //.setContentText(message)
                .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // notificationId is a unique int for each notification that you must define

        Notification notification = mBuilder.build() ;
        notificationManager.notify(1, notification);


        MediaPlayer mediaPlayer = MediaPlayer.create(this,R.raw.slow_spring_board_longer_tail);
        mediaPlayer.start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}
