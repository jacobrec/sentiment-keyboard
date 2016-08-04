package com.chair49.sentimentkeyboard.views;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.chair49.sentimentkeyboard.R;

import static android.content.DialogInterface.*;

/**
 * Created by jacob on 02/08/16.
 */
public class MyInstructionFragment extends android.support.v4.app.Fragment implements IOnFocusListenable {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.instructionlayout, container, false);
    }

    Button enable;
    Button set;

    Button clear;
    EditText testtext;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enable = (Button) getView().findViewById(R.id.enable);
        set = (Button) getView().findViewById(R.id.set);

        testtext = (EditText) getView().findViewById(R.id.testtext);
        clear = (Button) getView().findViewById(R.id.clear);

        set.setEnabled(true);

        final AlertDialog.Builder en = new AlertDialog.Builder(getActivity())
                .setTitle("Enable Keyboard")
                .setMessage(getActivity().getString(R.string.enablemessage))
                .setNeutralButton("Okay", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));

                    }

                });
        final AlertDialog.Builder se = new AlertDialog.Builder(getActivity())
                .setTitle("Select Sentiment Keyboard")
                .setMessage(getActivity().getString(R.string.setmessage))
                .setNeutralButton("Okay", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imeManager = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                        imeManager.showInputMethodPicker();
                    }

                });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                se.show();
            }
        });
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                en.show();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testtext.setText("");
            }
        });
        manageButtonStates();


    }

    private void manageButtonStates() {

        if (checkIsEnabled()) {
            enable.getBackground().setColorFilter(Color.argb(255,200,230,201), PorterDuff.Mode.MULTIPLY);
            enable.setEnabled(false);
            set.setEnabled(true);
            if (checkIsDefault()) {
                set.getBackground().setColorFilter(Color.argb(255,200,230,201), PorterDuff.Mode.MULTIPLY);
                set.setEnabled(false);
            } else {
                set.getBackground().clearColorFilter();
            }
        } else {
            set.setEnabled(false);
            enable.setEnabled(true);
            set.getBackground().clearColorFilter();
            enable.getBackground().clearColorFilter();
        }

    }

    public boolean checkIsDefault() {
        String id = Settings.Secure.getString(
                getActivity().getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD
        );
        return (id.contains("sentimentkeyboard") && id.contains("chair49"));
    }

    public boolean checkIsEnabled() {
        InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
        String flattenedlist = im.getEnabledInputMethodList().toString();
        return (flattenedlist.contains("chair49") && flattenedlist.contains("sentimentkeyboard"));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        manageButtonStates();
    }
}

interface IOnFocusListenable {
    public void onWindowFocusChanged(boolean hasFocus);
}