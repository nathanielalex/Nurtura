package com.example.nurtura.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nurtura.ArticleActivity;
import com.example.nurtura.R;

public class HomeFragment extends Fragment {
    Button btnArticle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        btnArticle = view.findViewById(R.id.btnArticle);

        btnArticle.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ArticleActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
