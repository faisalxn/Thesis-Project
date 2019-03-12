package com.example.user.companycommunity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class NewClickOnMyCompanyActivity extends AppCompatActivity implements
                    BottomNavigationView.OnNavigationItemSelectedListener,
                    GoogleApiClient.ConnectionCallbacks,
                    GoogleApiClient.OnConnectionFailedListener,
                    com.google.android.gms.location.LocationListener {


    Toolbar toolbar;
    String companyId,join_status;
    private FirebaseAuth mAuth;
    private Intent serviceIntent1,serviceIntent2;

    GoogleApiClient googleApiClient;
    Location lastLocation;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_click_on_my_company);


        companyId = getIntent().getStringExtra("companyId");

        serviceIntent1=new Intent(getApplicationContext(),NotificationService.class);
        startService(serviceIntent1);

        serviceIntent2=new Intent(getApplicationContext(),PushService.class);
        serviceIntent2.putExtra("companyId",companyId);
        startService(serviceIntent2);




        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.company_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Company Community System");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment(companyId));



        String uId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference databaseReference = reference.child("users").child(uId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    join_status = dataSnapshot.child("join_status").getValue().toString();


                    Intent intent;
                    if (join_status.equals("nill") || join_status.equals("pending")) {
                        intent = new Intent(NewClickOnMyCompanyActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                        Toast.makeText(NewClickOnMyCompanyActivity.this,"You are removed from company",Toast.LENGTH_LONG).show();

                        stopService();
                        startActivity(intent);
                        finish();
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NewClickOnMyCompanyActivity.this,"Problem occured in server",Toast.LENGTH_LONG).show();

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.company_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_account ){
            Toast.makeText(NewClickOnMyCompanyActivity.this,"Account",Toast.LENGTH_SHORT).show();
            /*
            Intent intent;
            intent=new Intent(getApplicationContext(),AccountActivity.class);
            startActivity(intent);
            */
        }
        if(item.getItemId() == R.id.menu_logout ){
            //Toast.makeText(NewClickOnMyCompanyActivity.this,"Logout",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            logoutUser();
        }
        return true ;
    }


    public void stopService(){

        //------------------------------stop service--------------------------------
        Intent serviceIntent1,serviceIntent2;
        serviceIntent1=new Intent(getApplicationContext(),NotificationService.class);
        stopService(serviceIntent1);

        serviceIntent2=new Intent(getApplicationContext(),PushService.class);
        serviceIntent2.putExtra("companyId",companyId);
        stopService(serviceIntent2);
        //------------------------------stop service--------------------------------


    }

    private void logoutUser() {
        Intent intent = new Intent(NewClickOnMyCompanyActivity.this,WelcomeActivity.class);


        stopService();


        googleApiClient.disconnect();
        finish();
        startActivity(intent);
    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            logoutUser();
        }else{
            buildGoogleApiClient();
        }
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null ;

        switch (item.getItemId()){

            case R.id.navigation_home :
                fragment = new HomeFragment(companyId);
                break;

            case R.id.navigation_dashboard :
                fragment = new DashboardFragment(companyId);
                break;

            case R.id.navigation_notifications :
                fragment = new NotificationsFragment(companyId);
                break;

            case R.id.navigation_company :
                fragment = new CompanyFragment(companyId);
                break;
        }


        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.company_frameLayout,fragment)
                    .commit();
            return true ;
        }
        return false;
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
