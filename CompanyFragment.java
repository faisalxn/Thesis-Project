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
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyFragment extends Fragment {
    String companyId,about_company,admin_id,company_address,company_name,admin_name;
    View view;
    TextView about,address,admin,name ;
    Button company_pending_join_request_button,
            company_pending_leave_request_button,
            company_leaves_taken_by_employees_button,
            company_employee_list,
            company_printing_records;

    @SuppressLint("ValidFragment")
    public CompanyFragment(String companyId) {
        this.companyId = companyId;
    }

    public CompanyFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_company,null);

        about = (TextView) view.findViewById(R.id.company_about);
        address = (TextView) view.findViewById(R.id.company_address);
        admin = (TextView) view.findViewById(R.id.company_admin);
        name = (TextView) view.findViewById(R.id.fragment_companyName);


        company_pending_join_request_button = (Button) view.findViewById(R.id.company_pending_join_request_button);
        company_pending_leave_request_button = (Button) view.findViewById(R.id.company_pending_leave_request_button);
        company_leaves_taken_by_employees_button = (Button) view.findViewById(R.id.company_leaves_taken_by_employees_button);
        company_employee_list  = (Button) view.findViewById(R.id.company_employee_list);
        company_printing_records = (Button) view.findViewById(R.id.company_printing_records);

        company_pending_join_request_button.setVisibility(View.INVISIBLE);
        company_pending_leave_request_button.setVisibility(View.INVISIBLE);
        //company_leaves_taken_by_employees_button.setVisibility(View.INVISIBLE);
        //company_employee_list.setVisibility(View.INVISIBLE);


        final String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();




        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = databaseReference.child("company").child(companyId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                about_company = dataSnapshot.child("about_company").getValue().toString();
                admin_id = dataSnapshot.child("admin_id").getValue().toString();
                company_address = dataSnapshot.child("company_address").getValue().toString();
                company_name = dataSnapshot.child("company_name").getValue().toString();

                about.setText(about_company);
                address.setText(company_address);
                name.setText(company_name);

                DatabaseReference reference1 = databaseReference.child("users").child(admin_id);
                reference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        admin_name = dataSnapshot.child("name").getValue().toString();
                        admin.setText(admin_name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if(uId.equals(admin_id)==false){
                    company_pending_join_request_button.setVisibility(View.GONE);
                    company_pending_leave_request_button.setVisibility(View.GONE);
                    //company_leaves_taken_by_employees_button.setVisibility(View.GONE);
                    //company_employee_list.setVisibility(View.GONE);

                }
                else {
                    company_pending_join_request_button.setVisibility(View.VISIBLE);
                    company_pending_leave_request_button.setVisibility(View.VISIBLE);
                    company_leaves_taken_by_employees_button.setVisibility(View.VISIBLE);
                    company_employee_list.setVisibility(View.VISIBLE);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        company_pending_join_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PendingRequestsActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);
            }
        });
        company_pending_leave_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PendingLeaveActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);

            }
        });
        company_leaves_taken_by_employees_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AllPastLeavesActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);
            }
        });
        company_employee_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),EmployeeListActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);

            }
        });

        company_printing_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(),AllPastPrintActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);



            }
        });


        return view;
    }




}
