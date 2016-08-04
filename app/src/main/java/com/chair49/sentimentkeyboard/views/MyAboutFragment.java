package com.chair49.sentimentkeyboard.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chair49.sentimentkeyboard.R;

/**
 * Created by jacob on 02/08/16.
 */
public class MyAboutFragment extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.aboutlayout, container, false);
    }
}
