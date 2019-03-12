package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationsFragment extends Fragment {

    private RecyclerView allNotificationsList;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private String uId;
    private String companyId;

    View view;

    @SuppressLint("ValidFragment")
    public NotificationsFragment(String companyId) {
        this.companyId = companyId;
    }

    public NotificationsFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications,null);

        //companyId = getIntent().getStringExtra("companyId");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();


        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("notifications").child(companyId).child(uId);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        allNotificationsList = (RecyclerView) view.findViewById(R.id.fragment_notifications_list);
        allNotificationsList.setHasFixedSize(true);
        allNotificationsList.setLayoutManager(mLayoutManager);

        return view;
    }


    @Override
    public void onStart() {
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
