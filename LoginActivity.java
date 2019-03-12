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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText emailText,passwordText ;
    Button login;
    ProgressDialog loading ;
    String companyId;
    private FirebaseAuth mAuth;

    boolean flag ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailText = (EditText) findViewById(R.id.emailText);
        passwordText = (EditText) findViewById(R.id.passwordText);

        login = (Button) findViewById(R.id.signInButton);

        loading = new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                loginAccount(email,password);

            }
        });

    }

    public void loginAccount(String email, String password) {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Provide email and password",Toast.LENGTH_LONG).show();

        }
        else {

            loading.setTitle("Sign In");
            loading.setMessage("Please wait untill email and password is verified.");
            loading.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                //startActivity(intent);

                                flag = true;

                                String uId = mAuth.getCurrentUser().getUid();
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference databaseReference = reference.child("users").child(uId);

                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(flag){
                                                flag = false;
                                            } else {
                                                return;
                                            }


                                            String join_status = dataSnapshot.child("join_status").getValue().toString();

                                            if(dataSnapshot.hasChild("company_id")) {
                                                companyId = dataSnapshot.child("company_id").getValue().toString();
                                            }

                                            Intent intent;
                                            if (join_status.equals("nill") || join_status.equals("pending")  ){
                                                //Toast.makeText(LoginActivity.this,company_id,Toast.LENGTH_LONG).show();
                                                intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else  {
                                                intent = new Intent(LoginActivity.this, NewClickOnMyCompanyActivity.class);
                                                intent.putExtra("companyId",companyId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                            }



                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed "+task.getException(),
                                        Toast.LENGTH_SHORT).show();

                            }
                            loading.dismiss();
                            // ...
                        }
                    });
        }
    }
}
