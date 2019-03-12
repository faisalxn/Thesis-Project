package com.example.user.companycommunity;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class FileTabsPagerAdapter extends FragmentPagerAdapter {
    String companyId;

    public FileTabsPagerAdapter(FragmentManager fm,String companyId) {
        super(fm);
        this.companyId = companyId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                UploadFragment uploadFragment = new UploadFragment(companyId);
                return uploadFragment;

            case 1:
                DownloadFragment downloadFragment = new DownloadFragment(companyId);
                return downloadFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Upload";
            case 1:
                return "Download";
            default:
                return null;
        }
    }
}
