package com.example.ltapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.constraintlayout.widget.ConstraintLayout;

public class UsersFragment extends Fragment {

    private ConstraintLayout btBack;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // Ánh xạ btBack
        btBack = view.findViewById(R.id.btBack);
        
        // Xử lý sự kiện click cho btBack
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại fragment/activity trước đó
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        return view;
    }
} 