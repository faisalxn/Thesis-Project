package com.example.user.companycommunity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.FileNameMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreatePollActivity extends AppCompatActivity {

    private String companyId ;
    EditText pollDescriptionText, option1Text ,option2Text, option3Text, option4Text , option5Text ;
    Button createPollButton;
    ImageButton addOptionButton ;
    int count = 0 ;
    private DatabaseReference df , notification ;
    String name ;
    Toolbar toolbar;
    ArrayList<String> strings;

    ProgressDialog loading ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        companyId = getIntent().getStringExtra("companyId");

        loading = new ProgressDialog(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar4);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Poll");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        pollDescriptionText = findViewById(R.id.pollDescriptionText);
        option1Text = findViewById(R.id.option1Text);
        option2Text = findViewById(R.id.option2Text);
        option3Text = findViewById(R.id.option3Text);
        option4Text = findViewById(R.id.option4Text);
        option5Text = findViewById(R.id.option5Text);

        createPollButton = (Button) findViewById(R.id.createPollButton);
        addOptionButton = (ImageButton) findViewById(R.id.addOptionButton);

        option3Text.setVisibility(View.INVISIBLE);
        option3Text.setEnabled(false);
        option4Text.setVisibility(View.INVISIBLE);
        option4Text.setEnabled(false);
        option5Text.setVisibility(View.INVISIBLE);
        option5Text.setEnabled(false);


        df = FirebaseDatabase.getInstance().getReference();


        final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        df.child("users").child(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        strings = new ArrayList<>();
        df.child("notifications").child(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strings.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    if(key.equals(uId)==false) {
                        strings.add(key);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count + 1 ;
                if(count==1){
                    option3Text.setVisibility(View.VISIBLE);
                    option3Text.setEnabled(true);
                }
                else if(count==2){
                    option4Text.setVisibility(View.VISIBLE);
                    option4Text.setEnabled(true);
                }
                else if(count==3){
                    option5Text.setVisibility(View.VISIBLE);
                    option5Text.setEnabled(true);
                }
            }
        });

        createPollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String des,text1,text2,text3,text4,text5;

                des = pollDescriptionText.getText().toString();
                text1 = option1Text.getText().toString();
                text2 = option2Text.getText().toString();

                text3 = text4 = text5 = "";

                //pollDescriptionText.setText("");
                //option1Text.setText("");
                //option2Text.setText("");

                if(count>=1){
                    text3 = option3Text.getText().toString();
                    //option3Text.setText("");

                }
                if(count>=2){
                    text4 = option4Text.getText().toString();
                    //option4Text.setText("");

                }
                if(count>=3){
                    text5 = option5Text.getText().toString();
                    //option5Text.setText("");

                }

                insert(des,text1,text2,text3,text4,text5);


            }
        });



    }

    public void insert(final String des,final String text1,final String text2,final String text3,final String text4,final String text5){
        //loading.setTitle("Creating Poll");
        //loading.setMessage("Please wait untill creating a new poll.");
        //loading.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference poll = df.child("poll").child(companyId);
                final String key = poll.push().getKey();
                DatabaseReference myDF = poll.child(key) ;
                int c = count + 2 ;

                Map<String , Object> poll_map = new HashMap<>();

                poll_map.put("des",des);
                poll_map.put("name",name);
                poll_map.put("count",c+"");

                poll_map.put("text1",text1);
                poll_map.put("text2",text2);
                poll_map.put("text3",text3);
                poll_map.put("text4",text4);
                poll_map.put("text5",text5);

                poll_map.put("textC1",0+"");
                poll_map.put("textC2",0+"");
                poll_map.put("textC3",0+"");
                poll_map.put("textC4",0+"");
                poll_map.put("textC5",0+"");


                myDF.updateChildren(poll_map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    notification = df.child("notifications").child(companyId);
                                    for(String s : strings){
                                        String key = notification.child(s).push().getKey();

                                        notification.child(s).child(key).child("name").setValue("A new poll has been created !");

                                    }


                                    Toast.makeText(getApplicationContext(),"Poll created",Toast.LENGTH_SHORT).show();

                                    pollDescriptionText.setText("");
                                    option1Text.setText("");
                                    option2Text.setText("");

                                    if(count>=1){
                                        //text3 = option3Text.getText().toString();
                                        option3Text.setText("");

                                    }
                                    if(count>=2){
                                        //text4 = option4Text.getText().toString();
                                        option4Text.setText("");

                                    }
                                    if(count>=3){
                                        //text5 = option5Text.getText().toString();
                                        option5Text.setText("");

                                    }



                                }
                                else {


                                }

                                //loading.dismiss();
                            }
                        });

            }
        });

        builder.setNegativeButton("no",null);

        builder.setTitle("Create Poll");
        builder.setMessage("Are you sure to create poll?");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();




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
        Intent intent = new Intent(CreatePollActivity.this,WelcomeActivity.class);

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
