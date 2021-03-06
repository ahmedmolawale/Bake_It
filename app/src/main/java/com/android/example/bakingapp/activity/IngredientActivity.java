package com.android.example.bakingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.example.bakingapp.R;
import com.android.example.bakingapp.adapter.IngredientAdapter;
import com.android.example.bakingapp.model.Ingredient;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.example.bakingapp.activity.RecipeActivity.RECIPE_NAME;

public class IngredientActivity extends AppCompatActivity {

    private static final String INGREDIENT_ID = "ingredient_id";
    private static final String INGREDIENT_ACTIVITY_TITLE = " Ingredients";
    @BindView(R.id.ingredient_recycler_view)
    RecyclerView ingredientRecyclerView;

    private IngredientAdapter ingredientAdapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_activity_layout);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();

        layoutManager = new LinearLayoutManager(this);
        ingredientRecyclerView.setLayoutManager(layoutManager);

        ingredientRecyclerView.setHasFixedSize(true);
        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                ingredientRecyclerView.getContext(),
                layoutManager.getOrientation());
        ingredientRecyclerView.addItemDecoration(dividerItemDecoration);

        if (savedInstanceState != null) {
            ingredients = savedInstanceState.getParcelableArrayList(INGREDIENT_ID);
            ingredientAdapter = new IngredientAdapter(ingredients);
            ingredientRecyclerView.setAdapter(ingredientAdapter);
            return;
        }
        String recipeName = "";
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeActivity.INGREDIENT_EXTRA)) {
            this.ingredients = intent.getParcelableArrayListExtra(RecipeActivity.INGREDIENT_EXTRA);
            recipeName = intent.getStringExtra(RECIPE_NAME);
            IngredientAdapter ingredientAdapter = new IngredientAdapter(this.ingredients);

            ingredientRecyclerView.setAdapter(ingredientAdapter);
        }
        if (actionBar != null) {
            actionBar.setTitle(recipeName + INGREDIENT_ACTIVITY_TITLE);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //to avoid any reload when orientation is changed
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(INGREDIENT_ID, ingredients);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        ingredients = savedInstanceState.getParcelableArrayList(INGREDIENT_ID);
        ingredientAdapter = new IngredientAdapter(ingredients);
        ingredientRecyclerView.setAdapter(ingredientAdapter);
    }
}
