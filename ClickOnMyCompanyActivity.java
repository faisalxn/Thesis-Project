package com.example.user.companycommunity;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ClickOnMyCompanyActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    Button newsButton , chattingButton, audioButton , videoButton , pendingButton , takeLeaveButton, leaveManagementButton , notificationButton , createPollButton , pollsButton , showLocationButton ;
    String companyId;
    private DatabaseReference mUsersDatabase;
    View v1,v2;


    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_on_my_company);

        companyId = getIntent().getStringExtra("companyId");


        newsButton = (Button) findViewById(R.id.newsButton);
        chattingButton = (Button) findViewById(R.id.chattingButton);
        audioButton = (Button) findViewById(R.id.audioButton);
        videoButton = (Button) findViewById(R.id.videoButton);
        pendingButton = (Button) findViewById(R.id.pendingButton);
        takeLeaveButton = (Button) findViewById(R.id.takeLeaveButton);
        leaveManagementButton = (Button) findViewById(R.id.leaveManagementButton);
        notificationButton = (Button) findViewById(R.id.notificationButton);
        createPollButton = (Button) findViewById(R.id.createPollButton);
        pollsButton = (Button) findViewById(R.id.pollsButton);
        showLocationButton = (Button) findViewById(R.id.showLocationButton);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uId = user.getUid();
        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = dataref.child("company").child(companyId);
        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String admin = dataSnapshot.child("admin_id").getValue().toString();
                if(admin.equals(uId)==false) {
                    v1 = findViewById(R.id.leaveManagementButton);
                    v1.setVisibility(View.GONE);

                    v2 = findViewById(R.id.pendingButton);
                    v2.setVisibility(View.GONE);




                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,FireVideo.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);



            }
        });


        showLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,LocationMapActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });



        pollsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,PollsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });



        createPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ClickOnMyCompanyActivity.this,CreatePollActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });




        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ClickOnMyCompanyActivity.this,NotificationsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);
                

            }
        });



        leaveManagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,LeaveManagementActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        takeLeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,TakeLeaveActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);
            }
        });


        newsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,BroadcastNewsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);

            }
        });

        chattingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,ChattingActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });



        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });




        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ClickOnMyCompanyActivity.this,PendingRequestsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(60*1000);
        locationRequest.setFastestInterval(60*1000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());


        String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("location").child(companyId);

        GeoFire geoFire = new GeoFire(df);
        geoFire.setLocation(uId, new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {

            }
        });


    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();

    }

}
