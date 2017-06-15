package com.android.example.bakingapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

/**
 * Created by root on 6/15/17.
 */

public class StepNavFragment extends Fragment {

    private static final String STEPS = "steps";
    private static final String PREV_POSITION = "prev_position";
    private static final String NEXT_POSITION = "next_position";
    private static final String POSITION = "position";
    private Button prev;
    private Button next;
    private ArrayList<Step> steps;
    private int position;
    private int prevPositionTemp;
    private int nextPositionTemp;
    private OnNavButtonClickListener onNavButtonClickListener;

    public interface OnNavButtonClickListener {
        void onPrevButtonClickListener(int currentPosition);

        void onNextButtonClickListener(int currentPosition);
    }


    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onNavButtonClickListener = (OnNavButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recipe_step_nav, container, false);
        prev = (Button) view.findViewById(R.id.step_prev);
        next = (Button) view.findViewById(R.id.step_next);

        if(savedInstanceState != null){
            steps = savedInstanceState.getParcelableArrayList(STEPS);
            position = savedInstanceState.getInt(POSITION);
            prevPositionTemp = savedInstanceState.getInt(PREV_POSITION);
            nextPositionTemp = savedInstanceState.getInt(NEXT_POSITION);
        }
        setUpButtons(prevPositionTemp, nextPositionTemp);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavButtonClickListener.onPrevButtonClickListener(position);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavButtonClickListener.onNextButtonClickListener(position);
            }
        });
        return view;
    }


    public void setUpButtons(int prev, int next) {
        try {
            steps.get(prev);
            this.prev.setEnabled(true);
        } catch (IndexOutOfBoundsException e) {
            this.prev.setEnabled(false);
        }

        try {
            this.steps.get(next);
            this.next.setEnabled(true);
        } catch (IndexOutOfBoundsException e) {
            this.next.setEnabled(false);
        }
    }

    public void setUpNav(ArrayList<Step> steps, int position) {
        this.position = position;
        this.steps = steps;
        prevPositionTemp = position - 1;
        nextPositionTemp = position + 1;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEPS,this.steps);
        outState.putInt(POSITION,position);
        outState.putInt(PREV_POSITION,prevPositionTemp);
        outState.putInt(NEXT_POSITION,nextPositionTemp);
    }
}
