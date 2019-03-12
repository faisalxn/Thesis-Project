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
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase,reference,remove_notification,admin_reference;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    String uId;
    String companyId,admin_id;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list);


        companyId = getIntent().getStringExtra("companyId");


        toolbar = (Toolbar) findViewById(R.id.employee_list_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Employees");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        reference = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = reference.child("request").child(companyId);
        remove_notification = reference.child("notifications").child(companyId);

        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.employee_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);


        admin_reference = reference.child("company").child(companyId);
        admin_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                admin_id = dataSnapshot.child("admin_id").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
        Intent intent = new Intent(EmployeeListActivity.this,WelcomeActivity.class);

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

        Query query = FirebaseDatabase.getInstance().getReference().child("request").child(companyId).orderByChild("join_status").equalTo("accepted");

        FirebaseRecyclerAdapter<User, EmployeeViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, EmployeeViewHolder>(
                User.class,
                R.layout.single_user_layout,
                EmployeeViewHolder.class,
                //mUsersDatabase
                query
        ) {
            @Override
            protected void populateViewHolder(EmployeeViewHolder pendingViewHolder, final User user, int position) {
                pendingViewHolder.setName(user.getName());
                pendingViewHolder.setEmail(user.getEmail());
                pendingViewHolder.setMobile(user.getMobile());
                pendingViewHolder.setStatus(user.getJoin_status());


                final String userId = getRef(position).getKey();

                pendingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(user.getJoin_status().equals("accepted") && admin_id.equals(userId)==false && uId.equals(admin_id)==true) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeListActivity.this);

                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mUsersDatabase.child(userId).child("join_status").removeValue();
                                    mUsersDatabase.child(userId).child("name").removeValue();
                                    mUsersDatabase.child(userId).child("email").removeValue();
                                    mUsersDatabase.child(userId).child("mobile").removeValue();

                                    remove_notification.child(userId).removeValue();

                                    reference.child("users").child(userId).child("company_id").removeValue();
                                    reference.child("users").child(userId).child("join_status").setValue("nill")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(EmployeeListActivity.this,"Employee removed from company.",Toast.LENGTH_LONG).show();
                                                    }
                                                    else {
                                                        Toast.makeText(EmployeeListActivity.this,"Employee removed failed",Toast.LENGTH_LONG).show();

                                                    }

                                                }
                                            });
                                }
                            });


                            builder.setNegativeButton("No",null);
                            builder.setMessage("Are you sure to remove this employee?");
                            builder.setTitle("Remove Employee");

                            AlertDialog dialog = builder.create();
                            dialog.show();






                        }

                        //Toast.makeText(EmployeeListActivity.this,user.getName(),Toast.LENGTH_LONG).show();

                    }
                });


            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class EmployeeViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public EmployeeViewHolder(View itemView) {
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
