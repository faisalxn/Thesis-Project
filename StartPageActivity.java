package com.example.user.companycommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartPageActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String companyId,join_status;
    boolean flag ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        mAuth = FirebaseAuth.getInstance();


        Thread thread = new Thread(){
            @Override
            public void run(){
                try{
                    sleep(2000);
                } catch ( Exception e){
                    e.printStackTrace();
                } finally {

                    if(mAuth.getCurrentUser()!=null) {

                        //----------------------------------------------------------------------------------------------------------------
                        flag = true ;
                        String uId = mAuth.getCurrentUser().getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        DatabaseReference databaseReference = reference.child("users").child(uId);

                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(flag) {
                                        flag = false;


                                        join_status = dataSnapshot.child("join_status").getValue().toString();

                                        //Toast.makeText(StartPageActivity.this,companyId,Toast.LENGTH_LONG).show();
                                        Intent intent;
                                        if (join_status.equals("nill") || join_status.equals("pending")) {
                                            intent = new Intent(StartPageActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        } else if (join_status.equals("accepted")) {
                                            if (dataSnapshot.hasChild("company_id")) {
                                                companyId = dataSnapshot.child("company_id").getValue().toString();
                                            }
                                            intent = new Intent(StartPageActivity.this, NewClickOnMyCompanyActivity.class);
                                            intent.putExtra("companyId", companyId);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(StartPageActivity.this,"Problem occured in server",Toast.LENGTH_LONG).show();

                            }
                        });


                        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
                        connectedRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean connected = dataSnapshot.getValue(Boolean.class);
                                if(connected == false){

                                    Toast.makeText(StartPageActivity.this,"Check your internet connection",Toast.LENGTH_LONG).show();

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                        //----------------------------------------------------------------------------------------------------------------
                    }
                    else {

                        Intent intent;
                        intent = new Intent(StartPageActivity.this, WelcomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }

                }
            }
        };
        thread.start();

    }



}
