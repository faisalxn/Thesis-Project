package com.example.user.companycommunity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LeaveManagementActivity extends AppCompatActivity {

    Button pendingLeaveRequestsButton,allLeavesButton ;
    private String companyId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_management);

        companyId = getIntent().getStringExtra("companyId");



        pendingLeaveRequestsButton = (Button) findViewById(R.id.pendingLeaveRequestsButton);
        pendingLeaveRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaveManagementActivity.this,PendingLeaveActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


        allLeavesButton = (Button) findViewById(R.id.allLeavesButton);
        allLeavesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaveManagementActivity.this,AllPastLeavesActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);


            }
        });


    }



}
