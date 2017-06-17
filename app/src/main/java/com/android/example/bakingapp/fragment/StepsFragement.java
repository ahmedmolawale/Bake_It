package com.android.example.bakingapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.activity.RecipeActivity;
import com.android.example.bakingapp.adapter.StepsFragmentAdapter;
import com.android.example.bakingapp.model.Ingredient;
import com.android.example.bakingapp.model.Step;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class StepsFragement extends Fragment {

    private static final String STEPS = "steps";
    private static final String INGREDIENTS = "ingredients";
    private StepsFragmentAdapter.OnListItemClickListener onListItemClickListener;
    @BindView(R.id.steps_recycler_view)
     RecyclerView stepsRecyclerView;
    private LinearLayoutManager layoutManager;
    private StepsFragmentAdapter stepsFragmentAdapter;
    private ArrayList<Step> recipeSteps;
    private ArrayList<Ingredient> recipeIngredients;
    @BindView(R.id.ingredient_list_on_details)
     TextView textView;
    private Unbinder unbinder;

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
        unbinder = ButterKnife.bind(StepsFragement.this,view);

        layoutManager = new LinearLayoutManager(getContext());
        stepsRecyclerView.setLayoutManager(layoutManager);
        stepsRecyclerView.setHasFixedSize(true);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(RecipeActivity.STEP_EXTRA)) {
            recipeSteps = intent.getParcelableArrayListExtra(RecipeActivity.STEP_EXTRA);
        }
        if (intent != null && intent.hasExtra(RecipeActivity.INGREDIENT_EXTRA)) {
            recipeIngredients = intent.getParcelableArrayListExtra(RecipeActivity.INGREDIENT_EXTRA);
        }

        if (savedInstanceState != null) {
            recipeSteps = savedInstanceState.getParcelableArrayList(STEPS);
            recipeIngredients = savedInstanceState.getParcelableArrayList(INGREDIENTS);
        }
        //build the ingredient list here
        StringBuilder ingredientBuilder = new StringBuilder();
        for (Ingredient ingredient : recipeIngredients) {
            String name = ingredient.getIngredient();
            String measure = ingredient.getMeasure();
            String quantity = String.valueOf(ingredient.getQuantity());

            String details = name.concat(" (").concat(quantity).concat(" ").concat(measure).concat(")");
            ingredientBuilder.append(details);
            ingredientBuilder.append("\n");
        }
        textView.setText(ingredientBuilder.toString());
        stepsFragmentAdapter = new StepsFragmentAdapter(getContext(), recipeSteps, onListItemClickListener);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                stepsRecyclerView.getContext(),
                layoutManager.getOrientation());
        stepsRecyclerView.addItemDecoration(dividerItemDecoration);
        stepsRecyclerView.setAdapter(stepsFragmentAdapter);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STEPS, recipeSteps);
        outState.putParcelableArrayList(INGREDIENTS, recipeIngredients);

    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
