package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {
    View view ;
    TabLayout tabLayout;
    ViewPager viewPager ;
    FragmentManager fragmentManager;
    String companyId;

    @SuppressLint("ValidFragment")
    public HomeFragment(String companyId){
        this.companyId = companyId ;
    }

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home,null);

        tabLayout = (TabLayout) view.findViewById(R.id.home_tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.home_viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());


        viewPagerAdapter.AddFragment(new TabNews(companyId),"News");
        viewPagerAdapter.AddFragment(new TabEmployees(companyId),"Employees");
        viewPagerAdapter.AddFragment(new TabChats(companyId),"Chats");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        return view ;
    }


}
