package com.example.user.companycommunity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRCodeScanActivity extends AppCompatActivity {

    SurfaceView cameraPreview;
    TextView txtResult;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    DatabaseReference reference , userName ;
    ImageView done_imageView ;

    String companyId;
    String name ;
    String page ;
    String id ;
    boolean flag ;
    DatabaseReference printrReference,databaseReference;
    int pages,payment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scan);

        companyId = getIntent().getStringExtra("companyId");

        reference = FirebaseDatabase.getInstance().getReference();
        userName = reference.child("users");
        flag = false ;

        done_imageView = (ImageView) findViewById(R.id.done_imageView);
        cameraPreview = (SurfaceView) findViewById(R.id.surfaceView);
        txtResult = (TextView) findViewById(R.id.QR_code_result);


        printrReference = reference.child("prints").child(companyId);


        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();


        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    txtResult.post(new Runnable() {
                        @Override
                        public void run() {

                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(2000);
                            String result = qrcodes.valueAt(0).displayValue ;
                            String ar[] = result.split(" ");
                            page = ar[0];
                            id = ar[1] ;

                            id = id.trim();
                            page = page.trim();


                            userName.child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    name = dataSnapshot.child("name").getValue().toString();
                                    txtResult.setText(name + " : " + page + " pages"  );
                                    flag = true ;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            printrReference.child(id).addValueEventListener(new ValueEventListener() {
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

                            barcodeDetector.release();
                        }
                    });
                }
            }
        });


        done_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag){

                    AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeScanActivity.this);

                    builder.setTitle("Printing")
                            .setMessage("Are you sure to print "+page+" pages for "+name+" ?" )
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    final int p = Integer.parseInt(page);

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

                                    Map<String,Object> map = new HashMap<>();
                                    map.put("pages",result+"");
                                    map.put("payment",total_bill+"");
                                    map.put("name",name);


                                    printrReference.child(id).setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(QRCodeScanActivity.this,"Print order successfull",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            });



                                }
                            })
                            .setNegativeButton("no",null);

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }


            }
        });


    }


}
