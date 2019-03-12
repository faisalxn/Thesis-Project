package com.example.user.companycommunity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SingleChattingActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private android.support.v7.widget.Toolbar toolbar ;
    String companyId , userId , name , name_main ;
    DatabaseReference pointer1,pointer2 ;
    private ImageView callImageButton;
    private RecyclerView mUsersList;
    private DatabaseReference mUsersDatabase,mUsersDatabase2,mUsersDatabase3, historyReference1 , historyReference2  ;
    private LinearLayoutManager mLayoutManager;
    String uId;
    EditText sendingText ;
    ImageButton sendButton ;
    boolean flag1,flag2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chatting);

        companyId = getIntent().getStringExtra("companyId");
        userId = getIntent().getStringExtra("userId");
        name = getIntent().getStringExtra("name");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();


        mAuth = FirebaseAuth.getInstance();
        sendingText = (EditText) findViewById(R.id.sendingText);
        sendButton = (ImageButton) findViewById(R.id.sendButtonForChat);
        callImageButton = (ImageView) findViewById(R.id.callImageButton);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.single_chatting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);

        mUsersList = (RecyclerView) findViewById(R.id.chatList);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();


        database.child("users").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_main = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String history_text = sendingText.getText().toString() ;


                final String key = database.child("chats").child(companyId).child(uId).child(userId).push().getKey();
                mUsersDatabase = database.child("chats").child(companyId).child(uId).child(userId).child(key);
                Map<String,Object> m1 = new HashMap<>();

                m1.put("text",history_text);
                m1.put("type","sent");
                m1.put("name",name_main);

                mUsersDatabase.updateChildren(m1);



                final String key2 = database.child("chats").child(companyId).child(userId).child(uId).push().getKey();
                mUsersDatabase3 = database.child("chats").child(companyId).child(userId).child(uId).child(key2);
                Map<String,Object> m2 = new HashMap<>();

                m2.put("text",history_text);
                m2.put("type","received");
                m2.put("name",name_main);

                mUsersDatabase3.updateChildren(m2);


                if(uId.equals(userId)){
                    return;
                }



                final Query query1 = database.child("history").child(companyId).child(uId).orderByChild("id").equalTo(userId);
                flag1 = true;
                query1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(flag1) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String id = ds.child("id").getValue().toString();
                                if(id.equals(userId)){
                                    ds.getRef().removeValue();
                                    break;
                                }
                            }

                            flag1 = false;

                            historyReference1 = database.child("history").child(companyId).child(uId);
                            final String history_key1 = historyReference1.push().getKey();
                            Map<String, Object> history_map1 = new HashMap<>();
                            history_map1.put("id", userId);
                            history_map1.put("text", history_text);
                            history_map1.put("name", name);
                            historyReference1.child(history_key1).updateChildren(history_map1);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                Query query2 = database.child("history").child(companyId).child(userId).orderByChild("id").equalTo(uId);
                flag2 = true ;
                query2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(flag2) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String id = ds.child("id").getValue().toString();
                                if(id.equals(uId)){
                                    ds.getRef().removeValue();
                                    break;
                                }
                            }

                            flag2 = false;

                            historyReference2 = database.child("history").child(companyId).child(userId);
                            final String history_key2 = historyReference2.push().getKey();
                            Map<String,Object> history_map2 = new HashMap<>();
                            history_map2.put("id"   , uId);
                            history_map2.put("text" , history_text);
                            history_map2.put("name" , name_main  );
                            historyReference2.child(history_key2).updateChildren(history_map2);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                sendingText.setText("");


            }
        });

        callImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleChattingActivity.this,CallActivity.class);
                intent.putExtra("recipientId",userId);
                startActivity(intent);

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
            onBackPressed();
            //this.finish();
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
        Intent intent = new Intent(SingleChattingActivity.this,WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersDatabase2 = FirebaseDatabase.getInstance().getReference().child("chats").child(companyId).child(uId).child(userId);

        FirebaseRecyclerAdapter<Chat, SingleChattingViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chat, SingleChattingViewHolder>(
                Chat.class,
                R.layout.single_chat,
                SingleChattingViewHolder.class,
                mUsersDatabase2
        ) {
            @Override
            protected void populateViewHolder(SingleChattingViewHolder singleChattingViewHolder, final Chat chats, int position) {
                //singleChattingViewHolder.setName(chats.getName(),chats.getType());
                Log.e("Chatting","populate "+chats.getType());
                singleChattingViewHolder.setText(chats.getText(),chats.getType());
            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class SingleChattingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public SingleChattingViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setText(String s , String type){
            TextView text = (TextView) mView.findViewById(R.id.chatText);
            ImageView i = (ImageView) mView.findViewById(R.id.leftImageView);
            //Log.e("Chatting","baire "+type);
            if(type.equals("sent")){
                Log.e("Chatting","vitore");
                //text.setGravity(Gravity.RIGHT);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)text.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                text.setBackgroundResource(R.drawable.chat_background1);
                text.setTextColor(Color.WHITE);
                i.setVisibility(View.GONE);
            }
            else {
                //text.setGravity(Gravity.LEFT);
                text.setBackgroundResource(R.drawable.chat_background2);
                text.setTextColor(Color.BLACK);

            }
            text.setText(s);
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent = new Intent(SingleChattingActivity.this,NewClickOnMyCompanyActivity.class);
        intent.putExtra("companyId",companyId);
        startActivity(intent);
        finish();
    }
}
