package com.eudext.dictionaryapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eudext.dictionaryapp.R;
import com.eudext.dictionaryapp.WordMeaning;

public class FragmentWordType extends Fragment {

    public FragmentWordType() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wordtype, container, false);

        Context context = getActivity();

        TextView textView = (TextView)view.findViewById(R.id.textViewId);

        String wordtype = ((WordMeaning)context).wordType;

        if(wordtype==null) {
            textView.setText("No wordtype");
        }

        textView.setText(wordtype);


        return view;


    }
}
