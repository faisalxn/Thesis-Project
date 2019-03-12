package com.example.user.companycommunity;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TakeLeaveActivity extends AppCompatActivity {

    private EditText leaveDaysText , leaveReasonText ,leaveDateText ;
    private Button leaveRequestButton ;
    private DatabaseReference database ;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private String days,reason,companyId,name,date ;
    private FirebaseUser user ;
    private String uId,admin_id;
    Toolbar toolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_leave);

        companyId = getIntent().getStringExtra("companyId");

        toolbar = (Toolbar) findViewById(R.id.toolbar6);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Take leave");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        uId = user.getUid();


        database = FirebaseDatabase.getInstance().getReference();
        loading = new ProgressDialog(this);

        leaveDaysText = (EditText) findViewById(R.id.leaveDaysText);
        leaveReasonText = (EditText) findViewById(R.id.leaveReasonText);
        leaveRequestButton = (Button) findViewById(R.id.leaveRequestButton);
        leaveDateText = (EditText) findViewById(R.id.leaveDateText);

        database.child("users").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        database.child("company").child(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                admin_id = dataSnapshot.child("admin_id").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        leaveRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                days = leaveDaysText.getText().toString();
                reason = leaveReasonText.getText().toString();
                date = leaveDateText.getText().toString();


                requestLeave(days,reason);


            }
        });



    }

    private void requestLeave(final String days, final String reason) {
        if(TextUtils.isEmpty(days) || TextUtils.isEmpty(reason)  ){
            Toast.makeText(TakeLeaveActivity.this,"Provide data in all fields",Toast.LENGTH_LONG).show();
        }
        else{
             //loading.setTitle("Take Leave");
             //loading.setMessage("Please wait untill request for leave is complete.");
             //loading.show();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String key = database.child("leave_request").child(companyId).push().getKey();
                    DatabaseReference myRef = database.child("leave_request").child(companyId).child(key);

                    String notification_key = database.child("notifications").child(companyId).child(admin_id).push().getKey();

                    database.child("notifications").child(companyId).child(admin_id).child(notification_key).child("name").setValue(name+" requested "+days+" days leave for "+reason);

                    Map<String,Object> leave_map = new HashMap<>();

                    leave_map.put("userId",uId);
                    leave_map.put("name",name);
                    leave_map.put("days",days);
                    leave_map.put("date",date);
                    leave_map.put("reason",reason);
                    leave_map.put("status","pending");


                    myRef.updateChildren(leave_map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(TakeLeaveActivity.this,"Request occured successfully",Toast.LENGTH_LONG).show();
                                        leaveDaysText.setText("");
                                        leaveReasonText.setText("");
                                        leaveDateText.setText("");
                                    }
                                    else{
                                        Toast.makeText(TakeLeaveActivity.this,"Error ocured in saving information",Toast.LENGTH_LONG).show();
                                    }
                                    //loading.dismiss();
                                }
                            });

                }
            });

            builder.setNegativeButton("no",null);

            builder.setTitle("Take Leave");
             builder.setMessage("Are you sure to take leave?");

             AlertDialog alertDialog = builder.create();
             alertDialog.show();



        }
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
        Intent intent = new Intent(TakeLeaveActivity.this,WelcomeActivity.class);

        //------------------------------stop service--------------------------------
        Intent serviceIntent1,serviceIntent2;
        serviceIntent1=new Intent(getApplicationContext(),NotificationService.class);
        stopService(serviceIntent1);

        serviceIntent2=new Intent(getApplicationContext(),PushService.class);
        serviceIntent2.putExtra("companyId",companyId);
        stopService(serviceIntent2);
        //------------------------------stop service--------------------------------


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}

