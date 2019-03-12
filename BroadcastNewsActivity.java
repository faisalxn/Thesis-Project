package com.example.user.companycommunity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

public class BroadcastNewsActivity extends AppCompatActivity {

    RecyclerView newsList ;
    EditText newsText ;
    ImageButton sendNewsButton ;

    private DatabaseReference mUsersDatabase , mUsersDatabase2;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;

    String uId ;
    String companyId,name ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_news);

        newsList = (RecyclerView) findViewById(R.id.newsList);
        newsText = (EditText) findViewById(R.id.newsText);
        sendNewsButton = (ImageButton) findViewById(R.id.sendNewsButton);

        companyId = getIntent().getStringExtra("companyId");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();
        mLayoutManager = new LinearLayoutManager(this);
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


        sendNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final String key1 = database.getReference("").child("message").child(companyId).push().getKey();


                mUsersDatabase.child("message").child(companyId).child(key1).child("name").setValue(name)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    //String key2 = database.getReference("").child("message").child(companyId).push().getKey();

                                    mUsersDatabase.child("message").child(companyId).child(key1).child("news").setValue(newsText.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(BroadcastNewsActivity.this,"News Broadcasted successfully.",Toast.LENGTH_LONG).show();
                                                        
                                                    }
                                                    else {

                                                    }

                                                }
                                            });
                                }
                                else {

                                }
                            }
                        });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mUsersDatabase2 = mUsersDatabase.child("message").child(companyId);
        FirebaseRecyclerAdapter<News, NewsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<News, NewsViewHolder>(
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
