package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabNews extends Fragment {
    View view ;
    RecyclerView newsList ;
    EditText newsText ;
    ImageButton sendNewsButton ;
    ProgressDialog progressDialog;
    private DatabaseReference mUsersDatabase , mUsersDatabase2 , notification ;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    ArrayList<String> strings;
    String uId ;
    String companyId,name ;

    @SuppressLint("ValidFragment")
    public TabNews(String companyId) {
        this.companyId = companyId ;
    }

    public TabNews() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_news,container,false);

        progressDialog = new ProgressDialog(getContext());
        newsList = (RecyclerView) view.findViewById(R.id.tab_newsList);
        newsText = (EditText) view.findViewById(R.id.tab_newsText);
        sendNewsButton = (ImageButton) view.findViewById(R.id.tab_newsSendButton);

        //companyId = getIntent().getStringExtra("companyId");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager.setReverseLayout(true);

        newsList.setHasFixedSize(true);
        newsList.setLayoutManager(mLayoutManager);

        mUsersDatabase.child("users").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        strings = new ArrayList<>();
        mUsersDatabase.child("notifications").child(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strings.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();

                    strings.add(key);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        sendNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //progressDialog.setTitle("News Broadcast");
                //progressDialog.setMessage("Please wait until news is broadcasted.");
                //progressDialog.show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure to broacast message?");
                builder.setTitle("News Broadcast");

                builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String key1 = mUsersDatabase.child("message").child(companyId).push().getKey();

                        Map<String,Object> news_map = new HashMap<>();

                        news_map.put("name",name);
                        news_map.put("news",newsText.getText().toString());


                        mUsersDatabase.child("message").child(companyId).child(key1).updateChildren(news_map)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            notification = mUsersDatabase.child("notifications").child(companyId);
                                            for(String s : strings){
                                                String key = notification.child(s).push().getKey();
                                                notification.child(s).child(key).child("name").setValue("A news has been broadcasted");

                                            }

                                            Toast.makeText(getContext(),"News Broadcasted successfully.",Toast.LENGTH_LONG).show();
                                            newsText.setText("");
                                            //progressDialog.dismiss();
                                        }
                                        else {
                                            //progressDialog.dismiss();
                                        }

                                    }


                                });

                    }
                });


                builder.setNegativeButton("no",null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();





            }
        });
        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();

        mUsersDatabase2 = mUsersDatabase.child("message").child(companyId);
        final FirebaseRecyclerAdapter<News, NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(
                News.class,
                R.layout.single_news_layout,
                NewsViewHolder.class,
                mUsersDatabase2
        ) {
            @Override
            protected void populateViewHolder(NewsViewHolder newsViewHolder, News news, int position) {
                newsViewHolder.setName(news.getName());
                newsViewHolder.setNews(news.getNews());
            }
        };
        newsList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public NewsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.newsNameText);
            name.setText(s);
        }
        public void setNews(String s){
            TextView news = (TextView) mView.findViewById(R.id.newsText);
            news.setText(s);
        }
    }
}
