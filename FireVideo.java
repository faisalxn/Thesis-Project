package com.example.user.companycommunity;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class FireVideo extends AppCompatActivity {

    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler;
    private AudioManager audioManager;
    private String companyId,uId,name;
    private DatabaseReference notification ;
    private ArrayList<String> strings ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_video);

        companyId = getIntent().getStringExtra("companyId");
        uId = getIntent().getStringExtra("uId");
        name = getIntent().getStringExtra("name");

        strings = new ArrayList<>();
        notification = FirebaseDatabase.getInstance().getReference().child("notifications").child(companyId);
        strings.clear();
        notification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(strings.size()==0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        String key = snapshot.getKey();
                        if(key.equals(uId)==false) {
                            strings.add(key);
                            //Toast.makeText(FireVideo.this,key,Toast.LENGTH_LONG).show();
                        }
                    }
                    createJoinNotification();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setSpeakerphoneOn(true);

        mRtcEventHandler = new IRtcEngineEventHandler() {


            @Override
            public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
                Log.i("uid video",uid+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(uid);
                    }
                });
            }


        };
        initializeAgoraEngine();
    }


    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), "8ba56d2e2e6c4718be69720ca3028bbf", mRtcEventHandler);
            joinChannel();
            setupLocalVideo();
            setupVideoProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    private void createJoinNotification(){
        for(String uKey : strings){
            String key = notification.child(uKey).push().getKey();
            //Log.e("notification",uKey+" "+key);
            notification.child(uKey).child(key).child("name").setValue(name + " joined in video call");
        }
    }


    private void createLeaveNotification(){
        for(String uKey : strings){
            String key = notification.child(uKey).push().getKey();
            notification.child(uKey).child(key).child("name").setValue(name + " leaved from video call");
        }
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, companyId, "Extra Optional Data", new Random().nextInt(10000000)+1); // if you do not specify the uid, Agora will assign one.
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid);

    }

    private void leaveChannel() {
        createLeaveNotification();
        mRtcEngine.leaveChannel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        leaveChannel();
        audioManager.setSpeakerphoneOn(false);
    }
}
