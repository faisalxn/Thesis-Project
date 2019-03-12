package com.example.user.companycommunity;

import android.content.Intent;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class PollsActivity extends AppCompatActivity {

    private RecyclerView pollsList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    String uId;
    String companyId;
    Toolbar toolbar ;
    ImageView imageView ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        companyId = getIntent().getStringExtra("companyId");


        toolbar = (Toolbar) findViewById(R.id.poll_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Polls");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        imageView = (ImageView) findViewById(R.id.add_poll);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PollsActivity.this,CreatePollActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);

            }
        });

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("poll").child(companyId);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        pollsList = (RecyclerView) findViewById(R.id.pollsList);
        pollsList.setHasFixedSize(true);
        pollsList.setLayoutManager(mLayoutManager);

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Poll,PollViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Poll, PollViewHolder>(
                Poll.class,
                R.layout.single_poll_layout,
                PollViewHolder.class,
                mUsersDatabase

        ) {
            @Override
            protected void populateViewHolder(PollViewHolder pollViewHolder, final Poll model, int position) {
                pollViewHolder.setDes(model.getDes());
                pollViewHolder.setName(model.getName());
                pollViewHolder.setOp1(model.getText1());
                pollViewHolder.setOp2(model.getText2());

                int count = Integer.parseInt(model.getCount());

                if(count>=3) { pollViewHolder.setOp3(model.getText3(), true); }
                else { pollViewHolder.setOp3(model.getText3(), false); }




                if(count>=4) { pollViewHolder.setOp4(model.getText4(), true); }
                else { pollViewHolder.setOp4(model.getText4(), false); }




                if(count>=5) { pollViewHolder.setOp5(model.getText5(), true); }
                else { pollViewHolder.setOp5(model.getText5(), false); }

                final String pollId = getRef(position).getKey();

                pollViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(PollsActivity.this,PollDetailsActivity.class);
                        intent.putExtra("companyId", companyId);
                        intent.putExtra("pollId",pollId);
                        //intent.putExtra("count",model.getCount());

                        //intent.putExtra("c1",model.getTextC1());
                        //intent.putExtra("c2",model.getTextC2());
                        //intent.putExtra("c3",model.getTextC3());
                        //intent.putExtra("c4",model.getTextC4());
                        //intent.putExtra("c5",model.getTextC5());


                        intent.putExtra("model", (Parcelable) model);

                        startActivity(intent);

                    }
                });


            }
        };

        pollsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PollViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public PollViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDes(String s){
            TextView des = (TextView) mView.findViewById(R.id.single_poll_des_text);
            des.setText(s);
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.single_poll_name_text);
            name.setText("Created by " + s);
        }


        public void setOp1(String s){
            TextView op1 = (TextView) mView.findViewById(R.id.single_poll_op1_text);
            op1.setText(s);
        }

        public void setOp2(String s){
            TextView op2 = (TextView) mView.findViewById(R.id.single_poll_op2_text);
            op2.setText(s);
        }

        public void setOp3(String s , boolean flag){
            TextView op3 = (TextView) mView.findViewById(R.id.single_poll_op3_text);
            op3.setText(s);

            if(flag==false){
                op3.setVisibility(View.GONE);
                ImageView op = (ImageView) mView.findViewById(R.id.single_poll_op3_image);
                op.setVisibility(View.GONE);
            }

        }

        public void setOp4(String s , boolean flag){
            TextView op4 = (TextView) mView.findViewById(R.id.single_poll_op4_text);
            op4.setText(s);

            if(flag==false){
                op4.setVisibility(View.GONE);
                ImageView op = (ImageView) mView.findViewById(R.id.single_poll_op4_image);
                op.setVisibility(View.GONE);
            }

        }

        public void setOp5(String s , boolean flag){
            TextView op5 = (TextView) mView.findViewById(R.id.single_poll_op5_text);
            op5.setText(s);

            if(flag==false){
                op5.setVisibility(View.GONE);
                ImageView op = (ImageView) mView.findViewById(R.id.single_poll_op5_image);
                op.setVisibility(View.GONE);
            }

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
        Intent intent = new Intent(PollsActivity.this,WelcomeActivity.class);

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
