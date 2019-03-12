package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.google.firebase.database.Query;

public class TabChats extends Fragment {
    View view ;
    RecyclerView chat_history_list ;

    private DatabaseReference mUsersDatabase ;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;

    String uId ;
    String companyId,name ;


    @SuppressLint("ValidFragment")
    public TabChats(String companyId) {
        this.companyId = companyId ;
    }

    public TabChats() {
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_chats,container,false);

        chat_history_list = (RecyclerView) view.findViewById(R.id.chat_history_list);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        chat_history_list.setHasFixedSize(true);
        chat_history_list.setLayoutManager(mLayoutManager);



        return view ;
    }


    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference df = mUsersDatabase.child("history").child(companyId).child(uId);

        FirebaseRecyclerAdapter<History, ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<History, ChatsViewHolder>(
                History.class,
                R.layout.single_news_layout,
                ChatsViewHolder.class,
                df
        ) {
            @Override
            protected void populateViewHolder(ChatsViewHolder chatsViewHolder, final History history, int position) {
                chatsViewHolder.setName(history.getName());
                chatsViewHolder.setText(history.getText());

                final String userId = history.getId();

                chatsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(),SingleChattingActivity.class);
                        intent.putExtra("companyId",companyId);
                        intent.putExtra("userId",userId);
                        intent.putExtra("name",history.getName());
                        startActivity(intent);
                        ((NewClickOnMyCompanyActivity)getActivity()).finish();

                    }
                });


            }
        };
        chat_history_list.setAdapter(firebaseRecyclerAdapter);
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.newsNameText);
            name.setText(s);
        }

        public void setText(String s){
            TextView text = (TextView) mView.findViewById(R.id.newsText);
            text.setText(s);
        }



    }

}
