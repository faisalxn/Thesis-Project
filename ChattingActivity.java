package com.example.user.companycommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class ChattingActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase , referenceForList ;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    String uId;
    String companyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);


        companyId = getIntent().getStringExtra("companyId");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        //mUsersDatabase = FirebaseDatabase.getInstance().getReference();

        //Query query = mUsersDatabase.child("users").orderByChild("company_id").equalTo(companyId);

        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.chattingList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        Query query = mUsersDatabase.child("users").orderByChild("company_id").equalTo(companyId);

        FirebaseRecyclerAdapter<User, ChattingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, ChattingViewHolder>(
                User.class,
                R.layout.single_user_for_chat_layout,
                ChattingViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(ChattingViewHolder chattingViewHolder, final User user, int position) {
                chattingViewHolder.setName(user.getName());
                final String userId = getRef(position).getKey();

                chattingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ChattingActivity.this,SingleChattingActivity.class);
                        intent.putExtra("companyId",companyId);
                        intent.putExtra("userId",userId);
                        //intent.putExtra("name",user.getName());



                        startActivity(intent);

                        //Toast.makeText(ChattingActivity.this,user.getName(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ChattingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ChattingViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.singleNameForChat);
            name.setText(s);
        }
    }

}
