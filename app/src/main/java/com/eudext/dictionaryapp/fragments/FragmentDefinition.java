package com.eudext.dictionaryapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eudext.dictionaryapp.R;
import com.eudext.dictionaryapp.WordMeaning;

public class FragmentDefinition extends Fragment {

    public FragmentDefinition() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_definition, container, false);

        Context context = getActivity();

        TextView textView = (TextView)view.findViewById(R.id.textViewId);

        String definition = ((WordMeaning)context).definition;

        if(definition==null) {
            textView.setText("No definition found");
        }

        textView.setText(definition);


        return view;


    }
}
