package com.example.user.companycommunity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    String uId;

    EditText account_name_text,account_email_text,account_mobile_text,account_pass_text_1,account_pass_text_2;
    Button save_account_button;
    DatabaseReference reference,databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uId = user.getUid();


        account_name_text = (EditText) findViewById(R.id.account_name_text);
        account_email_text = (EditText) findViewById(R.id.account_email_text);
        account_mobile_text = (EditText) findViewById(R.id.account_mobile_text);
        account_pass_text_1 = (EditText) findViewById(R.id.account_pass_text_1);
        account_pass_text_2 = (EditText) findViewById(R.id.account_pass_text_2);

        save_account_button = (Button) findViewById(R.id.save_account_button);
        account_email_text.setEnabled(false);

        reference = FirebaseDatabase.getInstance().getReference();

        databaseReference = reference.child("users").child(uId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name , email , mobile ;

                name = dataSnapshot.child("name").getValue().toString();
                email = dataSnapshot.child("email").getValue().toString();
                mobile = dataSnapshot.child("mobile").getValue().toString();

                account_name_text.setText(name);
                account_email_text.setText(email);
                account_mobile_text.setText(mobile);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        save_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name,email,mobile,password1,password2;

                name = account_name_text.getText().toString();
                email = account_email_text.getText().toString();
                mobile = account_mobile_text.getText().toString();
                password1 = account_pass_text_1.getText().toString();
                password2 = account_pass_text_2.getText().toString();

                //if(password1.equals(password2)) {

                    change(name, email, mobile, password1, password2);

                //}else {
                  //  Toast.makeText(AccountActivity.this,"Password not matched",Toast.LENGTH_LONG).show();

                //}




            }
        });



    }

    private void change(String name,String email, String mobile, final String password1, final String password2) {
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password1) ){
            Toast.makeText(AccountActivity.this,"Provide data in all fields",Toast.LENGTH_LONG).show();
        }
        else {

            final AuthCredential credential = EmailAuthProvider.getCredential(email,password1);

            Map<String,Object> account_map = new HashMap<>();

            account_map.put("name",name);
            account_map.put("mobile",mobile);

            databaseReference.updateChildren(account_map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    user.updatePassword(password2)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){

                                                                    }
                                                                    else {
                                                                        Toast.makeText(AccountActivity.this,"" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else {
                                                    Toast.makeText(AccountActivity.this,"" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });
                            }
                            else {

                                Toast.makeText(AccountActivity.this,"" + task.getException().getMessage() ,Toast.LENGTH_LONG).show();

                            }

                        }
                    });


        }

    }
}
