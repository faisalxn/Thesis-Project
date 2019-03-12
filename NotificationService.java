package com.example.user.companycommunity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;

public class NotificationService extends Service {

    private static final String APP_KEY = "420c1732-923e-4b0c-89cf-adcef29304cc";
    private static final String APP_SECRET = "wEYrWu/rPk+a7rHLSLr0tQ==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    Call call;
    private SinchClient sinchClient;
    private String callerId;





    public class NotificationServiceBinder extends Binder{
        public NotificationService getService(){
            return NotificationService.this;
        }
    }

    private IBinder mBinder = new NotificationServiceBinder();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;

    }
    /*
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
    */

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        callerId = mAuth.getCurrentUser().getUid() ;
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Toast.makeText(getApplicationContext(),"call service started",Toast.LENGTH_SHORT).show();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());


        return super.onStartCommand(intent, flags, startId);
    }



    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Intent myIntent = new Intent(NotificationService.this,IncomingCallActivity.class);
            startActivity(myIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public Call getCall(){
        return call;
    }

}
