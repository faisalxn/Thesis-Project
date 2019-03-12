package com.example.user.companycommunity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AllPastPrintActivity extends AppCompatActivity {

    private RecyclerView allPastLeavesList;
    private DatabaseReference mUsersDatabase,mUsersDatabase2;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    String uId,admin_id;
    String companyId;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_past_print);

        companyId = getIntent().getStringExtra("companyId");
        toolbar = (Toolbar) findViewById(R.id.past_print_toolbar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Printing records");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = reference.child("prints").child(companyId);
        mUsersDatabase2 = reference.child("company").child(companyId);

        mUsersDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                admin_id = dataSnapshot.child("admin_id").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mLayoutManager = new LinearLayoutManager(this);
        allPastLeavesList = (RecyclerView) findViewById(R.id.allPastPrintsList);
        allPastLeavesList.setHasFixedSize(true);
        allPastLeavesList.setLayoutManager(mLayoutManager);

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
        Intent intent = new Intent(AllPastPrintActivity.this,WelcomeActivity.class);

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

        FirebaseRecyclerAdapter<Print, AllPastPrintsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Print, AllPastPrintsViewHolder>(
                        Print.class,
                        R.layout.single_print_layout,
                        AllPastPrintsViewHolder.class,
                        mUsersDatabase
                ) {
                    @Override
                    protected void populateViewHolder(AllPastPrintsViewHolder allPastPrintsViewHolder, final Print print, int position) {
                        allPastPrintsViewHolder.setName(print.getName());
                        allPastPrintsViewHolder.setPages(print.getPages());
                        allPastPrintsViewHolder.setPayment(print.getPayment());

                        final String user_id = getRef(position).getKey();

                        allPastPrintsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(admin_id.equals(uId)){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AllPastPrintActivity.this);

                                    builder.setTitle("Printing record")
                                            .setMessage("Are you sure to clear record of "+print.getName()+" ?" )
                                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    mUsersDatabase.child(user_id).child("pages").setValue("0");
                                                    mUsersDatabase.child(user_id).child("payment").setValue("0");
                                                }
                                            })
                                            .setNegativeButton("no",null);

                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();



                                }


                            }
                        });

                    }
                };
        allPastLeavesList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllPastPrintsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public AllPastPrintsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.single_print_name);
            name.setText(s);
        }

        public void setPages(String s){
            TextView days = (TextView) mView.findViewById(R.id.single_print_pages);
            days.setText(s);
        }

        public void setPayment(String s){
            TextView days = (TextView) mView.findViewById(R.id.single_print_payment);
            days.setText(s);
        }

    }


}
