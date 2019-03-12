package com.example.user.companycommunity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;


public class PrintOrderActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText print_order_pages,print_order_payment,print_order_total_payment;
    Button print_order_button ;
    String uId, companyId, name ;
    DatabaseReference databaseReference, reference, reference2 ,reference3, reference4;
    int pages,payment;
    boolean flag = true ;
    ArrayList<String> strings = new ArrayList<>();
    ImageView QR_code_scan_button , QR_code_imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_order);

        companyId = getIntent().getStringExtra("companyId");

        toolbar = (Toolbar) findViewById(R.id.print_order_toolbar);


        print_order_pages = (EditText) findViewById(R.id.print_order_pages);
        print_order_payment = (EditText) findViewById(R.id.print_order_payment);
        print_order_total_payment = (EditText) findViewById(R.id.print_order_total_payment);
        print_order_button = (Button) findViewById(R.id.print_order_button);
        QR_code_scan_button = (ImageView) findViewById(R.id.QR_code_scan_button);
        QR_code_imageView = (ImageView) findViewById(R.id.QR_code_imageView);

        print_order_payment.setEnabled(false);
        print_order_total_payment.setEnabled(false);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Printing Order");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        reference = databaseReference.child("prints").child(companyId).child(uId);
        reference2 = databaseReference.child("users").child(uId);
        reference3 = databaseReference.child("notifications").child(companyId);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("pages") && dataSnapshot.hasChild("payment")  ) {
                    String t = dataSnapshot.child("pages").getValue().toString();
                    pages = Integer.parseInt(t);
                    String t2 = dataSnapshot.child("payment").getValue().toString();
                    payment = Integer.parseInt(t2);

                } else {
                    pages = 0 ;
                    payment = 0 ;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reference4 = reference3;
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strings.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    //Toast.makeText(PrintOrderActivity.this,key,Toast.LENGTH_SHORT).show();
                    strings.add(key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        QR_code_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PrintOrderActivity.this,QRCodeScanActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);

            }
        });

        print_order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int p = Integer.parseInt( print_order_pages.getText().toString());
                int result = pages + p ;
                int current_bill = 0 ;
                int threshold = 500;
                int total_bill = 0 ;

                if(pages>threshold){
                    current_bill = result - pages;
                    current_bill = current_bill/2;
                    total_bill = current_bill + payment ;

                }
                else if(result>threshold){
                    current_bill = result - threshold;
                    current_bill = current_bill/2;
                    total_bill = current_bill + payment ;

                }


                print_order_payment.setText(current_bill+"");
                print_order_total_payment.setText(total_bill+"");

                String text = print_order_pages.getText().toString().trim() + " " + uId ;

                if(text!=null && text.equals("")==false){

                    try {
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        BitMatrix bitMatrix = null;
                        bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 500,500);

                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                        QR_code_imageView.setImageBitmap(bitmap);


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }

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
        Intent intent = new Intent(PrintOrderActivity.this,WelcomeActivity.class);

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
