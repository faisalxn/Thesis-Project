package com.example.user.companycommunity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class IncomingCallActivity extends AppCompatActivity {
    private NotificationService myService;
    private boolean isServiceBound;
    private ServiceConnection serviceConnection;
    private Intent serviceIntent;
    private Call call;
    private TextView callStateText, durationText , callerIdText ;
    private ImageView receieveButton,hangUp , speakerButton ;
    private MediaPlayer mp ;
    private long mCallStart = 0;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    private AudioManager audioManager;


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            IncomingCallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            durationText.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        speakerButton = (ImageView) findViewById(R.id.speakerButton);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        speakerButton.setVisibility(View.INVISIBLE);
        speakerButton.setEnabled(false);



        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                else
                    audioManager.setSpeakerphoneOn(true);

            }
        });






        hangUp = (ImageView) findViewById(R.id.hangUpButton);
        mp = MediaPlayer.create(this, R.raw.kalimba);

        serviceIntent=new Intent(getApplicationContext(),NotificationService.class);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                NotificationService.NotificationServiceBinder myServiceBinder=(NotificationService.NotificationServiceBinder)iBinder;
                myService=myServiceBinder.getService();
                call = myService.getCall();

                DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("users").child(call.getRemoteUserId());
                df.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = (String) dataSnapshot.child("name").getValue();
                        callerIdText = (TextView) findViewById(R.id.callerIdText);
                        callerIdText.setText(name);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                //((TextView)findViewById(R.id.callerIdText)).setText(call.getRemoteUserId());
                callStateText.setText("ringing");
                mp.start();


            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };

        bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE);

        callStateText = (TextView) findViewById(R.id.callStateText);
        durationText = (TextView) findViewById(R.id.durationText);
        receieveButton = (ImageView) findViewById(R.id.receieveButton);






        receieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mp.stop();

                    call.answer();
                    call.addCallListener(new SinchCallListener());

                    receieveButton.setVisibility(View.INVISIBLE);
                    receieveButton.setEnabled(false);




            }
        });

        hangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call.hangup();
                mp.stop();
                if(mTimer!=null) {
                    mDurationTask.cancel();
                    mTimer.cancel();
                }
                hangUp.setEnabled(false);
                hangUp.setVisibility(View.INVISIBLE);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            hangUp.setEnabled(false);
            hangUp.setVisibility(View.INVISIBLE);
            if(mTimer!=null) {
                mDurationTask.cancel();
                mTimer.cancel();
            }
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            finish();

        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callStateText.setText("connected");
            Toast.makeText(IncomingCallActivity.this, "Call established", Toast.LENGTH_SHORT).show();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);
            mCallStart = System.currentTimeMillis();

            speakerButton.setVisibility(View.VISIBLE);
            speakerButton.setEnabled(true);

        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callStateText.setText("ringing");


        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

        }
    }



}
