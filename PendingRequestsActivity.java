package com.example.user.companycommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PendingRequestsActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase,reference,insert_notification;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    String uId;
    String companyId;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        companyId = getIntent().getStringExtra("companyId");

        toolbar = (Toolbar) findViewById(R.id.pending_requests_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Company join requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase = reference.child("request").child(companyId);
        insert_notification = reference.child("notifications").child(companyId);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);

        mUsersList = (RecyclerView) findViewById(R.id.pendingList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
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
        Intent intent = new Intent(PendingRequestsActivity.this,WelcomeActivity.class);

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

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference().child("request").child(companyId).orderByChild("join_status").equalTo("pending");
        FirebaseRecyclerAdapter<User, PendingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, PendingViewHolder>(
                User.class,
                R.layout.single_user_layout,
                PendingViewHolder.class,
                //mUsersDatabase
                query
        ) {
            @Override
            protected void populateViewHolder(PendingViewHolder pendingViewHolder, final User user, int position) {
                pendingViewHolder.setName(user.getName());
                pendingViewHolder.setEmail(user.getEmail());
                pendingViewHolder.setMobile(user.getMobile());
                pendingViewHolder.setStatus(user.getJoin_status());

                final String userId = getRef(position).getKey();
                pendingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user.getJoin_status().equals("pending")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PendingRequestsActivity.this);
                            // Add the buttons
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button


                                    Map<String, Object> map = new HashMap<String, Object>();

                                    map.put("name", user.getName() );
                                    map.put("email", user.getEmail() );
                                    map.put("mobile", user.getMobile() );
                                    map.put("join_status", "accepted" );

                                    mUsersDatabase.child(userId).updateChildren(map);

                                    reference.child("users").child(userId).child("company_id").setValue(companyId);
                                    reference.child("users").child(userId).child("join_status").setValue("accepted");
                                    String key = insert_notification.child(userId).push().getKey();
                                    insert_notification.child(userId).child(key).child("name").setValue("Hello "+user.getName()+" welcome to the company.")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(PendingRequestsActivity.this,user.getName()+" accepted in company",Toast.LENGTH_LONG).show();
                                                    }
                                                    else {
                                                        Toast.makeText(PendingRequestsActivity.this,"Accept failed",Toast.LENGTH_LONG).show();
                                                    }

                                                }
                                            });



                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            builder.setMessage("Are you sure to accept this join request? ")
                                    .setTitle("Join Request");

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }

                    }
                });
            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PendingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public PendingViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.singleUserNameText);
            name.setText(s);
        }

        public void setEmail(String s){
            TextView email = (TextView) mView.findViewById(R.id.singleEmailText);
            email.setText(s);
        }

        public void setMobile(String s){
            TextView mobile = (TextView) mView.findViewById(R.id.singleMobileText);
            mobile.setText(s);
        }

        public void setStatus(String s){
            TextView status = (TextView) mView.findViewById(R.id.singleJoinStatusText);
            status.setText(s);
        }
    }



}
