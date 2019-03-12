package com.example.user.companycommunity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PollDetailsActivity extends AppCompatActivity {

    private String uId,companyId,pollId,c1,c2,c3,c4,c5;
    private int count ;
    private Poll poll ;
    private TextView voteOp1Text,voteOp2Text,voteOp3Text,voteOp4Text,voteOp5Text;
    private TextView voteCountOp1,voteCountOp2,voteCountOp3,voteCountOp4,voteCountOp5;
    private TextView voteDes,voteName ;
    private ImageButton voteInOp1,voteInOp2,voteInOp3,voteInOp4,voteInOp5;
    private DatabaseReference df,df2 ;
    Toolbar toolbar;
    boolean voteDone ;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_details);

        auth = FirebaseAuth.getInstance();
        uId = auth.getCurrentUser().getUid();

        getFromIntent();
        initGUI();
        setValueInGUI();
        createDatabaseReference();
        createListeners();

    }

    public void createDatabaseReference(){
        df = FirebaseDatabase.getInstance().getReference().child("poll").child(companyId).child(pollId);
        df2 = FirebaseDatabase.getInstance().getReference().child("vote").child(companyId).child(pollId).child(uId);


        voteDone = false ;

        df2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("option")){
                    voteDone = true ;
                    Toast.makeText(PollDetailsActivity.this, "vote done", Toast.LENGTH_LONG).show();

                }
                else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Poll poll1 = dataSnapshot.getValue(Poll.class);

                poll = poll1 ;
                voteCountOp1.setText(poll1.getTextC1());
                voteCountOp2.setText(poll1.getTextC2());
                if(count>=3)voteCountOp3.setText(poll1.getTextC3());
                if(count>=4)voteCountOp4.setText(poll1.getTextC4());
                if(count>=5)voteCountOp5.setText(poll1.getTextC5());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFromIntent(){
        companyId = getIntent().getStringExtra("companyId");
        pollId = getIntent().getStringExtra("pollId");
        //count = getIntent().getStringExtra("count");

        /*
        c1 = getIntent().getStringExtra("c1");
        c2 = getIntent().getStringExtra("c2");
        c3 = getIntent().getStringExtra("c3");
        c4 = getIntent().getStringExtra("c4");
        c5 = getIntent().getStringExtra("c5");
        */

        poll = (Poll) getIntent().getParcelableExtra("model");
    }


    public void insertData(final String op){


        if(voteDone) {
            Toast.makeText(PollDetailsActivity.this, "You already voted in an option.", Toast.LENGTH_LONG).show();
            return ;

        }


        Map<String,Object> mp = new HashMap<>() ;
        mp.put("count",poll.getCount());
        mp.put("des",poll.getDes());
        mp.put("name",poll.getName());

        mp.put("text1",poll.getText1());
        mp.put("text2",poll.getText2());
        mp.put("text3",poll.getText3());
        mp.put("text4",poll.getText4());
        mp.put("text5",poll.getText5());


        mp.put("textC1",poll.getTextC1());
        mp.put("textC2",poll.getTextC2());
        mp.put("textC3",poll.getTextC3());
        mp.put("textC4",poll.getTextC4());
        mp.put("textC5",poll.getTextC5());

        df.updateChildren(mp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        voteCountOp1.setText(poll.getTextC1());
                        voteCountOp2.setText(poll.getTextC2());
                        if(count>=3)voteCountOp3.setText(poll.getTextC3());
                        if(count>=4)voteCountOp4.setText(poll.getTextC4());
                        if(count>=5)voteCountOp5.setText(poll.getTextC5());

                        df2.child("option").setValue(op);
                        //flag = false ;
                    }
                });

    }


    public void createListeners(){

        voteInOp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(poll.getTextC1());
                n = n+1 ;
                poll.setTextC1(n+"");
                insertData("1");
            }
        });


        voteInOp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(poll.getTextC2());
                n = n+1 ;
                poll.setTextC2(n+"");
                insertData("2");

            }
        });


        voteInOp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(poll.getTextC3());
                n = n+1 ;
                poll.setTextC3(n+"");
                insertData("3");
            }
        });


        voteInOp4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(poll.getTextC4());
                n = n+1 ;
                poll.setTextC4(n+"");
                insertData("4");
            }
        });

        voteInOp5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n = Integer.parseInt(poll.getTextC5());
                n = n+1 ;
                poll.setTextC5(n+"");
                insertData("5");

            }
        });



    }

    public void setValueInGUI(){
        voteDes.setText(poll.getDes());
        voteName.setText("Created by " + poll.getName());

        voteOp1Text.setText(poll.getText1());
        voteOp2Text.setText(poll.getText2());
        voteOp3Text.setText(poll.getText3());
        voteOp4Text.setText(poll.getText4());
        voteOp5Text.setText(poll.getText5());

        voteCountOp1.setText(poll.getTextC1());
        voteCountOp2.setText(poll.getTextC2());
        voteCountOp3.setText(poll.getTextC3());
        voteCountOp4.setText(poll.getTextC4());
        voteCountOp5.setText(poll.getTextC5());


        count = Integer.parseInt(poll.getCount());


        if(count<=2){
            voteInOp5.setVisibility(View.INVISIBLE);
            voteInOp5.setEnabled(false);
            voteOp5Text.setVisibility(View.INVISIBLE);
            voteCountOp5.setVisibility(View.INVISIBLE);


            voteInOp4.setVisibility(View.INVISIBLE);
            voteInOp4.setEnabled(false);
            voteOp4Text.setVisibility(View.INVISIBLE);
            voteCountOp4.setVisibility(View.INVISIBLE);

            voteInOp3.setVisibility(View.INVISIBLE);
            voteInOp3.setEnabled(false);
            voteOp3Text.setVisibility(View.INVISIBLE);
            voteCountOp3.setVisibility(View.INVISIBLE);
        }
        else if(count<=3){
            voteInOp5.setVisibility(View.INVISIBLE);
            voteInOp5.setEnabled(false);
            voteOp5Text.setVisibility(View.INVISIBLE);
            voteCountOp5.setVisibility(View.INVISIBLE);


            voteInOp4.setVisibility(View.INVISIBLE);
            voteInOp4.setEnabled(false);
            voteOp4Text.setVisibility(View.INVISIBLE);
            voteCountOp4.setVisibility(View.INVISIBLE);
        }

        else if(count<=4){
            voteInOp5.setVisibility(View.INVISIBLE);
            voteInOp5.setEnabled(false);
            voteOp5Text.setVisibility(View.INVISIBLE);
            voteCountOp5.setVisibility(View.INVISIBLE);
        }



    }

    public void initGUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Vote in poll");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        voteOp1Text = (TextView)findViewById(R.id.voteOp1Text);
        voteOp2Text = (TextView)findViewById(R.id.voteOp2Text);
        voteOp3Text = (TextView)findViewById(R.id.voteOp3Text);
        voteOp4Text = (TextView)findViewById(R.id.voteOp4Text);
        voteOp5Text = (TextView)findViewById(R.id.voteOp5Text);


        voteCountOp1 = (TextView)findViewById(R.id.voteCountOp1) ;
        voteCountOp2 = (TextView)findViewById(R.id.voteCountOp2) ;
        voteCountOp3 = (TextView)findViewById(R.id.voteCountOp3) ;
        voteCountOp4 = (TextView)findViewById(R.id.voteCountOp4) ;
        voteCountOp5 = (TextView)findViewById(R.id.voteCountOp5) ;


        voteInOp1 = (ImageButton)findViewById(R.id.voteInOp1) ;
        voteInOp2 = (ImageButton)findViewById(R.id.voteInOp2) ;
        voteInOp3 = (ImageButton)findViewById(R.id.voteInOp3) ;
        voteInOp4 = (ImageButton)findViewById(R.id.voteInOp4) ;
        voteInOp5 = (ImageButton)findViewById(R.id.voteInOp5) ;


        voteDes = (TextView) findViewById(R.id.voteDes);
        voteName = (TextView) findViewById(R.id.voteName);


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
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            logoutUser();
        }
        return true ;
    }
    private void logoutUser() {
        Intent intent = new Intent(PollDetailsActivity.this,WelcomeActivity.class);

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
