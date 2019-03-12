package com.example.user.companycommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class MyCompanyActivity extends AppCompatActivity {


    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private DatabaseReference dataref;

    //String companyId;
    String uId,cId;

    ArrayList<String> list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_company);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        dataref = FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = dataref.child("company");
        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.companyList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        dataref.child("users").child(uId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("company_id")) {
                            cId = dataSnapshot.child("company_id").getValue().toString();
                            Toast.makeText(MyCompanyActivity.this,cId,Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
                        Toast.makeText(MyCompanyActivity.this,companyId,Toast.LENGTH_LONG).show();
                        if(companyId.equals(cId)) {
                            Intent intent = new Intent(MyCompanyActivity.this, NewClickOnMyCompanyActivity.class);
                            intent.putExtra("companyId", companyId);
                            startActivity(intent);
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
}
