package com.android.example.bakingapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.activity.RecipeActivity;
import com.android.example.bakingapp.adapter.StepsFragmentAdapter;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

/**
 * Created by root on 6/13/17.
 */


public class StepsFragement extends Fragment {

    private StepsFragmentAdapter.OnListItemClickListener onListItemClickListener;
    private RecyclerView stepsRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private StepsFragmentAdapter stepsFragmentAdapter;
    private ArrayList<Step> recipeSteps;
    public StepsFragement() {
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            onListItemClickListener = (StepsFragmentAdapter.OnListItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListItemClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recipe_steps, container, false);
        stepsRecyclerView = (RecyclerView) view.findViewById(R.id.steps_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        stepsRecyclerView.setLayoutManager(layoutManager);
        stepsRecyclerView.setHasFixedSize(true);
        Intent intent = getActivity().getIntent();

        if(intent != null && intent.hasExtra(RecipeActivity.STEP_EXTRA)){
            recipeSteps = intent.getParcelableArrayListExtra(RecipeActivity.STEP_EXTRA);
        }
        stepsFragmentAdapter = new StepsFragmentAdapter(getContext(),recipeSteps,onListItemClickListener);
        stepsRecyclerView.setAdapter(stepsFragmentAdapter);
        return view;
    }
}
