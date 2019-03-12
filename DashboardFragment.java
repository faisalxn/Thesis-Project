package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

@SuppressLint("ValidFragment")
public class DashboardFragment extends Fragment {
    String companyId,name;

    @SuppressLint("ValidFragment")
    public DashboardFragment(String companyId){
        this.companyId = companyId ;
    }

    public DashboardFragment(){}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,null);

        final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference df = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = df.child("users").child(uId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ImageView poll_imageView = (ImageView) view.findViewById(R.id.poll_imageView);
        poll_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Poll",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),PollsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        ImageView leave_imageView = (ImageView) view.findViewById(R.id.leave_imageView);

        leave_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Poll",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),TakeLeaveActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        ImageView location_imageView = (ImageView) view.findViewById(R.id.location_imageView);

        location_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Poll",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),LocationMapActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        ImageView video_call_imageView = (ImageView) view.findViewById(R.id.video_call_imageView);

        video_call_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(),"Poll",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),FireVideo.class);
                intent.putExtra("companyId",companyId);
                intent.putExtra("name",name);
                intent.putExtra("uId",uId);

                startActivity(intent);


            }
        });


        ImageView file_imageView = (ImageView) view.findViewById(R.id.file_imageView);

        file_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),FileActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);



            }
        });



        ImageView print_imageView = (ImageView) view.findViewById(R.id.print_imageView);

        print_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PrintOrderActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        return view ;
    }


}
