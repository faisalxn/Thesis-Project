package com.example.user.companycommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private Intent serviceIntent;
    private ImageButton create,join;

    String join_status,companyId;

    boolean flag ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //serviceIntent=new Intent(getApplicationContext(),NotificationService.class);
        //startService(serviceIntent);


        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Company Community System");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        create = (ImageButton) findViewById(R.id.imageButton);
        join = (ImageButton) findViewById(R.id.imageButton2);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateCompanyActivity.class);
                startActivity(intent);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,JoinCompanyActivity.class);
                startActivity(intent);
            }
        });

        flag = true;

        String uId = mAuth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = reference.child("users").child(uId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(flag) {


                    join_status = dataSnapshot.child("join_status").getValue().toString();
                    Intent intent;

                    if (join_status.equals("accepted")) {
                        if (dataSnapshot.hasChild("company_id")) {
                            companyId = dataSnapshot.child("company_id").getValue().toString();
                        }
                        intent = new Intent(MainActivity.this, NewClickOnMyCompanyActivity.class);
                        intent.putExtra("companyId", companyId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        flag = false;
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"Problem occured in server",Toast.LENGTH_LONG).show();

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            logoutUser();
        }
    }

    private void logoutUser() {
        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.company_menu,menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_account ){
            Toast.makeText(MainActivity.this,"Account",Toast.LENGTH_SHORT).show();

        }

        if(item.getItemId() == R.id.menu_logout ){
            //Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT).show();

            mAuth.signOut();
            logoutUser();

        }


        return true;
    }
}
