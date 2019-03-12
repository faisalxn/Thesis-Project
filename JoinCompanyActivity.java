package com.example.user.companycommunity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinCompanyActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase,mUsersDatabase2;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private String company_id,join_status;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_company);

        toolbar = (Toolbar) findViewById(R.id.join_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Company");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String uId = user.getUid();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = reference.child("company");
        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.companyList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        mUsersDatabase2 = reference.child("users").child(uId);

        mUsersDatabase2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                join_status = dataSnapshot.child("join_status").getValue().toString();
                company_id = "";
                if( (join_status.equals("accepted") || join_status.equals("pending") )&& dataSnapshot.hasChild("company_id") ){

                    company_id = dataSnapshot.child("company_id").getValue().toString();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Toast.makeText(JoinCompanyActivity.this,company_id,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Company, CompanyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Company, CompanyViewHolder>(
                Company.class,
                R.layout.single_my_company_layout,
                CompanyViewHolder.class,
                mUsersDatabase
        ) {
            @Override
            protected void populateViewHolder(CompanyViewHolder companyViewHolder, Company company, int position) {
                companyViewHolder.setCompanyName(company.getCompany_name());
                companyViewHolder.setAboutCompany(company.getAbout_company());
                companyViewHolder.setCompanyAddress(company.getCompany_address());

                final String companyId = getRef(position).getKey();

                companyViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (join_status.equals("nill")){

                            Intent intent = new Intent(JoinCompanyActivity.this, ClickOnJoinCompanyActivity.class);
                            intent.putExtra("companyId",companyId);
                            startActivity(intent);

                        }
                        else if(join_status.equals("pending")==true && companyId.equals(company_id)==true) {

                            Intent intent = new Intent(JoinCompanyActivity.this, ClickOnJoinCompanyActivity.class);
                            intent.putExtra("companyId",companyId);
                            startActivity(intent);
                            //Toast.makeText(JoinCompanyActivity.this, "You already requested for a company.", Toast.LENGTH_SHORT).show();
                        }
                        else if(join_status.equals("pending") && companyId.equals(company_id)==false) {

                            Toast.makeText(JoinCompanyActivity.this, "You already requested for a company.", Toast.LENGTH_SHORT).show();

                        }
                        else if(join_status.equals("accepted")){

                            Toast.makeText(JoinCompanyActivity.this, "You are already in a company try to restart the app.", Toast.LENGTH_SHORT).show();

                        }

                    }
                });


            }
        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public CompanyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setCompanyName(String s){
            TextView companyNameView = (TextView) mView.findViewById(R.id.singleCompanyNameText);
            companyNameView.setText(s);
        }
        public void setAboutCompany(String s){
            TextView aboutCompanyView = (TextView) mView.findViewById(R.id.singleAboutCompanyText);
            aboutCompanyView.setText(s);
        }
        public void setCompanyAddress(String s){
            TextView companytAddressView = (TextView) mView.findViewById(R.id.singleCompanyAddressText);
            companytAddressView.setText(s);
        }
    }


    private void logoutUser() {
        Intent intent = new Intent(JoinCompanyActivity.this,WelcomeActivity.class);
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
            Toast.makeText(JoinCompanyActivity.this,"Account",Toast.LENGTH_SHORT).show();

        }

        if(item.getItemId() == R.id.menu_logout ){
            //Toast.makeText(MainActivity.this,"Logout",Toast.LENGTH_SHORT).show();

            mAuth.signOut();
            logoutUser();

        }


        return true;
    }


}
