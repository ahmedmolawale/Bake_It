package com.android.example.bakingapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Step;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by root on 6/15/17.
 */

public class StepDescFragment extends Fragment {

    private static final String STEP = "step";
    private TextView shortDesc;
    private TextView description;
    private Step step;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recipe_step_desc,container,false);
        shortDesc = (TextView) view.findViewById(R.id.step_short_desc);
        description = (TextView) view.findViewById(R.id.step_long_desc);
        if(savedInstanceState != null){
            this.step = savedInstanceState.getParcelable(STEP);
        }
        return view;
    }

    public void setUpDetails() {
        //also set the short desc and desc
        shortDesc.setText(step.getShortDescription());
        description.setText(step.getDescription());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23) {
            setUpDetails();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //API level 24 and above support multi window...so its safe to set up here
        if (Util.SDK_INT > 23) {
            setUpDetails();
        }
    }

    public void setDescStep(Step step) {
        this.step = step;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STEP,this.step);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(Util.SDK_INT <=23){
            shortDesc = null;
            description = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23){
            shortDesc = null;
            description = null;
        }
    }
}
