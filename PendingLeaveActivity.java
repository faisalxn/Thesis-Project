package com.example.user.companycommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PendingLeaveActivity extends AppCompatActivity {

    private RecyclerView allLeavesList;
    private DatabaseReference mUsersDatabase,mUsersDatabase2,mUsersDatabase3;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private DatabaseReference dataref;
    String uId;
    String companyId;
    private int days;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_leave);

        companyId = getIntent().getStringExtra("companyId");

        toolbar = (Toolbar) findViewById(R.id.pending_leave_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All leave requests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        dataref = FirebaseDatabase.getInstance().getReference();


        mUsersDatabase = dataref.child("leave_request").child(companyId);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        allLeavesList = (RecyclerView) findViewById(R.id.allLeavesList);
        allLeavesList.setHasFixedSize(true);
        allLeavesList.setLayoutManager(mLayoutManager);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(allLeavesList.getContext(), mLayoutManager.getOrientation());
        allLeavesList.addItemDecoration(dividerItemDecoration);


        mUsersDatabase2 = dataref.child("leaves").child(companyId);
        mUsersDatabase3 = dataref.child("notifications").child(companyId);
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
        Intent intent = new Intent(PendingLeaveActivity.this,WelcomeActivity.class);

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

        Query query = dataref.child("leave_request").child(companyId).orderByChild("status").equalTo("pending");

        FirebaseRecyclerAdapter<Leave, PendingLeaveViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Leave, PendingLeaveViewHolder>(
                Leave.class,
                R.layout.single_leave_layout,
                PendingLeaveViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(PendingLeaveViewHolder pendingLeaveViewHolder, final Leave leave, int position) {
                pendingLeaveViewHolder.setName(leave.getName());
                pendingLeaveViewHolder.setDays(leave.getDays());
                pendingLeaveViewHolder.setReason(leave.getReason());
                pendingLeaveViewHolder.setStatus(leave.getDate());

                final int temp = Integer.parseInt(leave.getDays()) ;
                final String leaveId = getRef(position).getKey();
                final String key = mUsersDatabase3.push().getKey();

                days = 0 ;
                mUsersDatabase2.child(leave.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild("days")) {
                            String d = dataSnapshot.child("days").getValue().toString();
                            days = Integer.parseInt(d);
                            //Toast.makeText(getApplicationContext(),days+" "+temp,Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                pendingLeaveViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(leave.getStatus().equals("pending")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PendingLeaveActivity.this);
                            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    Map<String, Object> map2 = new HashMap<String, Object>();
                                    Map<String, Object> map3 = new HashMap<String, Object>();


                                    map.put("name", leave.getName() );
                                    map.put("days", leave.getDays() );
                                    map.put("reason", leave.getReason() );
                                    map.put("status", "accepted" );
                                    map.put("userId", leave.getUserId() );

                                    mUsersDatabase.child(leaveId).updateChildren(map);

                                    days = days + temp;
                                    String d  = ""+days;

                                    map2.put("name", leave.getName() );
                                    map2.put("days", d );
                                    mUsersDatabase2.child(leave.getUserId()).updateChildren(map2);


                                    map3.put("name", "Your leave of "+leave.getDays()+" days for reason "+ leave.getReason() +" is accepted by your company admin." );
                                    mUsersDatabase3.child(leave.getUserId()).child(key).updateChildren(map3);

                                    Toast.makeText(PendingLeaveActivity.this,"Leave of "+leave.getName()+" is accepted.",Toast.LENGTH_LONG).show();
                                }
                            });


                            builder.setNegativeButton("no",null);

                            builder.setMessage("Are you sure to accept this leave request? ")
                                    .setTitle("Leave Request");

                            AlertDialog dialog = builder.create();
                            dialog.show();




                        }

                    }
                });

            }
        };
        allLeavesList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class PendingLeaveViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public PendingLeaveViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.single_leave_name);
            name.setText(s);
        }

        public void setDays(String s){
            TextView days = (TextView) mView.findViewById(R.id.single_leave_days);
            days.setText(s);
        }

        public void setReason(String s){
            TextView reason = (TextView) mView.findViewById(R.id.single_leave_reason);
            reason.setText(s);
        }

        public void setStatus(String s){
            TextView status = (TextView) mView.findViewById(R.id.single_leave_status);
            status.setText(s);
        }

    }


}
