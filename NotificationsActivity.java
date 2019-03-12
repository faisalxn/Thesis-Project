package com.example.user.companycommunity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsActivity extends AppCompatActivity {


    private RecyclerView allNotificationsList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private String uId;
    private String companyId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        companyId = getIntent().getStringExtra("companyId");


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(companyId).child(uId);


        mLayoutManager = new LinearLayoutManager(this);
        allNotificationsList = (RecyclerView) findViewById(R.id.allNotificationsList);
        allNotificationsList.setHasFixedSize(true);
        allNotificationsList.setLayoutManager(mLayoutManager);


    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Notification, AllNotificationsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Notification, AllNotificationsViewHolder>(
                        Notification.class,
                        R.layout.single_notification_layout,
                        AllNotificationsViewHolder.class,
                        mUsersDatabase
                ) {
                    @Override
                    protected void populateViewHolder(AllNotificationsViewHolder allNotificationsViewHolder, final Notification notification, int position) {
                        allNotificationsViewHolder.setName(notification.getName());
                    }
                };
        allNotificationsList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class AllNotificationsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public AllNotificationsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.single_notification_name);
            name.setText(s);
        }

    }




}
