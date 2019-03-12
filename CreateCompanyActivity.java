package com.example.user.companycommunity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateCompanyActivity extends AppCompatActivity {

    EditText companyNameText , companyAddressText , aboutCompanyText ;
    Button createCompanyButton ;
    ProgressDialog loading;
    DatabaseReference admin_info,database,request_table;
    private FirebaseAuth mAuth;
    String uId,email,mobile,name ;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);

        companyNameText = (EditText) findViewById(R.id.companyNameText);
        companyAddressText = (EditText) findViewById(R.id.companyAddressText);
        aboutCompanyText = (EditText) findViewById(R.id.aboutCompanyText);
        createCompanyButton = (Button) findViewById(R.id.createCompanyButton);

        toolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create company");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        loading = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        database = FirebaseDatabase.getInstance().getReference("");

        admin_info = database.child("users").child(uId);

        admin_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue().toString();
                mobile = dataSnapshot.child("mobile").getValue().toString();
                name = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        createCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String companyName,companyAddress,aboutCompany ;

                companyName = companyNameText.getText().toString();
                companyAddress = companyAddressText.getText().toString();
                aboutCompany = aboutCompanyText.getText().toString();

                createCompany(companyName,companyAddress,aboutCompany);
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
        Intent intent = new Intent(CreateCompanyActivity.this,WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void createCompany(final String companyName, final String companyAddress, final String aboutCompany) {
        if(TextUtils.isEmpty(companyName) || TextUtils.isEmpty(companyAddress) || TextUtils.isEmpty(aboutCompany) ){
            Toast.makeText(CreateCompanyActivity.this,"Provide data in all fields",Toast.LENGTH_LONG).show();
        }
        else {
            //loading.setTitle("Create Company");
            //loading.setMessage("Please wait untill creating company is complete.");
            //loading.show();

            final String key = database.child("company").push().getKey();

            DatabaseReference myRef = database.child("company").child(key);
            final DatabaseReference insert_notification = database.child("notifications").child(key).child(uId);
            final String key_notification = insert_notification.push().getKey();
            request_table = database.child("request").child(key).child(uId);


            request_table.child("name").setValue(name);
            request_table.child("email").setValue(email);
            request_table.child("mobile").setValue(mobile);
            request_table.child("join_status").setValue("accepted");

            admin_info.child("company_id").setValue(key);
            admin_info.child("join_status").setValue("accepted");


            myRef.child("admin_id").setValue(uId);
            myRef.child("company_name").setValue(companyName);
            myRef.child("company_address").setValue(companyAddress);
            myRef.child("about_company").setValue(aboutCompany)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                insert_notification.child(key_notification).child("name").setValue("Thank you for creating a company.");
                                database.child("users").child(uId).child("company_id").setValue(key)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    //loading.dismiss();
                                                    Toast.makeText(CreateCompanyActivity.this,"Company Created successfully.",Toast.LENGTH_LONG).show();

                                                }
                                                else {
                                                    //loading.dismiss();
                                                    Toast.makeText(CreateCompanyActivity.this,"Failed.",Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });
                            }
                            else{
                                Toast.makeText(CreateCompanyActivity.this,"Company is not created "+task.getException(),Toast.LENGTH_LONG).show();
                                loading.dismiss();
                            }
                        }
                    });
        }
    }
}
