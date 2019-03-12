package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class TabEmployees extends Fragment {
    View view ;
    RecyclerView employeeList ;

    private DatabaseReference mUsersDatabase ;
    private LinearLayoutManager mLayoutManager;
    private FirebaseAuth mAuth;

    String uId ;
    String companyId,name ;

    @SuppressLint("ValidFragment")
    public TabEmployees(String companyId) {
        this.companyId = companyId ;
    }

    public TabEmployees() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tab_employees,container,false);

        employeeList = (RecyclerView) view.findViewById(R.id.tab_employeesList);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        uId = user.getUid();

        mUsersDatabase = FirebaseDatabase.getInstance().getReference();

        mLayoutManager = new LinearLayoutManager(getContext());
        employeeList.setHasFixedSize(true);
        employeeList.setLayoutManager(mLayoutManager);


        return view ;
    }



    @Override
    public void onStart() {
        super.onStart();
        Query query = mUsersDatabase.child("request").child(companyId).orderByChild("join_status").equalTo("accepted");

        mUsersDatabase = mUsersDatabase.child("message").child(companyId);
        FirebaseRecyclerAdapter<Employee, EmployeesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Employee, EmployeesViewHolder>(
                Employee.class,
                R.layout.single_employee_layout,
                EmployeesViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(EmployeesViewHolder employeesViewHolder, final Employee employee, int position) {
                employeesViewHolder.setName(employee.getName());

                final String userId = getRef(position).getKey();

                employeesViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getContext(),SingleChattingActivity.class);
                        intent.putExtra("companyId",companyId);
                        intent.putExtra("userId",userId);
                        intent.putExtra("name",employee.getName());
                        startActivity(intent);
                        ((NewClickOnMyCompanyActivity)getActivity()).finish();

                    }
                });


            }
        };
        employeeList.setAdapter(firebaseRecyclerAdapter);


    }



    public static class EmployeesViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public EmployeesViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }
        public void setName(String s){
            TextView name = (TextView) mView.findViewById(R.id.employeesNameText);
            name.setText(s);

        }

    }


}
