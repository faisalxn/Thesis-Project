package com.example.user.companycommunity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DownloadFragment extends Fragment {
    String companyId;
    View view;
    ImageView download_public_imageView,download_private_imageView;

    public DownloadFragment() {
    }

    @SuppressLint("ValidFragment")
    public DownloadFragment(String companyId) {
        this.companyId = companyId;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_download, container, false);

        download_public_imageView = view.findViewById(R.id.download_public_imageView);
        download_private_imageView = view.findViewById(R.id.download_private_imageView);

        download_public_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PublicStorageDownloadListViewActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);
                
            }
        });


        download_private_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),PrivateStorageDownloadListViewActivity.class);
                intent.putExtra("companyId",companyId);
                startActivity(intent);



            }
        });


        return view;
    }
}
