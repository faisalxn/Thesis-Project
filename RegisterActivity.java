package com.example.user.companycommunity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText name , email , passsword , confirmPassword , mobile_no ;
    Button signUp ;

    ProgressDialog loading;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.nameText);
        email = (EditText) findViewById(R.id.emailText);
        passsword = (EditText) findViewById(R.id.passwordText);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordText);
        mobile_no = (EditText) findViewById(R.id.mobileText);


        signUp = (Button) findViewById(R.id.signUpButton);

        loading = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n,e,m,p,cP ;

                n = name.getText().toString();
                e = email.getText().toString();
                m = mobile_no.getText().toString();
                p = passsword.getText().toString();
                cP = confirmPassword.getText().toString();

                if(p.equals(cP)){
                    signUp(n,e,m,p);

                }
                else{

                    Toast.makeText(RegisterActivity.this,"Password not matched",Toast.LENGTH_LONG).show();

                }
            }
        });



    }

    public void signUp(final String name , final String email , final String mobile , String password ){
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(password) ){
            Toast.makeText(RegisterActivity.this,"Provide data in all fields",Toast.LENGTH_LONG).show();
        }
        else{
            loading.setTitle("Sign Up");
            loading.setMessage("Please wait untill saving all information is complete.");
            loading.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uId = user.getUid();
                                //updateUI(user);


                                // Write a message to the database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("").child("users").child(uId);

                                myRef.child("name").setValue(name);
                                //myRef.child("company_id").setValue("nill");
                                myRef.child("email").setValue(email);
                                myRef.child("status").setValue("I am using this network application.");
                                myRef.child("image").setValue("Default image");
                                myRef.child("thumb_image").setValue("Default thumb image");
                                myRef.child("join_status").setValue("nill");
                                myRef.child("mobile").setValue(mobile)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                                else{
                                                    Toast.makeText(RegisterActivity.this,"Error ocured in saving information",Toast.LENGTH_LONG).show();

                                                }

                                            }
                                        });


                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, ""+ task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                            loading.dismiss();
                            // ...
                        }
                    });

        }

    }




}
