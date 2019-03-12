package com.example.user.companycommunity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.v7.app.ActionBarActivity;

public class CallActivity extends AppCompatActivity {

    private static final String APP_KEY = "420c1732-923e-4b0c-89cf-adcef29304cc";
    private static final String APP_SECRET = "wEYrWu/rPk+a7rHLSLr0tQ==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView callState,recipientText,outgoingDurationText ;
    private SinchClient sinchClient;
    private Button button,receive,anotherButton ;
    private String callerId;
    private String recipientId;
    private MediaPlayer mp ;
    private long mCallStart = 0;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;
    private FirebaseAuth mAuth;
    private ImageView call_speaker;
    private AudioManager audioManager;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
            outgoingDurationText.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
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
        setContentView(R.layout.call);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uId = currentUser.getUid();

        recipientId = getIntent().getStringExtra("recipientId");
        callerId =  uId;

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.start();

        button = (Button) findViewById(R.id.button);
        callState = (TextView) findViewById(R.id.callState);
        outgoingDurationText = (TextView) findViewById(R.id.outgoingDurationText);
        call_speaker = (ImageView) findViewById(R.id.call_speaker);


        call_speaker.setVisibility(View.INVISIBLE);
        call_speaker.setEnabled(false);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        call_speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                }
                else {
                    audioManager.setSpeakerphoneOn(true);
                }

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                if(call==null){
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    //button.setText("Hang Up");
                }
                else {
                    call.hangup();
                    call = null;
                    callState.setText("Ended");

                    if(mTimer!=null) {
                        mDurationTask.cancel();
                        mTimer.cancel();
                    }
                    finish();
                }
                button.setEnabled(true);
            }
        });

        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("users").child(recipientId);
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                recipientText = (TextView) findViewById(R.id.recipientText);
                recipientText.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            callState.setText("Ended");

            if(mTimer!=null) {
                mDurationTask.cancel();
                mTimer.cancel();
            }

            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            finish();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("Connected");
            button.setText("Hang Up");
            call_speaker.setVisibility(View.VISIBLE);
            call_speaker.setEnabled(true);

            mTimer = new Timer();
            mDurationTask = new UpdateCallDurationTask();
            mTimer.schedule(mDurationTask, 0, 500);
            mCallStart = System.currentTimeMillis();

            //Toast.makeText(CallActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
            button.setText("Hang Up");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

        }
    }
}

