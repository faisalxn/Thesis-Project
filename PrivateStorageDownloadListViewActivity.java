package com.example.user.companycommunity;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;

public class PrivateStorageDownloadListViewActivity extends AppCompatActivity {

    Toolbar toolbar ;
    RecyclerView private_all_files ;
    FirebaseAuth mAuth;
    String companyId,uId ;
    private DatabaseReference fileNameRef;
    StorageReference storageFileRef;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_storage_download_list_view);

        companyId = getIntent().getStringExtra("companyId");

        toolbar = (Toolbar) findViewById(R.id.private_download_toolbar);
        private_all_files = (RecyclerView) findViewById(R.id.private_all_files);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Private storage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        fileNameRef = FirebaseDatabase.getInstance().getReference().child("URLs").child(companyId).child(uId);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        private_all_files.setHasFixedSize(true);
        private_all_files.setLayoutManager(mLayoutManager);



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
            mAuth.signOut();
            logoutUser();
        }
        return true ;
    }
    private void logoutUser() {
        Intent intent = new Intent(PrivateStorageDownloadListViewActivity.this,WelcomeActivity.class);

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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<FileDrive,AllFilesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FileDrive, AllFilesViewHolder>(
                FileDrive.class,
                R.layout.single_file_view,
                AllFilesViewHolder.class,
                fileNameRef

        ) {
            @Override
            protected void populateViewHolder(AllFilesViewHolder viewHolder, FileDrive model, int position) {
                viewHolder.setFile_name(model.getFile_name());
                final String fileName = model.getFile_name();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile(fileName);
                    }
                });
            }
        };
        private_all_files.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllFilesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public AllFilesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setFile_name(String file_name) {
            TextView name = (TextView) mView.findViewById(R.id.single_file_name);
            name.setText(file_name);
        }
    }

    private void downloadFile(String fileName) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageFileRef = storageRef.child(companyId).child(uId).child(fileName);
        String directory = "project/private_files";
        File rootPath = new File(Environment.getExternalStorageDirectory(), directory);
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File localFile = new File(rootPath, fileName);
        storageFileRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //Log.e()
                Toast.makeText(PrivateStorageDownloadListViewActivity.this,
                        "File download complete!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PrivateStorageDownloadListViewActivity.this,
                        "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
