package com.example.user.companycommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClickOnJoinCompanyActivity extends AppCompatActivity {

    private DatabaseReference mUsersDatabase,mUsersDatabase2,mUsersDatabase3,mUsersDatabase4 ;
    private FirebaseAuth mAuth;

    TextView joinName , joinAbout , joinAddress ;
    Button joinRequest ;
    String companyId,adminId ;
    String state ;

    String name , email , mobile ;

    //String owner ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_on_join_company);

        joinName = (TextView) findViewById(R.id.joinCompanyName);
        joinAbout = (TextView) findViewById(R.id.joinAboutCompany);
        joinAddress = (TextView) findViewById(R.id.joinCompanyAddress);
        joinRequest = (Button) findViewById(R.id.joinRequestButton);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Send join request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        state = "not_requested";

        companyId = getIntent().getStringExtra("companyId");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        final String uId = user.getUid();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = database.child("company").child(companyId);
        mUsersDatabase4 = database.child("notifications").child(companyId);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //String owner = dataSnapshot.child("admin_id").getValue().toString();
                String companyName = dataSnapshot.child("company_name").getValue().toString();
                String aboutCompany = dataSnapshot.child("about_company").getValue().toString();
                String companyAddress = dataSnapshot.child("company_address").getValue().toString();
                adminId = dataSnapshot.child("admin_id").getValue().toString();



                joinName.setText(companyName);
                joinAbout.setText(aboutCompany);
                joinAddress.setText(companyAddress);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUsersDatabase2 = database.child("request").child(companyId).child(uId);

        mUsersDatabase3 = database.child("users").child(uId);

        mUsersDatabase3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                mobile = dataSnapshot.child("mobile").getValue().toString();

                state = dataSnapshot.child("join_status").getValue().toString();
                if (state.equals("pending")) {
                    joinRequest.setText("Cancel join request");
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        joinRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                joinRequest.setEnabled(false);

                if(state.equals("nill")){

                    //mUsersDatabase3 = FirebaseDatabase.getInstance().getReference().child("company_information").child(owner).child(companyId);
                    mUsersDatabase2.child("join_status").setValue("pending"); // mUsersDatabase2 hoilo request table
                    mUsersDatabase2.child("name").setValue(name);
                    mUsersDatabase2.child("email").setValue(email);
                    mUsersDatabase2.child("mobile").setValue(mobile);

                    mUsersDatabase3.child("join_status").setValue("pending");
                    mUsersDatabase3.child("company_id").setValue(companyId);

                    String key = mUsersDatabase4.child(adminId).push().getKey();

                    mUsersDatabase4.child(adminId).child(key).child("name").setValue(name + " requested for joining your company")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        joinRequest.setText("Cancel join request");
                                        state = "pending" ;

                                        Toast.makeText(ClickOnJoinCompanyActivity.this,"Request successful",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(ClickOnJoinCompanyActivity.this,"Request unsuccessful",Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                }
                else if(state.equals("pending")){


                    //mUsersDatabase3 = FirebaseDatabase.getInstance().getReference().child("company_information").child(owner).child(companyId);
                    mUsersDatabase2.child("join_status").removeValue();
                    mUsersDatabase2.child("name").removeValue();
                    mUsersDatabase2.child("email").removeValue();
                    mUsersDatabase2.child("mobile").removeValue();


                    mUsersDatabase3.child("company_id").removeValue();
                    mUsersDatabase3.child("join_status").setValue("nill")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ((task.isSuccessful())){
                                        joinRequest.setText("Request Join");
                                        state = "nill" ;

                                        Toast.makeText(ClickOnJoinCompanyActivity.this,"Request removed",Toast.LENGTH_SHORT).show();
                                    }
                                    else {

                                    }

                                }
                            });
                }
                joinRequest.setEnabled(true);
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
        if(item.getItemId()==android.R.id.home){
            this.finish();
        }


        if(item.getItemId() == R.id.menu_account ){


        }
        if(item.getItemId() == R.id.menu_logout ){
            //Toast.makeText(NewClickOnMyCompanyActivity.this,"Logout",Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            logoutUser();
        }
        return true ;
    }
    private void logoutUser() {
        Intent intent = new Intent(ClickOnJoinCompanyActivity.this,WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
